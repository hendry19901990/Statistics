package com.statistics.jetty;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

 

public class ApplicationResources extends Application {

	private Set<Class<?>> classesSet = new HashSet<Class<?>>();
	

	public ApplicationResources() {
		classesSet.add(UsageAverage.class);
	}

	public Set<Class<?>> getClasses() {
		return (classesSet);
	}

}