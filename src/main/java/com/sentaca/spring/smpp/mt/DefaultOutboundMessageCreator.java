package com.sentaca.spring.smpp.mt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.smslib.OutboundBinaryMessage;
import org.smslib.OutboundMessage;

public class DefaultOutboundMessageCreator implements OutboundMessageCreator {

  @Override
  public OutboundMessage toOutboundMessage(MTMessage message) throws IOException {
    final Encodings encoding = EncodingsDetector.detect(message.getContent());

    OutboundMessage sms = null;

    switch (encoding) {
    case ENC8BIT:
      sms = new OutboundBinaryMessage(message.getDestinationAddress(), message.getContent().getBytes("ISO-8859-1"));
      break;
    case ENCUCS2:
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      OutputStreamWriter osw = new OutputStreamWriter(baos, "UnicodeBigUnmarked");
      osw.write(message.getContent());
      osw.close();

      sms = new OutboundBinaryMessage(message.getDestinationAddress(), baos.toByteArray());
      break;
    }

    sms.setEncoding(encoding.getSmslibEncodings());
    sms.setFrom(message.getOriginatingAddress());

    return sms;

  }

}
