package it.attocchi.jpec.server.protocollo.impl;

import it.attocchi.jpec.server.protocollo.AbstractAzione;
import it.attocchi.jpec.server.protocollo.AzioneEsito;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloTestErrore extends AbstractAzione {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloTestErrore.class);

	private String test = "";
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public AzioneEsito esegui() {
		logger.debug(this.getClass().getName());
		
		String messaggioErrore = String.valueOf("ERRORE" +  new Date().getTime());
		AzioneEsito res = AzioneEsito.errore(messaggioErrore, new Exception(messaggioErrore));
		
		res.logAndBuffer(logger, "TEST BUFFER ERRORE {}", new Date());
		
		return res;
	}
	


}
