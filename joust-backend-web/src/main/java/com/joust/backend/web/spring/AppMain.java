package com.joust.backend.web.spring;

import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.web.SpringServletContainerInitializer;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import de.javakaffee.web.msm.serializer.kryo.KryoTranscoderFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMain {

  public static final String ROOT = "";

  private Tomcat tomcat;

  public AppMain(Tomcat tomcat) {
    this.tomcat = tomcat;
  }

  public AppMain init() {
    Map<String, String> env = System.getenv();

    // this makes it easy to clean up tomcat created files when running from
    // within a maven environment.
    tomcat.setBaseDir("target");
    tomcat.setPort(Integer.valueOf(env.getOrDefault("PORT", "8080")));

    Context ctx = tomcat.addContext(ROOT, null);

    ctx.addServletContainerInitializer(new SpringServletContainerInitializer(),
        Stream.of(JoustWebInitializer.class).collect(Collectors.toSet()));

    setSessionManager(ctx);
    return this;
  }

  public AppMain start() throws Exception {
    tomcat.start();
    return this;
  }

  public AppMain stop() throws Exception {
    tomcat.stop();
    return this;
  }

  public AppMain destroy() throws LifecycleException {
    tomcat.destroy();
    return this;
  }

  public AppMain await() {
    tomcat.getServer().await();
    return this;
  }

  public static void main(String... args) throws Exception {
    new AppMain(new Tomcat()).init().start().await();

  }

  private static void setSessionManager(Context ctx) {
    // The nature of this application does not require session management, much
    // less distributed session management. I'll leave this here in case someone
    // needs an example of how to do it but turning it off by default.

    Map<String, String> env = System.getenv();
    boolean distributedSessions = Boolean.parseBoolean(env.getOrDefault("DISTRIBUTED_SESSIONS", "false"));
    log.info("distributedSessions={}", distributedSessions);

    if (distributedSessions) {

      String memcachedServers = env.get("MEMCACHEDCLOUD_SERVERS");
      String memcachedUsername = env.get("MEMCACHEDCLOUD_USERNAME");
      String memcachedPassword = env.get("MEMCACHEDCLOUD_PASSWORD");

      MemcachedBackupSessionManager sessionManager = new MemcachedBackupSessionManager();
      sessionManager.setMemcachedNodes(memcachedServers);
      sessionManager.setUsername(memcachedUsername);
      sessionManager.setPassword(memcachedPassword);
      sessionManager.setSticky(true);
      sessionManager.setLockingMode("auto");
      sessionManager.setMemcachedProtocol("binary");
      sessionManager.setTranscoderFactoryClass(KryoTranscoderFactory.class.getName());
      sessionManager.setRequestUriIgnorePattern(".*\\.(ico|png|gif|jpg|css|js)$");

      ctx.setManager(sessionManager);
    }
  }
}
