package com.redhat.gss.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService
public interface HashServer {

    @WebResult(name = "return", targetNamespace = "", partName = "return")
    @WebMethod
    public String calcHash(
        @WebParam(partName = "data", name = "dataType")
        ContentDataType data
    ) throws Exception;
}
