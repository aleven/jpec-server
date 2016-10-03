package it.attocchi.jpec.server.protocollo;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.RegolaPec;

import java.util.Properties;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzioneContext {

	protected static final Logger logger = LoggerFactory.getLogger(AzioneContext.class);

	private EntityManagerFactory emf;
	private Message email;
	private MessaggioPec pec;
	private RegolaPec regola;

	private Properties configurazioneMailbox;
	private String mailboxName;
	private String mailboxPassword;

	private MessaggioPec ricevuta;
	private MessaggioPec messaggioInviato;

	private AzioneContext() {
		super();
	}

	/**
	 * 
	 * @param emf
	 * @param email
	 * @param pec
	 * @param mailboxName
	 * @param mailboxPassword serve passarla per eventuale uso successivo interno, ed esempio inviare le ricevute di segnatura attraverso la mailbox
	 * @return
	 */
	public static AzioneContext buildContextMessaggi(EntityManagerFactory emf, Message email, MessaggioPec pec, String mailboxName, String mailboxPassword) {
		AzioneContext res = new AzioneContext();
		res.emf = emf;
		res.email = email;
		res.pec = pec;
		res.mailboxName = mailboxName;
		res.mailboxPassword = mailboxPassword;
		logger.debug("buildContextMessaggi");
		logger.debug("email={}", email);
		logger.debug("pec={}", pec);
		logger.debug("configurazioneMailbox={}", mailboxName);

		return res;
	}

	public static AzioneContext buildContextMessaggi(EntityManagerFactory emf, Message email, MessaggioPec pec, Properties configurazioneMailbox) {
		AzioneContext res = new AzioneContext();
		res.emf = emf;
		res.email = email;
		res.pec = pec;
		res.configurazioneMailbox = configurazioneMailbox;
		logger.debug("buildContextRicevute");
		logger.debug("email={}", email);
		logger.debug("pec={}", pec);
		logger.debug("configurazioneMailbox={}", configurazioneMailbox);
		return res;
	}

	public static AzioneContext buildContextRicevute(EntityManagerFactory emf, MessaggioPec ricevuta, MessaggioPec messaggioInviato) {
		AzioneContext res = new AzioneContext();
		res.emf = emf;
		res.ricevuta = ricevuta;
		res.messaggioInviato = messaggioInviato;
		logger.debug("buildContextRicevute");
		logger.debug("ricevuta={}", ricevuta);
		logger.debug("messaggioInviato={}", messaggioInviato);
		return res;
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

	public Message getEmail() {
		return email;
	}

	public void setEmail(Message email) {
		this.email = email;
	}

	public String getMailboxName() {
		return mailboxName;
	}

	public void setMailboxName(String mailboxName) {
		this.mailboxName = mailboxName;
	}

	public MessaggioPec getRicevuta() {
		return ricevuta;
	}

	public void setRicevuta(MessaggioPec ricevuta) {
		this.ricevuta = ricevuta;
	}

	public MessaggioPec getMessaggioInviato() {
		return messaggioInviato;
	}

	public void setMessaggioInviato(MessaggioPec messaggioInviato) {
		this.messaggioInviato = messaggioInviato;
	}

	public void setEmf(EntityManagerFactory emf) {
		this.emf = emf;
	}

	public void setPec(MessaggioPec pec) {
		this.pec = pec;
	}

	public void setRegola(RegolaPec regola) {
		this.regola = regola;
	}

	public void setConfigurazioneMailbox(Properties configurazioneMailbox) {
		this.configurazioneMailbox = configurazioneMailbox;
	}
	
	public String getMailboxPassword() {
		return mailboxPassword;
	}
	
	public void setMailboxPassword(String password) {
		this.mailboxPassword = password;
	}
}
