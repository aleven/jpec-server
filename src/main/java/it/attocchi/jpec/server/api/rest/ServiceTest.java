package it.attocchi.jpec.server.api.rest;

import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/test")
public class ServiceTest extends RestBaseJpa2 {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceTest.class);
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		logger.debug("{}", restServletContext.getContextPath());
		logger.debug("{}", uriInfo.getAbsolutePath());
		return new Date().toString();
	}
}
