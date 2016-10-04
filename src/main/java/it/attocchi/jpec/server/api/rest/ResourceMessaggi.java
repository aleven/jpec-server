package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.api.rest.data.NuovoMessaggioRequest;
import it.attocchi.jpec.server.api.rest.data.NuovoMessaggioResponse;
import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.exceptions.PecException;
import it.webappcommon.rest.RestBaseJpa2;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/messaggi")
public class ResourceMessaggi extends RestBaseJpa2 {

	protected static final Logger logger = LoggerFactory.getLogger(ResourceMessaggi.class);

	@GET
	@Path("/{id}/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessaggio(@PathParam("id") long idMessaggio) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());

			// MessaggioPec messaggio =
			// JpaController.callFindById(getContextEmf(), MessaggioPec.class,
			// idMessaggio);
			MessaggioPec messaggio = getMessaggioFromDb(idMessaggio);

			response = Response.ok(new Gson().toJson(messaggio)).build();
		} catch (Exception ex) {
			logger.error("doRicevi", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response nuovo(NuovoMessaggioRequest requestData) {
		Response response = null;
		NuovoMessaggioResponse responseData = new NuovoMessaggioResponse();
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());
			logger.debug("{}", new Gson().toJson(requestData));

			MessaggioPec messaggioCreato = MessaggioPecBL.creaMessaggio(getContextEmf(), requestData, "REST.ANONYMOUS");

			// MessaggioPecBL.inviaEmail(getContextEmf(), null, null,
			// "REST.ANONYMOUS");

			responseData.setMessageId(messaggioCreato.getId());
			URI uri = UriBuilder.fromUri(uriInfo.getAbsolutePath()).path("{id}").build(responseData.getMessageId());
			// URI uri =
			// URI.create("http://stackoverflow.com/questions/24968448");
			// Link link = Link.fromUri(uri).rel("self").build();
			Link link = Link.fromUri(uri).build();
			responseData.setLink(link.getUri().toString());

			response = Response.ok(responseData).build();

			// URI messageUri = new URI("/test/" + new Date().getTime());
			// response = Response.created(messageUri).build();

			// throw new
			// NotImplementedException("questa api non e' ancora disponibile");
			// } catch (AuthorizeException ex) {
			// logger.error("UNAUTHORIZED", ex);
			// response =
			// Response.status(Response.Status.UNAUTHORIZED).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
			// } catch (AS400UnavailableException ex) {
			// logger.error("SERVICE_UNAVAILABLE", ex);
			// response =
			// Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		} catch (PecException ex) {
			logger.error("PRECONDITION_FAILED", ex);
			response = Response.status(Response.Status.PRECONDITION_FAILED).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		} catch (Exception ex) {
			logger.error("INTERNAL_SERVER_ERROR", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	@GET
	@Path("/{id}/stato")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatoMessaggio(@PathParam("id") long idMessaggio) {
		Response response = null;
		try {
			logger.debug("{}", uriInfo.getAbsolutePath());

			MessaggioPec messaggio = getMessaggioFromDb(idMessaggio);
			String statoDescrizione = "";
			if (messaggio != null) {
				statoDescrizione = messaggio.getStatoDescrizione();
			} else {
				statoDescrizione = "Messaggio non trovato.";
			}

			response = Response.ok(new Gson().toJson(statoDescrizione)).build();
		} catch (Exception ex) {
			logger.error("doRicevi", ex);
			response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
		}
		return response;
	}

	private MessaggioPec getMessaggioFromDb(long idMessaggio) throws Exception {
		return JpaController.callFindById(getContextEmf(), MessaggioPec.class, idMessaggio);
	}

}
