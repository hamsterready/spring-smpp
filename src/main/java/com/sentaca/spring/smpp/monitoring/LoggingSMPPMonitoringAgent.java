package com.sentaca.spring.smpp.monitoring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.OutboundMessage;

import com.sentaca.spring.smpp.mt.MTMessage;

public class LoggingSMPPMonitoringAgent implements SMPPMonitoringAgent {

  private static final Log logger = LogFactory.getLog(LoggingSMPPMonitoringAgent.class);

  @Override
  public void onGatewayShutdown(String gatewayId) {
    logger.info("Gateway shutdown: " + gatewayId + ".");

  }

  @Override
  public void onGatewayStartupError(String gatewayId, Exception e) {
    logger.error(e.getMessage(), e);
  }

  @Override
  public void onGatewayStartupSuccess(String gatewayId) {
    logger.info("Gateway startup: " + gatewayId + ".");

  }

  @Override
  public void onMessageSend(MTMessage message, OutboundMessage outboundMessasge) {
    logger.info("Sending MT message: " + message + " (" + outboundMessasge + ").");
  }

}
