package com.sentaca.spring.smpp.mt;

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

}
