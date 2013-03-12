/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import java.io.UnsupportedEncodingException;

import org.ajwcc.pduUtils.gsm3040.PduUtils;

/**
 * Help class for decoding message to send from sms.
 * 
 * @author <a href="mailto:magdalena.biala@sentaca.com">Magdalena Biala</a>
 * 
 */
public class StringHelpers {

  public static String decode7bit(byte[] encodedMessage) {
    try {
      return encodedMessage != null ? new String(encodedMessage, "US-ASCII") : "";
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(" US-ASCII isn't supported", e);
    }
  }

  public static String decode8bit(byte[] encodedMessage) {
    return encodedMessage != null ? PduUtils.decode8bitEncoding(null, encodedMessage) : "";
  }

  public static String decodeUCS2(byte[] encodedMessage) {
    return encodedMessage != null ? PduUtils.decodeUcs2Encoding(null, encodedMessage) : "";
  }

  public static byte[] hexStringToByteArray(String s) {
    int len = s.length();
    byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

}
