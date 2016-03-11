package it.attocchi.jpec.server.protocollo.impl;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.protocollo.AbstractAzione;
import it.attocchi.jpec.server.protocollo.AzioneContext;
import it.attocchi.jpec.server.protocollo.AzioneEsito;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AllegaRicevuteTest extends AbstractAzione {

	protected final Logger logger = LoggerFactory.getLogger(AllegaRicevuteTest.class);

	@Override
	public AzioneEsito esegui() {
		logger.debug(this.getClass().getName());
		
		AzioneContext ctx = getContext();
		MessaggioPec ricevuta = ctx.getRicevuta();
		MessaggioPec pec = ctx.getMessaggioInviato();
		
		logger.info("questa ricevuta {} va allegata a {}", ricevuta, pec);
		logger.info("url a cui inviare {}", pec.getUrlDocumentale());
		
		return AzioneEsito.ok("", "");
	}
	


}
