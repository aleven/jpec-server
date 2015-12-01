package it.attocchi.jpec.server.api.rest;

import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class Test extends RestBaseJpa2 {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return new Date().toString();
	}
}
