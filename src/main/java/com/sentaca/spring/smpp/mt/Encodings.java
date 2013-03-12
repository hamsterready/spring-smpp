/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mt;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smslib.Message.MessageEncodings;

/***
 * Doesnot support for ENC7BIT
 * 
 * @author witek
 * 
 */
public enum Encodings {

  ENC8BIT(MessageEncodings.ENC8BIT, 140),
  ENCUCS2(MessageEncodings.ENCUCS2, 70);

  private static final Log logger = LogFactory.getLog(Encodings.class);

  private int length;
  @SuppressWarnings("unused")
  private MessageEncodings encoding;

  private Encodings(MessageEncodings encoding, int length) {
    this.encoding = encoding;
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  /***
   * Maps the MessageEncoding to the corresponding Encodings.
   * 
   * @param encoding
   * @return
   */
  public static Encodings get(MessageEncodings encoding) {
    switch (encoding) {
    case ENC7BIT:
    case ENC8BIT:
      return Encodings.ENC8BIT;
    case ENCUCS2:
      return Encodings.ENCUCS2;
    default:
      if (logger.isWarnEnabled()) {
        logger.warn("Unsupported encoding: " + encoding + " falling back to " + Encodings.ENC8BIT + ".");
      }
      return Encodings.ENC8BIT;
    }
  }

  public MessageEncodings getSmslibEncodings() {
    return MessageEncodings.valueOf(name());
  }
}
