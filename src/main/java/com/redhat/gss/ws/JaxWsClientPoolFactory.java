package com.redhat.gss.ws;

import javax.xml.ws.Service;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import javax.xml.namespace.QName;

public class JaxWsClientPoolFactory extends BasePooledObjectFactory<WrapperHack<HashServer>> {
  private Service service = null;
  private static final QName qname = new QName("http://ws.gss.redhat.com/", "HashServerImplPort");

  public JaxWsClientPoolFactory(Service service) {
    this.service = service;
  }

  public synchronized WrapperHack<HashServer> create() {
    WrapperHack<HashServer> h = new WrapperHack<HashServer>(service.getPort(qname, HashServer.class));
    return h;
  }

  public PooledObject<WrapperHack<HashServer>> wrap(WrapperHack<HashServer> obj) {
    return new DefaultPooledObject<WrapperHack<HashServer>>(obj);
  }
}
