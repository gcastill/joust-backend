package com.joust.backend.web;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import de.javakaffee.web.msm.serializer.kryo.KryoTranscoderFactory;

public class AppMain {

  public static void main(String[] args) throws Exception {

    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir("target");

    String webPort = System.getenv("PORT");
    if (webPort == null || webPort.isEmpty()) {
      webPort = "8080";
    }

    File baseDir = new File(System.getProperty("project.basedir", "joust-backend-web"));

    tomcat.setPort(Integer.valueOf(webPort));

    File webapp = new File(baseDir, "webapp");
    StandardContext ctx = (StandardContext) tomcat.addWebapp("", webapp.getAbsolutePath());

    // Declare an alternative location for your "WEB-INF/classes" dir
    // Servlet 3.0 annotation will work

    WebResourceRoot resources = new StandardRoot(ctx);

    ctx.setResources(resources);

    String memcachedServers = System.getenv("MEMCACHEDCLOUD_SERVERS");
    String memcachedUsername = System.getenv("MEMCACHEDCLOUD_USERNAME");
    String memcachedPassword = System.getenv("MEMCACHEDCLOUD_PASSWORD");

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

    // Define and bind web.xml file location.
    File configFile = new File(webapp, "WEB-INF/web.xml");
    ctx.setConfigFile(configFile.toURI().toURL());

    tomcat.start();
    tomcat.getServer().await();
  }
}
