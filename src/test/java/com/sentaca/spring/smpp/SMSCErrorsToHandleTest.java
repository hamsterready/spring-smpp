package com.sentaca.spring.smpp;

import static org.junit.Assert.assertTrue;

import org.jsmpp.SMPPConstant;
import org.junit.Test;

public class SMSCErrorsToHandleTest {

  private SMSCErrorsToHandle errors;
  
  @Test
  public void defaultValues() {
    errors = new SMSCErrorsToHandle("11, 101");
    
    assertTrue(errors.getSet().contains(SMPPConstant.STAT_ESME_RX_P_APPN));
    assertTrue(errors.getSet().contains(SMPPConstant.STAT_ESME_RINVDSTADR));
  }
  
  @Test
  public void anEmptyTest() {
    errors = new SMSCErrorsToHandle("");
    
    assertTrue(errors.getSet().isEmpty());
  }

}
