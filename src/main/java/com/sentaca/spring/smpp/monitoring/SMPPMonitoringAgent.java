package com.sentaca.spring.smpp.monitoring;

import org.smslib.OutboundMessage;

import com.sentaca.spring.smpp.SMPPService;
import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;
import com.sentaca.spring.smpp.mt.MTMessage;

public interface SMPPMonitoringAgent {

  /**
   * Raised when SMSC connection fails for some reason and {@link JSMPPGateway}
   * cannot be started.
   * 
   * Default {@link JSMPPGateway} behaviour is to keep trying to connect and
   * BIND using provided parameters.
   * 
   * This method should not throw exceptions as they are not handled properly:
   * http://bit.ly/YVQiua
   * 
   * @param e
   */
  void onGatewayStartupError(Exception e);

  /**
   * Invoked after successful connection, BIND and ENQUIRE LINK timer setup.
   * Invoked on restarts, see {@link #onGatewayShutdown(String)}.
   * 
   * @param gatewayId
   */
  void onGatewayStartupSuccess(String gatewayId);

  /**
   * Invoked on gateway restarts.
   * 
   * @param gatewayId
   */
  void onGatewayShutdown(String gatewayId);

  /**
   * Invoked on each {@link SMPPService#send(MTMessage)}
   * 
   * @param message
   *          - original message
   * @param outboundMessasge
   *          - converted message
   */
  void onMessageSend(MTMessage message, OutboundMessage outboundMessasge);

}
