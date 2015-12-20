package it.attocchi.jpec.server.protocollo;

import it.attocchi.jpec.server.entities.RegolaPec;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloContext {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloHelper.class);

	private EntityManagerFactory emf;
	private Message messaggioEmail;
	private RegolaPec regola;
	// private String protocolloImpl;

	public ProtocolloContext(EntityManagerFactory emf, Message messaggioEmail) {
		super();
		this.emf = emf;
		// this.protocolloImpl = protocolloImpl;
		this.messaggioEmail = messaggioEmail;
	}

	public EntityManagerFactory getEmf() {
		return emf;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public Message getMessaggioEmail() {
		return messaggioEmail;
	}

	public void setMessaggioEmail(Message messaggioEmail) {
		this.messaggioEmail = messaggioEmail;
	}

	public RegolaPec getRegola() {
		return regola;
	}

	public void setRegola(RegolaPec regola) {
		this.regola = regola;
	}

//	public String getProtocolloImpl() {
//		return protocolloImpl;
//	}
//
//	public void setProtocolloImpl(String protocolloImpl) {
//		this.protocolloImpl = protocolloImpl;
//	}

}
