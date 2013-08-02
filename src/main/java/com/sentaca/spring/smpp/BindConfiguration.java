/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.smslib.smpp.Address;

/**
 * Class stores configuration of SMSC.
 * 
 * @author Artur Kowalczyk
 */
public class BindConfiguration {

  private String host;
  private int port;
  private String systemId;
  private String password;
  private String sourceNPI;
  private String sourceTON;
  private String destinationNPI;
  private String destinationTON;
  /**
   * Used in BIND.
   */
  private String systemType;
  /**
   * Used in SUBMIT_SM
   * 
   */
  private String serviceType;
  
  private String localHost;
  private int localPort = 0;

  public Address getDestinationAddress() {
    if (null != this.getDestinationTON() && null != this.getDestinationNPI()) {
      return new Address(Address.TypeOfNumber.valueOf(Byte.parseByte(this.getDestinationTON())), Address.NumberingPlanIndicator.valueOf(Byte.parseByte(this
          .getDestinationNPI())));
    } else {
      return new Address();
    }
  }

  public String getDestinationNPI() {
    return destinationNPI;
  }

  public String getDestinationTON() {
    return destinationTON;
  }

  public String getHost() {
    return host;
  }

  public String getLocalHost() {
    return localHost;
  }

  public int getLocalPort() {
    return localPort;
  }

  public String getPassword() {
    return password;
  }

  public int getPort() {
    return port;
  }

  public String getServiceType() {
    return serviceType;
  }

  public Address getSourceAddress() {
    if (null != this.getSourceTON() && null != this.getSourceNPI()) {
      return new Address(Address.TypeOfNumber.valueOf(Byte.parseByte(this.getSourceTON())), Address.NumberingPlanIndicator.valueOf(Byte.parseByte(this
          .getSourceNPI())));
    } else {
      return new Address();
    }
  }

  public String getSourceNPI() {
    return sourceNPI;
  }

  public String getSourceTON() {
    return sourceTON;
  }

  public String getSystemId() {
    return systemId;
  }

  public String getSystemType() {
    return systemType;
  }

  public void setDestinationNPI(String destinationNPI) {
    this.destinationNPI = destinationNPI;
  }

  public void setDestinationTON(String destinationTON) {
    this.destinationTON = destinationTON;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setLocalHost(String localHost) {
    this.localHost = localHost;
  }

  public void setLocalPort(int localPort) {
    this.localPort = localPort;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public void setServiceType(String serviceType) {
    this.serviceType = serviceType;
  }

  public void setSourceNPI(String sourceNPI) {
    this.sourceNPI = sourceNPI;
  }

  public void setSourceTON(String sourceTON) {
    this.sourceTON = sourceTON;
  }

  public void setSystemId(String systemId) {
    this.systemId = systemId;
  }

  public void setSystemType(String systemType) {
    this.systemType = systemType;
  }
  
  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }

}
