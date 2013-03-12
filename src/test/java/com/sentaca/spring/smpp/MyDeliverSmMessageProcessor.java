package com.sentaca.spring.smpp;

import org.jsmpp.bean.DeliverSm;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;

public class MyDeliverSmMessageProcessor extends DeliverSmMessageProcessor {

  @Override
  public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
    System.out.println("Got message: " + inboundMessage);
  }

}
