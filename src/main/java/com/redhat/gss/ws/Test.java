package com.redhat.gss.ws;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import org.jboss.logging.Logger;
import java.util.List;
import javax.xml.ws.handler.Handler;
import java.util.Arrays;
import java.util.Collections;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.PortInfo;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.spi.Provider;

public class Test {
  private static ObjectPool<WrapperHack<Hello>> pool = null;
  private final List<Handler> handlerChain = Collections.singletonList((Handler)new SimpleLoggingHandler());

  private static final Logger log = Logger.getLogger(Test.class);

  public void init() {
    log.warn("Provider class:       " + Provider.provider().getClass().getName());
    log.warn("Provider classloader: " + Provider.provider().getClass().getClassLoader());
    if (pool == null) {
      final QName ns = new QName("http://ws.gss.redhat.com/", "HelloImplService");
      URL wsdl = null;
      String host = System.getProperty("gss.endpoint.host", "localhost:8080");
      try {
        wsdl = new URL("http://" + host + "/hello/hello?wsdl");
      } catch(MalformedURLException mue) {
      }
      final Service service = Service.create(wsdl, ns);
      service.setHandlerResolver(new HandlerResolver() {
        public List<Handler> getHandlerChain(PortInfo info) {
          log.warn("Retrieving custom handler chain");
          return handlerChain;
        }
      });
      pool = new GenericObjectPool<WrapperHack<Hello>>(new JaxWsClientPoolFactory(service));
    }
  }

  public static void main(String[] args) throws Exception {
    // org.apache.log4j.BasicConfigurator.configure();
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
      WrapperHack<Hello> wrapper = null;
      
      try {
        wrapper = pool.borrowObject();
        Hello port = wrapper.getItem();

        long start = 0, end = 0;
        long[] times = new long[1000];
        for(int i=0 ;i < count; i++) {
          ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);
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
          start = System.nanoTime();
          port.hello("Kyle");
          end = System.nanoTime();
          long elapsed = (end-start)/1000000;
          times[i % 1000] = elapsed;
          if(log.isTraceEnabled()) {
            log.tracef("Elapsed time: %dms", elapsed);
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
}
