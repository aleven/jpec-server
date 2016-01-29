package it.attocchi.jpec.server.protocollo.impl;

import it.attocchi.jpec.server.protocollo.AbstractProtocollo;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloTest extends AbstractProtocollo {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloTest.class);

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
		String protocolloRisposta = String.valueOf(getTest() +  new Date().getTime());
		ProtocolloEsito res = ProtocolloEsito.ok(protocolloRisposta);
		return res;
	}
	


}
