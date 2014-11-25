package com.redhat.gss.ws;

import java.io.InputStream;
import javax.xml.ws.soap.MTOM;
import javax.activation.DataHandler;
import javax.jws.WebService;
import java.security.MessageDigest;
import org.jboss.logging.Logger;
import java.math.BigInteger;
import org.apache.cxf.annotations.EndpointProperty;
import org.apache.cxf.annotations.EndpointProperties;
import java.lang.reflect.Method;
import javax.xml.ws.WebServiceContext;
import javax.annotation.Resource;
import javax.xml.ws.handler.MessageContext;
import org.apache.cxf.message.Message;
import org.apache.cxf.jaxws.context.WrappedMessageContext;
import java.util.Collection;
import org.apache.cxf.io.Transferable;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.FileInputStream;

@MTOM
@WebService(endpointInterface="com.redhat.gss.ws.HashServer")
public class HashServerImpl implements HashServer {
  private static final String ATTACHMENT_PATH = "/home/remote/klape/work/dev/maven-projects/cxf-standalone/attachments";
  private static final Logger log = Logger.getLogger(HashServer.class);
  private static final AtomicInteger counter = new AtomicInteger();

  @Resource
  WebServiceContext ctx;

  public String calcHash(ContentDataType data) throws Exception {
    try {
      Message message = ((WrappedMessageContext)ctx.getMessageContext()).getWrappedMessage();
      Collection c = message.getAttachments();
      for(Object o : c) {
        //Don't do anything
        //Just iterating through the attachments will force CXF to cache them
        //Which will make it analyze the size vs threshold
      }

      DataHandler dh = data.getContentData();
      InputStream input = dh.getInputStream();
      if(input instanceof Transferable) {
        Transferable t = (Transferable)input;
        String filename = ATTACHMENT_PATH + File.separator + counter.incrementAndGet();
        log.info(filename);
        File f = new File(filename);
        t.transferTo(f);
        input = new FileInputStream(f);
      }

      MessageDigest digest = MessageDigest.getInstance("MD5");
      byte[] bb = new byte[2048];
      int totalLength = 0;
      int length = 0;
      while((length = input.read(bb)) > 0) {
        totalLength += length;
        digest.update(bb, 0, length);
      }
      input.close();
      log.info("Total attachment length: " + totalLength);
      String s = toHexString(digest.digest());
      log.debug("Digest: " + s);
      return s;
    } catch(Exception e) {
      log.error("", e);
    }
    return null;
  }

  private String toHexString(byte[] hash) {
    BigInteger bi = new BigInteger(1, hash);
    return String.format("%0" + (hash.length << 1) + "x", bi);
  }
}
