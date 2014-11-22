package com.redhat.gss.ws;

import java.util.Random;
import javax.jws.WebService;
import org.jboss.logging.Logger;
import javax.jws.Oneway;

@WebService
public class HelloImpl {
  private static Logger log = Logger.getLogger(Hello.class);
  private static Random r = new Random();

  public void hello(String name) {
    String greeting = "Hello, " + name + "!";
    log.debug(greeting);
  }
}
