package com.redhat.gss.ws;

import javax.xml.ws.handler.LogicalMessageContext;
import org.jboss.ws.api.handler.GenericSOAPHandler;
import org.jboss.logging.Logger;
import javax.xml.ws.handler.MessageContext;
import javax.xml.transform.Source;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.soap.SOAPHeaderElement;
import java.io.ByteArrayOutputStream;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;

public class SimpleLoggingHandler extends GenericSOAPHandler<LogicalMessageContext> implements Handler {
  
  private static Logger log = Logger.getLogger("com.redhat.gss.handlers");

  public boolean handleInbound(MessageContext ctx) {
    log.debug("Inbound:  " + getClass().getName());
    return true;
  }

  public boolean handleOutbound(MessageContext ctx) {
    log.debug("Outbound: " + getClass().getName());
    return true;
  }
}
