package com.redhat.gss.ws;

import javax.jws.WebService;
import org.jboss.logging.Logger;
import javax.jws.Oneway;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPException;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.soap.SOAPFaultException;

@WebService
@BindingType(value=SOAPBinding.SOAP12HTTP_BINDING)
public class HelloImpl {
  private static Logger log = Logger.getLogger(Hello.class);
  private static final String FAULT_NS = "http://gss.redhat.com/";

  public void hello(String name) throws HDWSFault {
    String greeting = "Hello, " + name + "!";
    if("Jack".equals(name)) {
      try {
        HDWSFaultType fault = new HDWSFaultType();
        fault.setCode(new QName(FAULT_NS, "badName"));
        fault.getSubCode().add(new QName(FAULT_NS, "dislikedName"));
        SOAPFactory factory = SOAPFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPFault soapFault = factory.createFault("Error accepting name", new QName(SOAPConstants.URI_NS_SOAP_1_2_ENVELOPE, "Sender"));
        soapFault.appendFaultSubcode(new QName("urn:nzl:govt:ict:stds:authn:deployment:igovt:gls:hdws:1_0", "dislikedName"));
        SOAPFaultException sfe = new SOAPFaultException(soapFault);
        throw new HDWSFault("The reason of the fault", sfe, fault);
      } catch(SOAPException e) {
        throw new RuntimeException("Bad name");
      }
    }
    log.debug(greeting);
  }
}
