package it.attocchi.jpec.server.api.rest;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.jaxrs.config.BeanConfig;

@ApplicationPath("/api")
public class AppConfig extends ResourceConfig {
	
	public AppConfig() {
		packages("it.attocchi.jpec.server.api.rest");
		register(MultiPartFeature.class);
		// swagger gen
		register(io.swagger.jaxrs.listing.ApiListingResource.class);
		register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
		// swagger conf
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setTitle("JPEC-SERVER REST API");
		beanConfig.setVersion("1.0.0");
		beanConfig.setSchemes(new String[] { "http" });
		beanConfig.setHost("localhost:8080");
		beanConfig.setBasePath("gdadocer/api");
		beanConfig.setResourcePackage("it.attocchi.jpec.server.api.rest");
		beanConfig.setScan(true);		
	}
	
}
