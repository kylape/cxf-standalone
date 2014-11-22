package com.redhat.gss.ws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.spi.Provider;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.jboss.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import java.io.InputStream;
import javax.xml.ws.soap.SOAPBinding;

public class Test {
  private static ObjectPool<WrapperHack<HashServer>> pool = null;

  private static final Logger log = Logger.getLogger(Test.class);
  private static final URL FILE = Test.class.getResource("/shadowman.jpg");
  private static String FILE_HASH = "";

  static {
    try {
      FILE_HASH = calcHash(FILE);
    } catch(Exception e) {
      log.error("Local hash can't be calculated!", e);
    }
  }

  public void init() {
    log.warn("Provider class:       " + Provider.provider().getClass().getName());
    log.warn("Provider classloader: " + Provider.provider().getClass().getClassLoader());
    if (pool == null) {
      final QName ns = new QName("http://ws.gss.redhat.com/", "HashServerImplService");
      String host = System.getProperty("gss.endpoint.host", "localhost:8080");
      URL wsdl = null;

      try {
        wsdl = new URL("http://" + host + "/hello/hello?wsdl");
      } catch(MalformedURLException mue) {
      }
      final Service service = Service.create(wsdl, ns);
      pool = new GenericObjectPool<WrapperHack<HashServer>>(new JaxWsClientPoolFactory(service));
    }
  }

  public static void main(String[] args) throws Exception {
    Test t = new Test();
    t.init();
    t.test();
  }

  public void test() throws Exception {
    test(10, 10000);
  }

  public void test(int numThreads, int count) throws Exception {
    log.info("Starting Client load test with " + numThreads + " threads and " + count + " invocations per thread");
    try {
      CountDownLatch finishLatch = new CountDownLatch(numThreads);
      for (int i=0; i<numThreads; i++) {
        new Thread(new ClientRunner(count, finishLatch)).start();
      }
      finishLatch.await();
      log.info("Test successfully completed.");
    } catch (InterruptedException ie) {
    }
  }

  public class ClientRunner implements Runnable {
    private final int count;
    private final CountDownLatch finishLatch;

    public ClientRunner(int count, CountDownLatch finishLatch) {
      this.count = count;
      this.finishLatch = finishLatch;
    }

    public void run() {
      WrapperHack<HashServer> wrapper = null;
      
      try {
        wrapper = pool.borrowObject();
        HashServer port = wrapper.getItem();
        SOAPBinding binding = (SOAPBinding)((BindingProvider)port).getBinding();
        binding.setMTOMEnabled(true);

        long start = 0, end = 0;
        long[] times = new long[1000];
        for(int i=0 ;i < count; i++) {
          if((i % 1000) == 0) {
            long avg = 0L;
            for(int j=0; j<1000; j++) {
              avg += times[j];
            }
            avg = avg/1000;
            log.debugf("%d: %dms", i, avg);
          }
          if(log.isTraceEnabled()) {
            log.trace("Run: " + i);
          }
          DataHandler dh = new DataHandler(new URLDataSource(FILE));
          ContentDataType data = new ContentDataType();
          data.setContentData(dh);

          start = System.nanoTime();
          String hash = port.calcHash(data);
          end = System.nanoTime();

          long elapsed = (end-start)/1000000;
          times[i % 1000] = elapsed;
          if(log.isTraceEnabled()) {
            log.tracef("Elapsed time: %dms", elapsed);
          }

          if(!FILE_HASH.equals(hash)) {
            log.warn("Hash not equal!");
          }
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (wrapper != null) {
          try {
            pool.returnObject(wrapper);
          } catch(Exception e) {
            e.printStackTrace();
          }
        }
        finishLatch.countDown();
        log.debug("Thread complete.  Current latch count: " + finishLatch.getCount());
      }
    }
  }

  private static String calcHash(URL url) throws Exception {
    InputStream input = url.openStream();
    MessageDigest digest = MessageDigest.getInstance("MD5");
    byte[] bb = new byte[1024];
    int length = 0;
    while((length = input.read(bb)) > 0) {
      digest.update(bb, 0, length);
    }
    return toHexString(digest.digest());
  }

  private static String toHexString(byte[] hash) {
    BigInteger bi = new BigInteger(1, hash);
    return String.format("%0" + (hash.length << 1) + "x", bi);
  }
}
