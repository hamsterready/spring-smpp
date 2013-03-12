/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import org.jsmpp.bean.DataSm;
import org.jsmpp.session.DataSmResult;
import org.jsmpp.session.Session;

public interface DataSmMessageProcessor {

  DataSmResult processMessage(DataSm dataSm, Session source);

}
