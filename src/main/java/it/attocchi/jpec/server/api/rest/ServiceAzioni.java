package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.exceptions.PecException;
import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
			logger.error("INTERNAL_SERVER_ERROR", ex);
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
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@PUT
	@Path("/invia")
	public Response doInvia() {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			// MessaggioPecBL.inviaMessaggiInCoda NON IN CORSO (getContextEmf(),
			// "REST.ANONYMOUS");
			response = Response.ok(new Date().toString(), MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@PUT
	@Path("/invia/{idMessaggio}")
	public Response doInvia(@PathParam("idMessaggio") long idMessaggio) {
		Response response = null;
		try {
			logger.debug("{}/{}", restServletContext.getContextPath(), idMessaggio);
			String messageID = MessaggioPecBL.inviaMessaggio(getContextEmf(), idMessaggio, "REST.ANONYMOUS");
			response = Response.ok(messageID, MediaType.TEXT_PLAIN).build();
		} catch (PecException ex) {
			logger.error("PRECONDITION_FAILED", ex);
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

}
