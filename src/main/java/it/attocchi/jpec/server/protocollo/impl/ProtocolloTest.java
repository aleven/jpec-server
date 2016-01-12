package it.attocchi.jpec.server.protocollo.impl;

import it.attocchi.jpec.server.protocollo.AbstractProtocollo;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloTest extends AbstractProtocollo {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloTest.class);

	@Override
	public String esegui() {
		logger.debug(this.getClass().getName());
		String protocolloRisposta = String.valueOf(new Date().getTime());
		return protocolloRisposta;
	}

}
