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
