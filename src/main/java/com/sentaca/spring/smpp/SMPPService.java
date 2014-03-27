package com.sentaca.spring.smpp;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.smslib.GatewayException;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.TimeoutException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.sentaca.spring.smpp.jsmpp.JSMPPGateway;
import com.sentaca.spring.smpp.monitoring.LoggingSMPPMonitoringAgent;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;
import com.sentaca.spring.smpp.mt.DefaultOutboundMessageCreator;
import com.sentaca.spring.smpp.mt.MTMessage;
import com.sentaca.spring.smpp.mt.OutboundMessageCreator;

/**
 * 
 * @author hamster
 * 
 */
public class SMPPService implements InitializingBean, DisposableBean {

  private static final boolean DEFAULT_AUTOSTART_MODE = true;

  private Set<SMSCGatewayConfiguration> gatewaysConfigurations = new HashSet<SMSCGatewayConfiguration>();

  private SMPPMonitoringAgent smppMonitoringAgent = new LoggingSMPPMonitoringAgent();

  private OutboundMessageCreator outboundMessageCreator = new DefaultOutboundMessageCreator();

  private boolean autoStart = DEFAULT_AUTOSTART_MODE;

  private boolean forceStart = false;

  /**
   * {@link #gatewaysConfigurations} must not be null nor empty
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    if (autoStart || forceStart) {
      Assert.notEmpty(gatewaysConfigurations, "Gateways must not be empty.");
      Assert.notNull(smppMonitoringAgent, "smppMonitoringAgent must not be null, use default LoggingSMPPMonitoringAgent or NoopSMPPMonitoringAgent.");
      Assert.notNull(outboundMessageCreator, "outboundMessageCreator must not be null, use DefaultOutboundMessageCreator or your own implementatin.");

      // add gateways
      for (SMSCGatewayConfiguration configuration : gatewaysConfigurations) {
        final JSMPPGateway gateway = new JSMPPGateway(configuration.getSmscConfig(), configuration.getMessageReceiver(), smppMonitoringAgent, configuration.isUseUdhiInSubmitSm());
        gateway.setEnquireLink(configuration.getEnquireLinkSeconds() * 1000);
        
        Service.getInstance().addGateway(gateway);
      }

      // and fire-it-up
      Service.getInstance().startService();
    }

  }

  @Override
  public void destroy() throws Exception {
    if (autoStart || forceStart) {
      Service.getInstance().stopService();
    }
  }

  public void send(MTMessage message) throws TimeoutException, GatewayException, IOException, InterruptedException {
    final OutboundMessage outboundMessasge = outboundMessageCreator.toOutboundMessage(message);
    outboundMessasge.setGatewayId("*"); // FIXME add support for routing one day
    smppMonitoringAgent.onMessageSend(message, outboundMessasge);
    Service.getInstance().sendMessage(outboundMessasge);
  }

  /**
   * Set to false if {@link #gatewaysConfigurations} should not be initialized
   * not started via {@link InitializingBean#afterPropertiesSet()}.
   * 
   * Use {@link #start()} to initialize gateways and server manually.
   * 
   * @see #DEFAULT_AUTOSTART_MODE
   * @param autoStart
   */
  public void setAutoStart(boolean autoStart) {
    this.autoStart = autoStart;
  }

  public void setGatewaysConfiguration(Set<SMSCGatewayConfiguration> gatewaysConfigurations) {
    this.gatewaysConfigurations = gatewaysConfigurations;
  }

  public void setOutboundMessageCreator(OutboundMessageCreator outboundMessageCreator) {
    this.outboundMessageCreator = outboundMessageCreator;
  }

  public void setSmppMonitoringAgent(SMPPMonitoringAgent smppMonitoringAgent) {
    this.smppMonitoringAgent = smppMonitoringAgent;
  }

  public void start() throws Exception {
    forceStart = true;
    afterPropertiesSet();
  }

}
