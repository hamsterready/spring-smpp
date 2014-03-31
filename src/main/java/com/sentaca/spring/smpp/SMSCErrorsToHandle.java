package com.sentaca.spring.smpp;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SMSCErrorsToHandle {
  
  private static Log logger = LogFactory.getLog(SMSCErrorsToHandle.class);

  private final Set<Integer> set = new HashSet<Integer>();

  public SMSCErrorsToHandle(String commaSeparatedErrors) {
    for (String value : commaSeparatedErrors.split(",")) {
      try {
        set.add(Integer.parseInt(value.trim()));
      } catch (NumberFormatException e) {
        logger.warn("Can't parse: " + value);
      }
    }
  }

  public SMSCErrorsToHandle(Set<Integer> errors) {
    this.set.addAll(errors);
  }
  
  public Set<Integer> getSet() {
    return set;
  }

}
