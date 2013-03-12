package com.sentaca.spring.smpp.mt;

import java.io.IOException;

import org.smslib.OutboundMessage;

public interface OutboundMessageCreator {

  OutboundMessage toOutboundMessage(MTMessage message) throws IOException;
}
