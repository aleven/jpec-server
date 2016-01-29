package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpec.server.api.rest.data.UploadAllegatoRequest;
import it.attocchi.jpec.server.api.rest.data.UploadAllegatoResponse;
import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.entities.AllegatoPec;
import it.attocchi.jpec.server.exceptions.PecException;
import it.webappcommon.rest.RestBaseJpa2;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

@Path("/allegati")
public class ResourceAllegati extends RestBaseJpa2 {

	protected static final Logger logger = LoggerFactory.getLogger(ResourceAllegati.class);

	// @GET
	// @Path("/{id}/")
	// @Produces(MediaType.APPLICATION_JSON)
	// public Response getMessaggio() {
	// Response response = null;
	// try {
	// logger.debug("{}", restServletContext.getContextPath());
	// response = Response.ok(new Date().toString(),
	// MediaType.TEXT_PLAIN).build();
	// } catch (Exception ex) {
	// logger.error("doRicevi", ex);
	// response =
	// Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex.getMessage()).type(MediaType.TEXT_PLAIN).build();
	// }
	// return response;
	// }

	@POST
	@Path("/upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response upload(@FormDataParam("allegato") UploadAllegatoRequest allegatoRequest, @FormDataParam("file") InputStream file, @FormDataParam("file") FormDataContentDisposition fileDisposition) {
		Response response = null;
		UploadAllegatoResponse responseData = new UploadAllegatoResponse();
		try {
			logger.debug("{}", restServletContext.getContextPath());
			logger.debug("{}", new Gson().toJson(allegatoRequest));

			AllegatoPec allegato = MessaggioPecBL.saveFile(getContextEmf(), allegatoRequest, file);

			responseData.setId(allegato.getId());
			response = Response.ok(responseData).build();

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
