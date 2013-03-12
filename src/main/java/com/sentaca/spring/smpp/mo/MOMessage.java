/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.smslib.Message.MessageEncodings;

/**
 * Class needed for storing destination address (destination_addr).
 * 
 * @author <a href="mailto:magdalena.biala@sentaca.com">Magdalena Biala</a>
 * 
 */
public class MOMessage {

  private String destAddress;
  private String text;
  private MessageEncodings encoding;
  private String originator;
  private String messageId;

  public MOMessage(String originator, String destAddress, String text, MessageEncodings encoding) {
    this.originator = originator;
    this.text = text;
    this.destAddress = destAddress;
    this.encoding = encoding;
  }

  public String getDestAddress() {
    return destAddress;
  }

  public void setDestAddress(String destAddress) {
    this.destAddress = destAddress;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public MessageEncodings getEncoding() {
    return encoding;
  }

  public void setEncoding(MessageEncodings encoding) {
    this.encoding = encoding;
  }

  public String getOriginator() {
    return originator;
  }

  public void setOriginator(String originator) {
    this.originator = originator;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }
}
