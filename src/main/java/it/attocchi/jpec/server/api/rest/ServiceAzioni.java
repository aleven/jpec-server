package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.bl.NotificaPecBL;
import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.utils.Crono;
import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;
import java.util.List;

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
			
			StringBuffer sb = new StringBuffer();
			
			Crono.start("invia");
			List<String> messaggiInviati = MessaggioPecBL.inviaMessaggiInCoda(getContextEmf(), "REST.ANONYMOUS");
			sb.append(Crono.stopAndLog("invia"));
			sb.append("\n");
			
			Crono.start("importa");
			MessaggioPecBL.importaNuoviMessaggi(getContextEmf(), "REST.ANONYMOUS");
			sb.append(Crono.stopAndLog("importa"));
			sb.append("\n");
			
			Crono.start("aggiorna");
			MessaggioPecBL.aggiornaStatoMessaggi(getContextEmf(), "REST.ANONYMOUS");
			sb.append(Crono.stopAndLog("aggiorna"));
			sb.append("\n");
			
			Crono.start("notifiche");
			NotificaPecBL.inviaNotifiche(getContextEmf(), "REST.ANONYMOUS", false, null);
			sb.append(Crono.stopAndLog("notifiche"));
			sb.append("\n");
			
			sb.append(new Date().toString());
			
			response = Response.ok(sb.toString(), MediaType.TEXT_PLAIN).build();
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

	@GET
	@Path("/aggiornastato")
	// @Produces(MediaType.TEXT_PLAIN)
	public Response doAggiornaStato() {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			MessaggioPecBL.aggiornaStatoMessaggi(getContextEmf(), "REST.ANONYMOUS");
			response = Response.ok(new Date().toString(), MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}
	
	@GET
	@Path("/invianotifiche")
	// @Produces(MediaType.TEXT_PLAIN)
	public Response doInviaNotifiche() {
		Response response = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			NotificaPecBL.inviaNotifiche(getContextEmf(), "REST.ANONYMOUS", false, "");
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
			List<String> messaggiInviati = MessaggioPecBL.inviaMessaggiInCoda(getContextEmf(), "REST.ANONYMOUS");
			response = Response.ok(messaggiInviati, MediaType.TEXT_PLAIN).build();
		} catch (PecException ex) {
			logger.error("PRECONDITION_FAILED", ex);
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();			
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
