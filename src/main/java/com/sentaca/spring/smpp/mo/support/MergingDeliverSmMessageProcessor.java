/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo.support;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.OptionalParameter.OctetString;
import org.jsmpp.bean.OptionalParameter.Tag;
import org.smslib.Message.MessageEncodings;
import org.smslib.Message.MessageTypes;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;

/**
 * This is {@link DeliverSmMessageProcessor} which uses shared synchronized Map
 * (via {@link ThreadSafeHashMapFactoryBean} to handle fragmented messages which
 * might be delivered (via DELIVER_SM) from different SMSCs.
 * 
 * Note: it depends on operator architecture but one of our customers setup
 * consisted of two SMSCs which our application had to handle. It was perfectly
 * normal scenario (in case of our customer) that message fragments has been
 * delivered via different SMSCs (and BINDs).
 * 
 * @author hamster
 * 
 */
public abstract class MergingDeliverSmMessageProcessor extends DeliverSmMessageProcessor {

  private static final Log logger = LogFactory.getLog(MergingDeliverSmMessageProcessor.class);

  private Map<String, MOMessage[]> mpMsgMap;

  public MergingDeliverSmMessageProcessor(Map<String, MOMessage[]> mpMsgMap) {
    this.mpMsgMap = mpMsgMap;
  }

  /**
   * Process inbound message - single or multipart.
   * 
   * @param deliverSm
   */
  @Override
  public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
    // TODO refactor it as it looks like shit :)
    MOMessage inboundMessageToUse = inboundMessage;

    // check if this is part of multipart message
    short msgRefNo = 0;
    byte msgMaxNum = 0;
    byte msgSeqNum = 0;
    if (deliverSm.getOptionalParametes() != null) {
      for (OptionalParameter param : deliverSm.getOptionalParametes()) {
        switch (Tag.valueOf(param.tag)) {
        case SAR_MSG_REF_NUM:
          msgRefNo = ((OptionalParameter.Short) param).getValue();
          break;
        case SAR_TOTAL_SEGMENTS:
          msgMaxNum = ((OptionalParameter.Byte) param).getValue();
          break;
        case SAR_SEGMENT_SEQNUM:
          msgSeqNum = ((OptionalParameter.Byte) param).getValue();
          break;
        case MESSAGE_PAYLOAD:
          OctetString messagePayload = ((OptionalParameter.OctetString) param);
          try {
            inboundMessageToUse.setText(new String(messagePayload.getValue(), "UTF-8"));
          } catch (UnsupportedEncodingException e) {
            inboundMessageToUse.setText(messagePayload.getValueAsString());
          }
          break;
        default:
          break;
        }
      }
    }

    logger.info("Segmented message: msgRefNo:" + msgRefNo + ", msgSeqNum:" + msgSeqNum + ", msgMaxNum:" + msgMaxNum);

    if ((msgMaxNum != 0) && (msgSeqNum != 0) && (msgRefNo != 0)) {
      final String messageKey = String.format("%s:%s:%s", inboundMessageToUse.getOriginator(), msgRefNo, inboundMessageToUse.getDestAddress());

      boolean allPartAvailable = true;

      synchronized (mpMsgMap) {
        // add to parts-list
        if (mpMsgMap.containsKey(messageKey)) {
          mpMsgMap.get(messageKey)[msgSeqNum - 1] = inboundMessageToUse;
        } else {
          MOMessage[] msgs = new MOMessage[msgMaxNum];
          msgs[msgSeqNum - 1] = inboundMessageToUse;
          mpMsgMap.put(messageKey, msgs);
        }

        for (int i = 0; i < mpMsgMap.get(messageKey).length; i++) {
          if (mpMsgMap.get(messageKey)[i] == null) {
            allPartAvailable = false;
            break;
          }
        }
      }

      // if
      if (allPartAvailable) {
        // compose multipart message and process message further
        String text = "";
        for (MOMessage partMessage : mpMsgMap.get(messageKey)) {
          text += partMessage.getText();
        }

        // FIXME - first part of sms determinates encoding... is it correct
        // behaviour?
        MessageEncodings encoding = mpMsgMap.get(messageKey)[0].getEncoding();
        inboundMessageToUse = new MOMessage(inboundMessageToUse.getOriginator(), inboundMessageToUse.getDestAddress(), text, encoding);

        mpMsgMap.remove(messageKey);
      } else {
        // there is nothing to process further - wait for another part of
        // multipart message
        return;
      }
    }

    this.jsmppGateway.incInboundMessageCount();

    processInboundMessageNotification(MessageTypes.INBOUND, inboundMessageToUse);
  }

  /**
   * This method is invoked:
   * <ul>
   * <li>for each non-fragmented message</li>
   * <li>for each concatenated message</li>
   * </ul>
   * 
   * Here's example:
   * 
   * <pre>
   *   DELIVER_SM part 1 of 3 -> method NOT invoked
   *   DELIVER_SM part 3 of 3 -> method NOT invoked
   *   DELIVER_SM not fragmented -> processInboundMessageNotification invoked
   *   DELIVER_SM part 2 o 3 -> processInboundMessageNotification invoked, inboundMessage.getText returns concatenation of text from each fragment.
   * </pre>
   * 
   * TODO we need binary support... SMPP is flexible but this is not what
   * programmers need (however telco world is very happy about this flexibility)
   * 
   * @param msgType
   * @param inboundMessage
   */
  public abstract void processInboundMessageNotification(MessageTypes msgType, MOMessage inboundMessage);

}
