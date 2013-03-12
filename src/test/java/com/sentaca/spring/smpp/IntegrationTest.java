package com.sentaca.spring.smpp;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.jsmpp.bean.DeliverSm;
import org.smslib.Message.MessageTypes;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sentaca.spring.smpp.mo.DeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.MOMessage;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.mo.support.MergingDeliverSmMessageProcessor;
import com.sentaca.spring.smpp.mo.support.ThreadSafeHashMapFactoryBean;
import com.sentaca.spring.smpp.mt.MTMessage;

public class IntegrationTest extends TestCase {

//  private SMPPService smppServer;

//  @Override
//  protected void setUp() throws Exception {
//    super.setUp();
//    smppServer = new SMPPService();
//    final Set<SMSCGatewayConfiguration> gatewaysConfigurations = new HashSet<SMSCGatewayConfiguration>();
//    BindConfiguration smscConfig = new BindConfiguration();
//    smscConfig.setSystemId("smppclient1");
//    smscConfig.setPassword("password");
//    smscConfig.setHost("midleton.sentaca.com");
//    smscConfig.setPort(2775);
//    smscConfig.setSourceNPI("1");
//    smscConfig.setSourceTON("0");
//    smscConfig.setDestinationNPI("1");
//    smscConfig.setDestinationTON("1");
//    smscConfig.setSystemType("test");
//    smscConfig.setServiceType("test");
//
//    MessageReceiver messageReceiver = new MessageReceiver();
//    messageReceiver.setDeliverSmMessageProcessor(new DeliverSmMessageProcessor() {
//
//      @Override
//      public void processInboundMessage(DeliverSm deliverSm, MOMessage inboundMessage) {
//        System.out.println("GOT " + inboundMessage);
//      }
//
//    });
//
//    gatewaysConfigurations.add(new SMSCGatewayConfiguration(smscConfig, messageReceiver));
//    smppServer.setGatewaysConfiguration(gatewaysConfigurations);
//
//  }
//
//  public void test01() throws Exception {
//    smppServer.afterPropertiesSet();
//
//    Thread.sleep(10000000);
//  }
//
//  public void test02() throws Exception {
//    final Map<String, MOMessage[]> map = new ThreadSafeHashMapFactoryBean().getObject();
//    Set<SMSCGatewayConfiguration> gatewaysConfigurations = new HashSet<SMSCGatewayConfiguration>();
//    BindConfiguration smscConfig1 = new BindConfiguration();
//    smscConfig1.setSystemId("smppclient1");
//    smscConfig1.setPassword("password");
//    smscConfig1.setHost("midleton.sentaca.com");
//    smscConfig1.setPort(2775);
//    smscConfig1.setSourceNPI("1");
//    smscConfig1.setSourceTON("0");
//    smscConfig1.setDestinationNPI("1");
//    smscConfig1.setDestinationTON("1");
//    smscConfig1.setSystemType("test");
//    smscConfig1.setServiceType("test");
//
//    gatewaysConfigurations.add(new SMSCGatewayConfiguration(smscConfig1, new MessageReceiver(new MergingDeliverSmMessageProcessor(map) {
//
//      @Override
//      public void processInboundMessageNotification(MessageTypes msgType, MOMessage inboundMessage) {
//        System.out.println("GOT " + inboundMessage);
//      }
//
//    })));
//    BindConfiguration smscConfig2 = new BindConfiguration();
//    smscConfig2.setSystemId("smppclient1");
//    smscConfig2.setPassword("password");
//    smscConfig2.setHost("midleton.sentaca.com");
//    smscConfig2.setPort(12775);
//    smscConfig2.setSourceNPI("1");
//    smscConfig2.setSourceTON("0");
//    smscConfig2.setDestinationNPI("1");
//    smscConfig2.setDestinationTON("1");
//    smscConfig2.setSystemType("test");
//    smscConfig2.setServiceType("test");
//    gatewaysConfigurations.add(new SMSCGatewayConfiguration(smscConfig2, new MessageReceiver(new MergingDeliverSmMessageProcessor(map) {
//
//      @Override
//      public void processInboundMessageNotification(MessageTypes msgType, MOMessage inboundMessage) {
  // System.out.println("GOT " + inboundMessage);
  // }
//
//    })));
//
//    smppServer.setGatewaysConfiguration(gatewaysConfigurations);
//    smppServer.afterPropertiesSet();
//
//    Thread.sleep(6000);
//
//    smppServer.send(new MTMessage("123", "123123", "Hello!"));
//
//    Thread.sleep(600000);
//
//  }

  public void testMinimal() throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("minimal.xml");
    SMPPService service = context.getBean(SMPPService.class);

    Thread.sleep(5000);
    service.send(new MTMessage("123", "123123", "Hello! There test3"));
    Thread.sleep(6000);
    context.close();
  }

  public void testMerging() throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("merging.xml");
    SMPPService service = context.getBean(SMPPService.class);

    Thread.sleep(5000);
    service.send(new MTMessage("123", "123123", "Hello! There test3"));
    Thread.sleep(6000);
    context.close();
  }
}
