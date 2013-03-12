/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.bean.MessageType;
import org.smslib.Message.MessageEncodings;
import org.springframework.beans.factory.BeanInitializationException;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;

public abstract class DeliverSmMessageProcessor {

  protected JSMPPGateway jsmppGateway;
  private boolean alreadyCalled = false;

  public abstract void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage);

  // TODO support async here...
  // @Async
  public final void processMessage(DeliverSm deliverSm) {
    if (MessageType.SMSC_DEL_RECEIPT.containedIn(deliverSm.getEsmClass())) {
      processStatusReportMessage(deliverSm);
    } else {
      processInboundMessage(deliverSm, convertDeliverSmToInboundMessage(deliverSm));
      jsmppGateway.incInboundMessageCount();
    }
  }

  /**
   * Process single inbound message (or part of multipart message).
   * 
   * @param deliverSm
   * @return
   */
  private MOMessage convertDeliverSmToInboundMessage(DeliverSm deliverSm) {
    byte[] shortMessage = deliverSm.getShortMessage();
    String text = "";
    MessageEncodings messageEncoding;
    if (Alphabet.ALPHA_DEFAULT.value() == deliverSm.getDataCoding()) {
      messageEncoding = MessageEncodings.ENC7BIT;
      text = StringHelpers.decode7bit(shortMessage);
    } else if (Alphabet.ALPHA_8_BIT.value() == deliverSm.getDataCoding()) {
      messageEncoding = MessageEncodings.ENC8BIT;
      text = StringHelpers.decode8bit(shortMessage);
    } else if (Alphabet.ALPHA_UCS2.value() == deliverSm.getDataCoding()) {
      messageEncoding = MessageEncodings.ENCUCS2;
      text = StringHelpers.decodeUCS2(shortMessage);
    } else {
      messageEncoding = MessageEncodings.ENCCUSTOM;
      text = new String(shortMessage);
    }

    return new MOMessage(deliverSm.getSourceAddr(), deliverSm.getDestAddress(), text, messageEncoding);
  }

  public void processStatusReportMessage(DeliverSm deliverSm) {
  }

  public final void setJsmppGateway(JSMPPGateway jsmppGateway) {
    if (alreadyCalled) {
      throw new BeanInitializationException(
          "Single instance of DeliverSmMessageProcessor can be used only by one JSMPPGateway. See MergingDeliverSmMessageProcessor and shared in JVM-memory thread-safe map to handle communication between instances.");
    }
    this.jsmppGateway = jsmppGateway;
    alreadyCalled = true;
  }
}
