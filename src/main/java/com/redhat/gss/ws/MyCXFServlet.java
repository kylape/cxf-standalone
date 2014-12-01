package com.redhat.gss.ws;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import javax.xml.ws.Endpoint;

public class MyCXFServlet extends CXFNonSpringServlet {
  
  @Override
  protected void loadBus(ServletConfig servletConfig) {
    super.loadBus(servletConfig);        
     
    BusFactory.setDefaultBus(getBus()); 
    Endpoint.publish("/hello", new HelloImpl());
  }
}
