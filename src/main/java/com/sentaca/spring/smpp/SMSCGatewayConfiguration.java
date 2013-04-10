package com.sentaca.spring.smpp;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;
import com.sentaca.spring.smpp.mo.MessageReceiver;

/**
 * Composition of {@link BindConfiguration} and {@link MessageReceiver}. Used by
 * {@link SMPPService} for deferred {@link JSMPPGateway} initialization (as
 * {@link JSMPPGateway} does not follow POJO-style).
 * 
 * @see SMPPService
 * @author hamster
 * 
 */
public class SMSCGatewayConfiguration {

  private final BindConfiguration smscConfig;

  private final MessageReceiver messageReceiver;

  private boolean useUdhiInSubmitSm = false;

  public SMSCGatewayConfiguration(BindConfiguration smscConfig, MessageReceiver messageReceiver) {
    this.smscConfig = smscConfig;
    this.messageReceiver = messageReceiver;
  }

  public MessageReceiver getMessageReceiver() {
    return messageReceiver;
  }

  public BindConfiguration getSmscConfig() {
    return smscConfig;
  }

  public boolean isUseUdhiInSubmitSm() {
    return useUdhiInSubmitSm;
  }

  public void setUseUdhiInSubmitSm(boolean useUdhiInSubmitSm) {
    this.useUdhiInSubmitSm = useUdhiInSubmitSm;
  }
}
