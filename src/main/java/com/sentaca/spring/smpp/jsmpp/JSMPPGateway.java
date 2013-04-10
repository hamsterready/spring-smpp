// SMSLib for Java v3
// A Java API library for sending and receiving SMS via a GSM modem
// or other supported gateways.
// Web Site: http://www.smslib.org
//
// Copyright (C) 2002-2011, Thanasis Delenikas, Athens/GREECE.
// SMSLib is distributed under the terms of the Apache License version 2.0
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.sentaca.spring.smpp.jsmpp;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.SMPPConstant;
import org.jsmpp.bean.Alphabet;
import org.jsmpp.bean.BindType;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.GeneralDataCoding;
import org.jsmpp.bean.MessageClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.extra.SessionState;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SessionStateListener;
import org.smslib.AGateway;
import org.smslib.GatewayException;
import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;
import org.smslib.OutboundMessage.FailureCauses;
import org.smslib.OutboundMessage.MessageStatuses;
import org.smslib.TimeoutException;
import org.smslib.helper.Logger;
import org.smslib.smpp.AbstractSMPPGateway;
import org.smslib.smpp.BindAttributes;

import com.sentaca.spring.smpp.BindConfiguration;
import com.sentaca.spring.smpp.mo.MessageReceiver;
import com.sentaca.spring.smpp.monitoring.SMPPMonitoringAgent;

/**
 * A gateway that supports SMPP through JSMPP (http://code.google.com/p/jsmpp/).
 * 
 * @author Bassam Al-Sarori
 * 
 */
public class JSMPPGateway extends AbstractSMPPGateway {

  class JSMPPSessionStateListener implements SessionStateListener {

    public void onStateChange(SessionState newState, SessionState oldState, Object source) {
      if (logger.isInfoEnabled()) {
        logger.info("#onStateChange: " + getGatewayId() + ": " + oldState.name() + " to " + newState.name() + ".");
      }

      if (newState.isBound()) {
        if (!getStatus().equals(GatewayStatuses.STARTED)) {
          try {
            if (logger.isInfoEnabled()) {
              logger.info("#onStateChange: starting gateway " + getGatewayId() + ".");
            }

            JSMPPGateway.super.startGateway();

            if (logger.isInfoEnabled()) {
              logger.info("#onStateChange: gateway " + getGatewayId() + " started.");
            }

          } catch (Exception e) {
            logger.error(e.getMessage(), e);
            smppMonitoringAgent.onGatewayStartupError(JSMPPGateway.this.getGatewayId(), e);
          }
        }
      } else if (newState.equals(SessionState.CLOSED) || newState.equals(SessionState.UNBOUND)) {
        if (getStatus().equals(GatewayStatuses.STARTED)) {
          JSMPPGateway.super.setStatus(GatewayStatuses.RESTART);
        }
      }
    }
  }

  private static Log logger = LogFactory.getLog(JSMPPGateway.class);
  private static final int PDU_PROCESSOR_DEGREE = 10;

  private static final long TRANSACTION_TIMER = 6000L;

  private MessageReceiver messageReceiver;

  private SMPPSession session = null;
  private SessionStateListener stateListener = new JSMPPSessionStateListener();
  private BindType bindType;
  private TypeOfNumber bindTypeOfNumber;
  private NumberingPlanIndicator bindNumberingPlanIndicator;

  private final BindConfiguration smscConfig;
  private SMPPMonitoringAgent smppMonitoringAgent;
  private boolean useUdhi;

  /**
   * 
   * @param smscConfig
   * @param messageReceiver
   * @param smppMonitoringAgent
   * @param useUdhi 
   */
  public JSMPPGateway(BindConfiguration smscConfig, MessageReceiver messageReceiver, SMPPMonitoringAgent smppMonitoringAgent, boolean useUdhi) {
    super(smscConfig.getHost() + ":" + smscConfig.getPort() + ":" + smscConfig.getSystemId(), smscConfig.getHost(), smscConfig.getPort(), new BindAttributes(
        smscConfig.getSystemId(), smscConfig.getPassword(), smscConfig.getSystemType(), org.smslib.smpp.BindAttributes.BindType.TRANSCEIVER));
    this.smscConfig = smscConfig;
    this.messageReceiver = messageReceiver;
    this.smppMonitoringAgent = smppMonitoringAgent;
    this.useUdhi = useUdhi;
    messageReceiver.init(this);
    setAttributes(AGateway.GatewayAttributes.SEND | AGateway.GatewayAttributes.CUSTOMFROM | AGateway.GatewayAttributes.BIGMESSAGES
        | AGateway.GatewayAttributes.FLASHSMS | AGateway.GatewayAttributes.RECEIVE);
    this.setSourceAddress(smscConfig.getSourceAddress());
    this.setDestinationAddress(smscConfig.getDestinationAddress());
    this.setProtocol(Protocols.PDU);

    init();
  }

  private void closeSession() {
    if (session != null) {
      session.removeSessionStateListener(stateListener);
      session.unbindAndClose();
      session = null;
    }
  }

  private String formatTimeFromHours(int timeInHours) {
    if (timeInHours < 0) {
      return null;
    }
    Calendar cDate = Calendar.getInstance();
    cDate.clear();
    cDate.set(Calendar.YEAR, 0);
    cDate.add(Calendar.HOUR, timeInHours);

    int years = cDate.get(Calendar.YEAR) - cDate.getMinimum(Calendar.YEAR);
    int months = cDate.get(Calendar.MONTH);
    int days = cDate.get(Calendar.DAY_OF_MONTH) - 1;
    int hours = cDate.get(Calendar.HOUR_OF_DAY);

    String yearsString = (years < 10) ? "0" + years : years + "";
    String monthsString = (months < 10) ? "0" + months : months + "";
    String daysString = (days < 10) ? "0" + days : days + "";
    String hoursString = (hours < 10) ? "0" + hours : hours + "";

    return yearsString + monthsString + daysString + hoursString + "0000000R";
  }

  public BindConfiguration getSmscConfig() {
    return smscConfig;
  }

  private void init() {
    switch (bindAttributes.getBindType()) {
    case RECEIVER:
      bindType = BindType.BIND_RX;
      setInbound(true);
      setOutbound(false);
      break;
    case TRANSMITTER:
      bindType = BindType.BIND_TX;
      setInbound(false);
      setOutbound(true);
      break;
    case TRANSCEIVER:
      bindType = BindType.BIND_TRX;
      setInbound(true);
      setOutbound(true);
      break;
    default:
      IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Unknown BindType " + bindAttributes.getBindType());
      Logger.getInstance().logError(illegalArgumentException.getMessage(), illegalArgumentException, getGatewayId());
      throw illegalArgumentException;
    }

    bindTypeOfNumber = TypeOfNumber.valueOf(bindAttributes.getBindAddress().getTypeOfNumber().value());
    bindNumberingPlanIndicator = NumberingPlanIndicator.valueOf(bindAttributes.getBindAddress().getNumberingPlanIndicator().value());

    initSession();
  }

  private void initSession() {
    closeSession();

    if (logger.isTraceEnabled()) {
      logger.trace("#initSession with " + stateListener + " and " + messageReceiver + ".");
    }

    session = new SMPPSession();
    session.setPduProcessorDegree(PDU_PROCESSOR_DEGREE);
    session.setTransactionTimer(TRANSACTION_TIMER);

    session.addSessionStateListener(stateListener);

    session.setMessageReceiverListener(messageReceiver);
  }

  @Override
  public boolean sendMessage(OutboundMessage msg) throws TimeoutException, GatewayException, IOException, InterruptedException {

    Alphabet encoding = Alphabet.ALPHA_DEFAULT;

    switch (msg.getEncoding()) {
    case ENC8BIT:
      encoding = Alphabet.ALPHA_8_BIT;
      break;
    case ENCUCS2:
      encoding = Alphabet.ALPHA_UCS2;
      break;
    case ENCCUSTOM:
      encoding = Alphabet.ALPHA_RESERVED;
      break;
    default:
      break;
    }
    GeneralDataCoding dataCoding;

    switch (msg.getDCSMessageClass()) {
    case MSGCLASS_FLASH:
      dataCoding = new GeneralDataCoding(false, true, MessageClass.CLASS0, encoding);
      break;
    case MSGCLASS_ME:
      dataCoding = new GeneralDataCoding(false, true, MessageClass.CLASS1, encoding);
      break;
    case MSGCLASS_SIM:
      dataCoding = new GeneralDataCoding(false, true, MessageClass.CLASS2, encoding);
      break;
    case MSGCLASS_TE:
      dataCoding = new GeneralDataCoding(false, true, MessageClass.CLASS3, encoding);
      break;
    default:
      if (encoding != Alphabet.ALPHA_8_BIT) {
        dataCoding = new GeneralDataCoding();
        dataCoding.setAlphabet(encoding);
      } else {
        dataCoding = new GeneralDataCoding(0);
      }
    }

    // the original code process the data incorrectly so we have to fix it
    byte[] binaryContent = null;

    if (msg instanceof OutboundBinaryMessage) {
      binaryContent = ((OutboundBinaryMessage) msg).getDataBytes();
    } else {
      binaryContent = msg.getText().getBytes();
    }

    final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
    registeredDelivery.setSMSCDeliveryReceipt((msg.getStatusReport()) ? SMSCDeliveryReceipt.SUCCESS_FAILURE : SMSCDeliveryReceipt.DEFAULT);

    boolean returnValue = true;
    // TODO support for UDHI messages
    if (useUdhi) {
      int dataLength = 134; // 140 - 6 (messahe length - udh length)
      int parts = (int) Math.ceil(binaryContent.length / (double) dataLength);
      byte referenceNumber = (byte) (Math.random() * 0xFF);
      for (int i = 0; i < parts; i++) {
        byte[] binaryContentPart = ArrayUtils.subarray(binaryContent, i * dataLength, Math.min(binaryContent.length, (i + 1) * dataLength));
        binaryContentPart = ArrayUtils.add(binaryContentPart, 0, (byte) 0x05);
        binaryContentPart = ArrayUtils.add(binaryContentPart, 1, (byte) 0x00);
        binaryContentPart = ArrayUtils.add(binaryContentPart, 2, (byte) 0x03);
        binaryContentPart = ArrayUtils.add(binaryContentPart, 3, referenceNumber);
        binaryContentPart = ArrayUtils.add(binaryContentPart, 4, (byte) parts);
        binaryContentPart = ArrayUtils.add(binaryContentPart, 5, (byte) (i + 1));
        returnValue = submitShortMessage(msg, dataCoding, registeredDelivery, binaryContentPart, new ESMClass(SMPPConstant.ESMCLS_UDHI_INDICATOR_SET));
      }
    } else {
      returnValue = submitShortMessage(msg, dataCoding, registeredDelivery, binaryContent, new ESMClass());
    }

    return returnValue;
  }

  /**
   * @param msg
   * @param dataCoding
   * @param registeredDelivery
   * @param binaryContentPart
   * @param esmClass
   * @return
   * @throws IOException
   * @throws TimeoutException
   */
  private boolean submitShortMessage(OutboundMessage msg, GeneralDataCoding dataCoding, final RegisteredDelivery registeredDelivery, byte[] binaryContentPart,
      ESMClass esmClass) throws IOException, TimeoutException {
    try {
      String msgId = session.submitShortMessage(smscConfig.getServiceType(), TypeOfNumber.valueOf(sourceAddress.getTypeOfNumber().value()),
          NumberingPlanIndicator.valueOf(sourceAddress.getNumberingPlanIndicator().value()), (msg.getFrom() != null) ? msg.getFrom() : getFrom(),
          TypeOfNumber.valueOf(destinationAddress.getTypeOfNumber().value()),
          NumberingPlanIndicator.valueOf(destinationAddress.getNumberingPlanIndicator().value()), msg.getRecipient(), esmClass, (byte) 0,
          (byte) msg.getPriority(), null, formatTimeFromHours(msg.getValidityPeriod()), registeredDelivery, (byte) 0, dataCoding, (byte) 0, binaryContentPart);

      msg.setRefNo(msgId);
      msg.setDispatchDate(new Date());
      msg.setGatewayId(getGatewayId());
      msg.setMessageStatus(MessageStatuses.SENT);
      incOutboundMessageCount();

    } catch (PDUException e) {
      msg.setGatewayId(getGatewayId());
      msg.setMessageStatus(MessageStatuses.FAILED);
      msg.setFailureCause(FailureCauses.BAD_FORMAT);
      logger.error(getGatewayId() + ": Message Format not accepted: " + e.getMessage(), e);
      return false;
    } catch (ResponseTimeoutException e) {
      logger.error(getGatewayId() + ": Message could not be sent: " + e.getMessage(), e);
      throw new TimeoutException(e);
    } catch (InvalidResponseException e) {
      logger.error(getGatewayId() + ": Message could not be sent: " + e.getMessage(), e);
      throw new IOException("InvalidResponseException: ", e);
    } catch (NegativeResponseException e) {
      logger.error(getGatewayId() + ": Message could not be sent: " + e.getMessage(), e);
      throw new IOException("NegativeResponseException: ", e);
    }
    return true;
  }

  @Override
  public void setEnquireLink(int enquireLink) {
    super.setEnquireLink(enquireLink);
    if (session != null) {
      session.setEnquireLinkTimer(enquireLink);
    }
  }

  @Override
  public void startGateway() throws TimeoutException, GatewayException, IOException, InterruptedException {
    if (session == null || !session.getSessionState().isBound()) {
      initSession();
      if (enquireLink > 0) {
        session.setEnquireLinkTimer(enquireLink);
      }
      session.connectAndBind(host, port, new BindParameter(bindType, bindAttributes.getSystemId(), bindAttributes.getPassword(),
          bindAttributes.getSystemType(), bindTypeOfNumber, bindNumberingPlanIndicator, null));
    } else {
      Logger.getInstance().logWarn("SMPP session already bound.", null, getGatewayId());
    }
    smppMonitoringAgent.onGatewayStartupSuccess(getGatewayId());
  }

  @Override
  public void stopGateway() throws TimeoutException, GatewayException, IOException, InterruptedException {
    if (session != null && session.getSessionState().isBound()) {
      closeSession();
    } else {
      Logger.getInstance().logWarn("SMPP session not bound.", null, getGatewayId());
    }
    super.stopGateway();
    smppMonitoringAgent.onGatewayShutdown(getGatewayId());
  }

}
