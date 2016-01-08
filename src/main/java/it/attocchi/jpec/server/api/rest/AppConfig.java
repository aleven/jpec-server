package it.attocchi.jpec.server.api.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
	
	public AppConfig() {
		packages("it.attocchi.jpec.server.api.rest");
		register(MultiPartFeature.class);
	}
	
}
