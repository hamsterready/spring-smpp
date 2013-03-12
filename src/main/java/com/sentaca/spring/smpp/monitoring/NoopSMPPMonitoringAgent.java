package com.sentaca.spring.smpp.monitoring;

import org.smslib.OutboundMessage;

import com.sentaca.spring.smpp.mt.MTMessage;

public class NoopSMPPMonitoringAgent implements SMPPMonitoringAgent {

  @Override
  public void onGatewayStartupError(Exception e) {
  }

  @Override
  public void onGatewayStartupSuccess(String gatewayId) {
  }

  @Override
  public void onGatewayShutdown(String gatewayId) {
  }

  @Override
  public void onMessageSend(MTMessage message, OutboundMessage outboundMessasge) {
  }

}
