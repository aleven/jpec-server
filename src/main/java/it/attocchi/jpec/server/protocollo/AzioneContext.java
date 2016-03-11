package it.attocchi.jpec.server.protocollo;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.RegolaPec;

import java.util.Properties;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzioneContext {

	protected final Logger logger = LoggerFactory.getLogger(AzioneContext.class);

	private EntityManagerFactory emf;
	private Message email;
	private MessaggioPec pec;
	private RegolaPec regola;
	private Properties configurazioneMailbox;

	public AzioneContext(EntityManagerFactory emf, Message email, MessaggioPec pec, Properties configurazioneMailbox) {
		super();
		this.emf = emf;
		this.email = email;
		this.pec = pec;
		this.configurazioneMailbox = configurazioneMailbox;
	}

	public EntityManagerFactory getEmf() {
		return emf;
	}

	public Message getMessaggioEmail() {
		return email;
	}

	public RegolaPec getRegola() {
		return regola;
	}

	public Properties getConfigurazioneMailbox() {
		return configurazioneMailbox;
	}

	public MessaggioPec getPec() {
		return pec;
	}
}
