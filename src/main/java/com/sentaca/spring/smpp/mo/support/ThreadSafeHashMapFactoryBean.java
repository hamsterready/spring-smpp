/**
 * Copyright (c) 2012 Sentaca Communications Ltd.
 */
package com.sentaca.spring.smpp.mo.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.FactoryBean;

import com.sentaca.spring.smpp.mo.MOMessage;

public class ThreadSafeHashMapFactoryBean implements FactoryBean<Map<String, MOMessage[]>> {

  @Override
  public Map<String, MOMessage[]> getObject() throws Exception {
    return Collections.synchronizedMap(new HashMap<String, MOMessage[]>());
  }

  @Override
  public Class<?> getObjectType() {
    return HashMap.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }

}
