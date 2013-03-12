package com.sentaca.spring.smpp.mt;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class MTMessage {

  private String originatingAddress;
  private String destinationAddress;
  private String content;

  public MTMessage() {
    // -- default
  }

  public MTMessage(String originatingAddress, String destinationAddress, String content) {
    this.originatingAddress = originatingAddress;
    this.destinationAddress = destinationAddress;
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public String getDestinationAddress() {
    return destinationAddress;
  }

  public String getOriginatingAddress() {
    return originatingAddress;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public void setDestinationAddress(String destinationAddress) {
    this.destinationAddress = destinationAddress;
  }

  public void setOriginatingAddress(String originatingAddress) {
    this.originatingAddress = originatingAddress;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
  }

}
