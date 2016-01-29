package it.attocchi.jpec.server.protocollo.impl;

import it.attocchi.jpec.server.protocollo.AbstractProtocollo;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito.ProtocolloEsitoStato;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloTestErrore extends AbstractProtocollo {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloTestErrore.class);

	private String test = "";
	
	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public ProtocolloEsito esegui() {
		logger.debug(this.getClass().getName());
		
		String messaggioErrore = String.valueOf("ERRORE" +  new Date().getTime());
		ProtocolloEsito res = ProtocolloEsito.errore(messaggioErrore);		
		
		return res;
	}
	


}
