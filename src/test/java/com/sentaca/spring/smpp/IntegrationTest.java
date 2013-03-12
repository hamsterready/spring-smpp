package com.sentaca.spring.smpp;

import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.sentaca.spring.smpp.mt.MTMessage;

public class IntegrationTest extends TestCase {

  public void xtestMinimal() throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("minimal.xml");
    SMPPService service = context.getBean(SMPPService.class);

    Thread.sleep(5000);
    service.send(new MTMessage("123", "123123", "Hello! There test3"));
    Thread.sleep(6000);
    context.close();
  }

  public void xtestMerging() throws Exception {
    ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("merging.xml");
    SMPPService service = context.getBean(SMPPService.class);

    Thread.sleep(5000);
    service.send(new MTMessage("123", "123123", "Hello! There test3"));
    Thread.sleep(6000);
    context.close();
  }
}
