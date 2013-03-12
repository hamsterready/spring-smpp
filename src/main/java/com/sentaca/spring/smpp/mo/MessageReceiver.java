/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import org.jsmpp.bean.AlertNotification;
import org.jsmpp.bean.DataSm;
import org.jsmpp.bean.DeliverSm;
import org.jsmpp.extra.ProcessRequestException;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.MessageReceiverListener;
import org.jsmpp.session.Session;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;

public class MessageReceiver implements MessageReceiverListener {

  private DeliverSmMessageProcessor deliverSmMessageProcessor;

  private DataSmMessageProcessor dataSmMessageProcessor;

  private AlertNotificationMessageProcessor alertNotificationMessageProcessor;

  public MessageReceiver(DeliverSmMessageProcessor deliverSmMessageProcessor) {
    this.deliverSmMessageProcessor = deliverSmMessageProcessor;
  }

  public MessageReceiver() {
    // -- default
  }

  // Circular dependency hack.
  public void init(JSMPPGateway jsmppGateway) {
    if (deliverSmMessageProcessor != null) {
      deliverSmMessageProcessor.setJsmppGateway(jsmppGateway);
    }
  }

  public void onAcceptAlertNotification(AlertNotification alertNotification) {
    if (alertNotificationMessageProcessor != null) {
      alertNotificationMessageProcessor.processMessage(alertNotification);
    }
  }

  public DataSmResult onAcceptDataSm(DataSm dataSm, Session source) throws ProcessRequestException {
    if (dataSmMessageProcessor != null) {
      return dataSmMessageProcessor.processMessage(dataSm, source);
    }
    return null;
  }

  public void onAcceptDeliverSm(DeliverSm deliverSm) throws ProcessRequestException {
    if (deliverSmMessageProcessor != null) {
      deliverSmMessageProcessor.processMessage(deliverSm);
    }
  }

  public void setAlertNotificationMessageProcessor(AlertNotificationMessageProcessor alertNotificationMessageProcessor) {
    this.alertNotificationMessageProcessor = alertNotificationMessageProcessor;
  }

  public void setDataSmMessageProcessor(DataSmMessageProcessor dataSmMessageProcessor) {
    this.dataSmMessageProcessor = dataSmMessageProcessor;
  }

  public void setDeliverSmMessageProcessor(DeliverSmMessageProcessor deliverSmMessageProcessor) {
    this.deliverSmMessageProcessor = deliverSmMessageProcessor;
  }

}