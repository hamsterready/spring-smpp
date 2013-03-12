package com.sentaca.spring.smpp;

import java.util.Map;

import org.smslib.Message.MessageTypes;

import com.sentaca.spring.smpp.mo.MOMessage;
import com.sentaca.spring.smpp.mo.support.MergingDeliverSmMessageProcessor;

public class MyMergingMessageProcessor extends MergingDeliverSmMessageProcessor {

  public MyMergingMessageProcessor(Map<String, MOMessage[]> mpMsgMap) {
    super(mpMsgMap);
  }

  @Override
  public void processInboundMessageNotification(MessageTypes msgType, MOMessage inboundMessage) {
    System.out.println("GOT YOU MSG: " + inboundMessage);
  }

}
