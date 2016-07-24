package com.joust.backend.web;

import java.io.File;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import de.javakaffee.web.msm.serializer.kryo.KryoTranscoderFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AppMain {

  public static void main(String[] args) throws Exception {

    Map<String, String> env = System.getenv();

    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir("target");

    String webPort = env.getOrDefault("PORT", "8080");

    File baseDir = new File(System.getProperty("project.basedir", "target/standalone"));

    tomcat.setPort(Integer.valueOf(webPort));

    File webapp = new File(baseDir, "webapp");

    Context ctx = tomcat.addWebapp("/", webapp.getAbsolutePath());

    WebResourceRoot resources = new StandardRoot(ctx);

    ctx.setResources(resources);

    setSessionManager(ctx);

    tomcat.start();
    tomcat.getServer().await();
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
