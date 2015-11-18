package it.attocchi.jpec.server.api.rest;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class Test {

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getIt() {
		return new Date().toString();
	}
}
