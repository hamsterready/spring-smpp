package com.sentaca.spring.smpp.jsmpp;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.DataCoding;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.OptionalParameter;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.runners.MockitoJUnitRunner;
import org.smslib.Message.MessageClasses;
import org.smslib.OutboundMessage;
import org.smslib.Message.MessageEncodings;
import org.smslib.OutboundMessage.FailureCauses;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.smpp.Address;

import com.sentaca.spring.smpp.BindConfiguration;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;

@RunWith(MockitoJUnitRunner.class)
public class JSMPPGatewayTest {

  @Mock
  private SMPPSession session;
  @Mock
  private BindConfiguration smscConfig;
  @Mock
  private MessageReceiver messageReceiver;
  @Mock
  private SMPPMonitoringAgent smppMonitoringAgent;
  @Mock
  private OutboundMessage msg;

  private Address sourceAddress = new Address();
  private Address destinationAddress = new Address();
  private boolean useUdhi = true;

  private JSMPPGateway gateway;

  @Before
  public void init() {
    final Set<Integer> smscErrorsToIgnore = new HashSet<Integer>();
    smscErrorsToIgnore.add(SMPPConstant.STAT_ESME_RX_P_APPN);
    smscErrorsToIgnore.add(SMPPConstant.STAT_ESME_RINVDSTADR);
    
    gateway = new JSMPPGateway(smscConfig, messageReceiver, smppMonitoringAgent, useUdhi, smscErrorsToIgnore);
    Whitebox.setInternalState(gateway, "session", session);
    Whitebox.setInternalState(gateway, "sourceAddress", sourceAddress);
    Whitebox.setInternalState(gateway, "destinationAddress", destinationAddress);

    when(msg.getEncoding()).thenReturn(MessageEncodings.ENC8BIT);
    when(msg.getDCSMessageClass()).thenReturn(MessageClasses.MSGCLASS_FLASH);
    when(msg.getText()).thenReturn("test");
  }

  @Test(expected = IOException.class)
  public void regularNegativeResponseExTest() throws Exception {
    mockSubmitShortMessage(SMPPConstant.STAT_ESME_RCANCELFAIL);

    gateway.sendMessage(msg);
  }
  
  @Test
  public void permanentAppErrTest() throws Exception {
    mockSubmitShortMessage(SMPPConstant.STAT_ESME_RX_P_APPN);

    boolean result = gateway.sendMessage(msg);
    
    assertFalse(result);
    verify(msg).setMessageStatus(MessageStatuses.FAILED);
    verify(msg).setFailureCause(FailureCauses.BAD_NUMBER);
  }
  
  @Test
  public void invalidDestAddrTest() throws Exception {
    mockSubmitShortMessage(SMPPConstant.STAT_ESME_RINVDSTADR);

    boolean result = gateway.sendMessage(msg);
    
    assertFalse(result);
    verify(msg).setMessageStatus(MessageStatuses.FAILED);
    verify(msg).setFailureCause(FailureCauses.BAD_NUMBER);
  }
  
  private void mockSubmitShortMessage(int cmdStatus) throws Exception {
    when(session.submitShortMessage(anyString(), any(TypeOfNumber.class), any(NumberingPlanIndicator.class), anyString(), any(TypeOfNumber.class),
        any(NumberingPlanIndicator.class), anyString(), any(ESMClass.class), anyByte(), anyByte(), anyString(), anyString(), any(RegisteredDelivery.class),
        anyByte(), any(DataCoding.class), anyByte(), any(byte[].class), Matchers.<OptionalParameter> anyVararg())).thenThrow(
    new NegativeResponseException(cmdStatus));
  }

}
