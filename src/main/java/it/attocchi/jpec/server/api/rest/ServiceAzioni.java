package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpec.server.api.rest.data.NuovoMessaggioRequest;
import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/azioni")
public class ServiceAzioni extends RestBaseJpa2 {

	protected static final Logger logger = LoggerFactory.getLogger(ServiceAzioni.class);

	@GET
	@Path("/inviaericevi")
	// @Produces(MediaType.TEXT_PLAIN)
	public Response doInviaeRicevi() {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			MessaggioPecBL.importaNuoviMessaggi(getContextEmf(), "REST.ANONYMOUS");
			response = Response.ok(new Date().toString(), MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("doInviaeRicevi", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@GET
	@Path("/ricevi")
	// @Produces(MediaType.TEXT_PLAIN)
	public Response doRicevi() {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			MessaggioPecBL.importaNuoviMessaggi(getContextEmf(), "REST.ANONYMOUS");
			response = Response.ok(new Date().toString(), MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("doRicevi", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@POST
	@Path("/invia")
	public Response doInvia(NuovoMessaggioRequest requestData) {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			// MessaggioPecBL.inviaMessaggiInCoda NON IN CORSO (getContextEmf(), "REST.ANONYMOUS");
			response = Response.ok(new Date().toString(), MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("doInvia", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

}
