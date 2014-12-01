package com.redhat.gss.ws;

import javax.xml.ws.WebFault;
import javax.xml.ws.soap.SOAPFaultException;
import javax.xml.soap.SOAPFault;

@WebFault(name = "HDWSFault", targetNamespace = "urn:nzl:govt:ict:stds:authn:deployment:igovt:gls:hdws:1_0")
public class HDWSFault extends Exception {

  private HDWSFaultType faultInfo;

  public HDWSFault(String message, SOAPFaultException sfe, HDWSFaultType faultInfo) {
    super(message, sfe);
    this.faultInfo = faultInfo;
  }

  public HDWSFaultType getFaultInfo() {
    return faultInfo;
  }
}
