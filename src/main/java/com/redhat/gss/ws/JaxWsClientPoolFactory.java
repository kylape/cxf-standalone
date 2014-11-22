package com.redhat.gss.ws;

import javax.xml.ws.Service;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import javax.xml.namespace.QName;

public class JaxWsClientPoolFactory extends BasePooledObjectFactory<WrapperHack<Hello>> {
  private Service service = null;
  private static final QName qname = new QName("http://ws.gss.redhat.com/", "HelloImplPort");

  public JaxWsClientPoolFactory(Service service) {
    this.service = service;
  }

  public synchronized WrapperHack<Hello> create() {
    WrapperHack<Hello> h = new WrapperHack<Hello>(service.getPort(qname, Hello.class));
    return h;
  }

  public PooledObject<WrapperHack<Hello>> wrap(WrapperHack<Hello> obj) {
    return new DefaultPooledObject<WrapperHack<Hello>>(obj);
  }
}
