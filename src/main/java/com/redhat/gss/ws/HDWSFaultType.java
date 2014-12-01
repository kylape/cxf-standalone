package com.redhat.gss.ws;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HDWSFaultType", propOrder = {
    "code",
    "subCode"
})
public class HDWSFaultType {

  @XmlElement(name = "Code", required = true)
  protected QName code;
  @XmlElement(name = "SubCode")
  protected List<QName> subCode;

  public QName getCode() {
    return code;
  }

  public void setCode(QName value) {
    this.code = value;
  }

  public List<QName> getSubCode() {
    if (subCode == null) {
      subCode = new ArrayList<QName>();
    }
    return this.subCode;
  }
}
