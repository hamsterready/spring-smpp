/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mt;

/**
 * The class is responsible for finding minimal set of characters(encoding)
 * required to encode the given text as SMS.
 * 
 * @author Artur Kowalczyk
 */
public class EncodingsDetector {

  /**
   * FIXME - is it correct implementation?
   * 
   * Method tries to find the best encoding for the given text.
   * 
   * @param text
   *          the text(in UTF-8, regular Java String) to encode.
   * @return the best encoding for given text either ENC8BIT or UCS2.
   */
  public static Encodings detect(String text) {
    Encodings result = Encodings.ENC8BIT;

    for (int i = 0; i < text.length(); i++) {
      if (text.charAt(i) > 255) {
        return Encodings.ENCUCS2;
      }
    }

    return result;
  }

}
