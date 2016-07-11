package com.joust.backend.data.spring;

import java.util.HashMap;
import java.util.Map;

import org.flywaydb.core.internal.util.Location;
import org.flywaydb.core.internal.util.scanner.Resource;
import org.flywaydb.core.internal.util.scanner.classpath.ClassPathScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class FilesAsStringFactoryBean extends AbstractFactoryBean<Map<String, String>> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilesAsStringFactoryBean.class);
	private Location location = new Location("classpath:db/sql");

	private String prefix = "";
	private String suffix = ".sql";

	@Override
	protected Map<String, String> createInstance() throws Exception {

		ClassPathScanner scanner = new ClassPathScanner(getClass().getClassLoader());
		Resource[] resources = scanner.scanForResources(location, prefix, suffix);
		Map<String, String> result = new HashMap<>();
		for (Resource resource : resources) {
			LOGGER.info("found {}", resource.getLocation());
			result.put(resource.getFilename(), resource.loadAsString("utf-8"));
		}
	
		return result;
	}

	@Override
	public Class<?> getObjectType() {
		return Map.class;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
