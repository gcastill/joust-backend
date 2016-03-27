package com.joust.be;

import java.io.File;

import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

public class BackendMain {

	public static void main(String[] args) throws Exception {

		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir("target");

		String webPort = System.getenv("PORT");
		if (webPort == null || webPort.isEmpty()) {
			webPort = "8080";
		}

		tomcat.setPort(Integer.valueOf(webPort));

		File webapp = new File("src/main/webapp/");
		StandardContext ctx = (StandardContext) tomcat.addWebapp("", webapp.getAbsolutePath());

		// Declare an alternative location for your "WEB-INF/classes" dir
		// Servlet 3.0 annotation will work
		File classes = new File("target/classes");
		WebResourceRoot resources = new StandardRoot(ctx);

		resources.addPreResources(new DirResourceSet(resources, "/WEB-INF/classes", classes.getAbsolutePath(), "/"));
		ctx.setResources(resources);

		// Define and bind web.xml file location.
		File configFile = new File(webapp, "WEB-INF/web.xml");
		ctx.setConfigFile(configFile.toURI().toURL());

		tomcat.start();
		tomcat.getServer().await();
	}
}
