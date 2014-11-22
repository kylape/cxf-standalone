package com.redhat.gss.ws;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contentDataType", propOrder = {
    "contentData"
})
public class ContentDataType {

    @XmlElement(required = true)
    @XmlMimeType("application/octet-stream")
    protected DataHandler contentData;

    public ContentDataType() {}

    public ContentDataType(DataHandler dh) {
      this.contentData = dh;
    }

    public DataHandler getContentData() {
        return contentData;
    }

    public void setContentData(DataHandler value) {
        this.contentData = value;
    }
}
