package it.attocchi.jpec.server.api.rest;

import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.webappcommon.rest.RestBaseJpa2;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/azioni")
public class Azioni extends RestBaseJpa2 {

	protected static final Logger logger = LoggerFactory.getLogger(Azioni.class);

	@GET
	@Path("/inviaericevi")
	@Produces(MediaType.TEXT_PLAIN)
	public String doInviaeRicevi() {
		String res = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			MessaggioPecBL.importaNuoviMessaggi(getContextEmf(), "REST.ANONYMOUS");
			new Date().toString();
		} catch (Exception ex) {
			logger.error("doInviaeRicevi", ex);
			res = ex.getMessage();
		}
		return res;
	}

	@GET
	@Path("/ricevi")
	@Produces(MediaType.TEXT_PLAIN)
	public String doRicevi() {
		String res = null;
		try {
			logger.debug("{}", restServletContext.getContextPath());
			MessaggioPecBL.importaNuoviMessaggi(getContextEmf(), "REST.ANONYMOUS");
			new Date().toString();
		} catch (Exception ex) {
			logger.error("doRicevi", ex);
			res = ex.getMessage();
		}
		return res;
	}

	@POST
	@Path("/invia")
	@Produces(MediaType.TEXT_PLAIN)
	public String doInvia() {
		logger.debug("{}", restServletContext.getContextPath());
		throw new NotImplementedException("questa api non e' ancora disponibile");
	}

}
