/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo;

import org.jsmpp.bean.AlertNotification;

public interface AlertNotificationMessageProcessor {

  void processMessage(AlertNotification alertNotification);

}
