package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.NotificaPec;
import it.attocchi.jpec.server.entities.filters.NotificaPecFilter;
import it.attocchi.mail.utils.MailSender;
import it.attocchi.mail.utils.items.MailHeader;
import it.attocchi.utils.DateUtilsLT;
import it.attocchi.utils.ListUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.SendFailedException;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.log4j.Logger;

public class NotificaPecBL {

	public enum TipoNotifica {
		INOLTRO,
		CAMBIO_STATO,
		NUOVO_INVIO,
		OBSOLETO,
		RICEZIONE,
		ERRORE
	}

	protected static final Logger logger = Logger.getLogger(NotificaPecBL.class.getName());

	public static List<NotificaPec> notificheDaInviare(EntityManagerFactory emf, String idUtente) throws Exception {

		NotificaPecFilter filtro = new NotificaPecFilter();
		filtro.setDaInviare(true);

		return JpaController.callFind(emf, NotificaPec.class, filtro);
	}

	public static void creaNotificaNuovoInvioAiResponsabili(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggio, String mailbox) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INVIO_DESTINATARI, mailbox);
		String protocollo = messaggio.getProtocollo();

		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_INVIO, mailbox);
		// "INVIO POSTA CERTIFICATA: " + messaggio.getOggetto()
		String oggetto = messaggio.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + oggetto;
		}

		salva(emf, transactionController, idUtente, messaggio.getId(), TipoNotifica.NUOVO_INVIO, destinatari, oggetto, messaggio.getMessaggio(), protocollo, null);

	}

	public static void creaNotificaCambioStatoAdUtente(EntityManagerFactory emf, JpaController transactionController, long idUtente, String stato, MessaggioPec messaggioStato, MessaggioPec messaggioCorrispondente, String mailbox) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_CAMBIOSTATO_DESTINATARI, mailbox);
		if (StringUtils.isBlank(destinatari)) {
			destinatari = messaggioCorrispondente.getEmailMittente();
		}

		// lo stato contiene gia' i : alla fine
		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_CAMBIO_STATO, mailbox);
		// String oggetto = "MESSAGGIO " + stato + " " +
		// messaggioCorrispondente.getOggetto();
		String oggetto = messaggioCorrispondente.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + stato + " " + oggetto;
		}
		String protocollo = messaggioCorrispondente.getProtocollo();

		// String testo =
		// StringUtils.defaultIfEmpty(messaggioCorrispondente.getMessaggio(),
		// ConfigurazioneBL.getValueString(emf, chiave,
		// mailboxRequested)NotificaCambioStatoMessaggio());
		String testo = StringUtils.defaultIfEmpty(messaggioStato.getMessaggio(), ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_CAMBIOSTATO_MESSAGGIO, mailbox));

		if (ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_NOTITY_STATUS, mailbox)) {
			// if (!verificaNotificaGiaCreata(emf, transactionController,
			// oggetto, protocollo)) {
			if (!verificaNotificaGiaCreata(emf, transactionController, messaggioCorrispondente.getId(), TipoNotifica.CAMBIO_STATO, destinatari, stato)) {

				if (stato.equals(MessaggioPecBL.OGGETTO_CONSEGNA))
					/*
					 * Nel caso di Cambio stato di Consegna Alleghiamo anche
					 * l'EML
					 */
					salva(emf, transactionController, idUtente, messaggioCorrispondente.getId(), TipoNotifica.CAMBIO_STATO, destinatari, oggetto, testo, protocollo, messaggioStato.getEmlFile());
				else
					salva(emf, transactionController, idUtente, messaggioCorrispondente.getId(), TipoNotifica.CAMBIO_STATO, destinatari, oggetto, testo, protocollo, null);
			}
		} else {
			logger.warn("Non Creo la Notifica di CambioStato isEnableNotifyStatus=false");
		}
	}

	public static void creaNotificaRicezioneAiResponsabili(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggio, String mailbox) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INVIO_DESTINATARI, mailbox);
		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_RICEZIONE, mailbox);

		// String oggetto = "RICEVUTA POSTA CERTIFICATA" + ": " +
		// messaggio.getOggetto();
		String oggetto = messaggio.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + oggetto;
		}

		String protocollo = "";
		String testo = StringUtils.defaultIfEmpty(messaggio.getMessaggio(), ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_RICEZIONE_MESSAGGIO, mailbox));

		salva(emf, transactionController, idUtente, messaggio.getId(), TipoNotifica.RICEZIONE, destinatari, oggetto, testo, protocollo, messaggio.getEmlFile());
	}

	public static void creaNotificaErroreAiResponsabili(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggio, String mailbox, String dettaglioErrore, String emlDaAllegare) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INVIO_DESTINATARI, mailbox);
		String oggettoNotifica = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_ERRORE_OGGETTO, mailbox);
		String prefisso = TipoNotifica.ERRORE.name();

		String oggettoMessaggio = messaggio.getOggetto();
		if (StringUtils.isBlank(oggettoNotifica)) {
			oggettoNotifica = String.format("%s: %s", prefisso, oggettoMessaggio);
		}

		String protocolloOMessageId = (StringUtils.isBlank(messaggio.getProtocollo())) ? messaggio.getMessageID() : messaggio.getProtocollo();
		String testoNotifica = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_ERRORE_MESSAGGIO, mailbox);
		if (StringUtils.isNotBlank(dettaglioErrore)) {
			testoNotifica = StringUtils.isBlank(testoNotifica) ? dettaglioErrore : testoNotifica + "\n\n" + dettaglioErrore;
		}

		salva(emf, transactionController, idUtente, messaggio.getId(), TipoNotifica.ERRORE, destinatari, oggettoNotifica, testoNotifica, protocolloOMessageId, emlDaAllegare);
	}

	public static void creaNotificaObsoletoAiResponsabili(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggio, String mailbox) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INVIO_DESTINATARI, mailbox);

		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_OBSOLETO, mailbox);
		// String oggetto = "MESSAGGIO SENZA ESITO" + ": " +
		// messaggio.getOggetto();
		String oggetto = messaggio.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + oggetto;
		}
		String protocollo = messaggio.getProtocollo();

		// String testo = StringUtils.defaultIfEmpty(messaggio.getMessaggio(),
		// ConfigurazioneBL.getValueString(emf, chiave,
		// mailboxRequested)NotificaObsoletoMessaggio());
		String testo = StringUtils.defaultIfEmpty(null, ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_OBSOLETO_MESSAGGIO, mailbox));

		if (!verificaNotificaGiaCreata(emf, transactionController, messaggio.getId(), TipoNotifica.OBSOLETO, destinatari)) {
			// salva(emf, transactionController, idUtente, messaggio.getId(),
			// TipoNotifica.OBSOLETO, destinatari, oggetto, testo, protocollo,
			// messaggio.getEmlFile());
			salva(emf, transactionController, idUtente, messaggio.getId(), TipoNotifica.OBSOLETO, destinatari, oggetto, testo, protocollo, null);
		}
	}

	public static void creaNotificaObsoletoAlMittente(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggio, String mailbox) throws Exception {

		String destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_CAMBIOSTATO_DESTINATARI, mailbox);
		if (StringUtils.isBlank(destinatari)) {
			destinatari = messaggio.getEmailMittente();
		}

		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_OBSOLETO, mailbox);
		// String oggetto = "MESSAGGIO SENZA ESITO" + ": " +
		// messaggio.getOggetto();
		String oggetto = messaggio.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + oggetto;
		}

		String protocollo = messaggio.getProtocollo();

		// String testo = StringUtils.defaultIfEmpty(messaggio.getMessaggio(),
		// ConfigurazioneBL.getValueString(emf, chiave,
		// mailboxRequested)NotificaObsoletoMessaggio());
		String testo = StringUtils.defaultIfEmpty(null, ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_OBSOLETO_MESSAGGIO, mailbox));

		if (!verificaNotificaGiaCreata(emf, transactionController, messaggio.getId(), TipoNotifica.OBSOLETO, destinatari)) {
			// salva(emf, transactionController, idUtente, messaggio.getId(),
			// TipoNotifica.OBSOLETO, destinatari, oggetto, testo, protocollo,
			// messaggio.getEmlFile());
			salva(emf, transactionController, idUtente, messaggio.getId(), TipoNotifica.OBSOLETO, destinatari, oggetto, testo, protocollo, null);
		}
	}

	public static void salva(EntityManagerFactory emf, JpaController transactionController, long idUtente, long idMessaggioPadre, TipoNotifica tipo, String destinatari, String oggetto, String messaggio, String protocollo, String allegati) throws Exception {

		NotificaPec notifica = new NotificaPec();
		notifica.markAsCreated(idUtente);

		notifica.setTipo(tipo.name());
		notifica.setIdMessaggioPadre(idMessaggioPadre);

		notifica.setOggetto(oggetto);
		notifica.setDestinatari(destinatari);
		notifica.setMessaggio(messaggio);
		notifica.setAllegati(allegati);
		notifica.setProtocollo(protocollo);

		if (transactionController == null) {
			JpaController.callInsert(emf, notifica);
		} else {
			transactionController.insert(notifica);
		}
	}

	public static synchronized void inviaNotifiche(EntityManagerFactory emf, String currentUser, boolean useCurrentUserAsSender, String mailbox) throws Exception {
		// boolean res = false;
		List<NotificaPec> notificheDaInviare = notificheDaInviare(emf, currentUser);
		for (NotificaPec notifica : notificheDaInviare) {
			// in caso di errore solleva eccezione ed interrompe il ciclo
			inviaNotifica(emf, notifica, currentUser, useCurrentUserAsSender, mailbox);
		}
		// res = true;
		// return res;
	}

	private static boolean verificaNotificaGiaCreata(EntityManagerFactory emf, JpaController transactionController, long idMessaggioPadre, TipoNotifica tipo, String destinatari) throws Exception {
		return verificaNotificaGiaCreata(emf, transactionController, idMessaggioPadre, tipo, destinatari, "");
	}

	/**
	 * Usa il nuovo modo di verificare la notifica controllando
	 * idMessaggioPadre, il Tipo ed i Destinatari
	 * 
	 * @param emf
	 * @param transactionController
	 * @param tipo
	 * @param idMessaggioPadre
	 * @return
	 * @throws Exception
	 */
	private static boolean verificaNotificaGiaCreata(EntityManagerFactory emf, JpaController transactionController, long idMessaggioPadre, TipoNotifica tipo, String destinatari, String oggetto) throws Exception {
		boolean res = false;

		NotificaPecFilter f = new NotificaPecFilter();
		f.setTipo(tipo.name());
		f.setIdMessaggioPadre(idMessaggioPadre);
		f.setDestinatari(destinatari);
		f.setOggetto(oggetto);

		NotificaPec notificaEsistente = null;
		if (transactionController == null)
			notificaEsistente = JpaController.callFindFirst(emf, NotificaPec.class, f);
		else
			notificaEsistente = transactionController.findFirst(NotificaPec.class, f);

		res = (notificaEsistente != null);

		logger.info(String.format("verificaNotificaGiaCreata per %s, %s, %s, %s = %s", tipo.name(), idMessaggioPadre, destinatari, oggetto, res));

		return res;
	}

	// private static boolean verificaNotificaGiaCreata(EntityManagerFactory
	// emf, JpaController transactionController, String oggetto, String
	// protocollo) throws Exception {
	// return verificaNotificaGiaCreata(emf, transactionController, oggetto,
	// protocollo, null);
	// }
	//
	// private static boolean verificaNotificaGiaCreata(EntityManagerFactory
	// emf, JpaController transactionController, String oggetto, String
	// protocollo, String destinatari) throws Exception {
	// boolean res = false;
	//
	// NotificaFilter f = new NotificaFilter();
	// f.setOggetto(oggetto);
	// f.setProtocollo(protocollo);
	// f.setDestinatari(destinatari);
	//
	// Notifica n = null;
	// if (transactionController == null)
	// n = JpaController.callFindFirst(emf, Notifica.class, f);
	// else
	// n = transactionController.findFirst(Notifica.class, f);
	//
	// res = n != null;
	//
	// return res;
	// }

	public static boolean creaNotificaInoltro(EntityManagerFactory emf, JpaController transactionController, long idUtente, MessaggioPec messaggioInoltro, String mailbox) throws Exception {
		boolean res = false;

		String destinatari = messaggioInoltro.getInoltratoDestinatari();

		if (StringUtils.isNotBlank(ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INOLTRO_DESTINATARI, mailbox))) {
			if (StringUtils.isBlank(destinatari))
				destinatari = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INOLTRO_DESTINATARI, mailbox);
			else
				destinatari = destinatari + ", " + ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_INOLTRO_DESTINATARI, mailbox);
		}

		messaggioInoltro.setInoltratoDestinatari(destinatari);

		String prefisso = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICA_PREFISSO_INOLTRO, mailbox);
		// String oggetto = "INOLTRO" + ": " + messaggioInoltro.getOggetto();
		String oggetto = messaggioInoltro.getOggetto();
		if (StringUtils.isNotBlank(prefisso)) {
			oggetto = prefisso + oggetto;
		}
		String protocollo = messaggioInoltro.getProtocollo();

		String allegato = messaggioInoltro.getEmlFile();
		if (StringUtils.isNotBlank(messaggioInoltro.getPostacertFile())) {
			allegato = messaggioInoltro.getPostacertFile();
		}

		/* verifica presenza eml del contentuto da inoltrare */
		if (StringUtils.isNotBlank(allegato)) {
			if (!new File(allegato).exists()) {
				throw new Exception("Impossibile recuperare il contenuto del messaggio di posta certificata.", new FileNotFoundException(allegato));
			}
		}

		if (!verificaNotificaGiaCreata(emf, null, messaggioInoltro.getId(), TipoNotifica.INOLTRO, destinatari)) {
			salva(emf, null, idUtente, messaggioInoltro.getId(), TipoNotifica.INOLTRO, destinatari, oggetto, messaggioInoltro.getMessaggio(), protocollo, allegato);
			res = true;
		} else {
			logger.warn("Notifica gia' creata");
		}

		return res;
	}

	public static synchronized void inviaNotifica(EntityManagerFactory emf, NotificaPec notifica, String currentUser, boolean useCurrentUserAsSender, String mailbox) throws Exception {

		ConfigurazioneBL.resetCurrent();

		if (ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_NOTITY_SEND, mailbox)) {

			String smtpServer = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_SERVER, mailbox);
			int smtpPort = ConfigurazioneBL.getValueInt(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_PORT, mailbox);
			String senderEmail = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SENDER_EMAIL, mailbox);
			String senderName = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SENDER_NAME, mailbox);
			boolean enableSSL = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_SSL, mailbox);
			boolean enableSSLNoCertCheck = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_SSLNOCHECK, mailbox);
			String smtpUsername = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_USERNAME, mailbox);
			String smtpPassword = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_NOTIFICHE_SMTP_PASSWORD, mailbox);

			if (currentUser != null && useCurrentUserAsSender) {
				senderEmail = StringUtils.defaultIfBlank(currentUser, senderEmail);
				senderName = StringUtils.defaultIfBlank(currentUser, senderName);
			}

			// String smtpUsername =
			// Configurazione.getCurrent(emf).getSmtpUsernameNotifiche();
			// String smtpPassword =
			// Configurazione.getCurrent(emf).getSmtpPasswordNotifiche();

			// String destinatari =
			// ConfigurazioneBL.getValueString(emf, chiave,
			// mailboxRequested)NotificaInvioDestinatari();
			String destinatari = notifica.getDestinatari();

			List<MailHeader> customHeaders = new ArrayList<MailHeader>();
			// customHeaders.add(new
			// MailHeader(MessaggioPecBL.HEADER_X_HYDRO_PROTOCOL,
			// notifica.getProtocollo()));

			// String oggetto =
			// Configurazione.getCurrent(emf).getNotificaInvioOggetto();
			// String corpo =
			// Configurazione.getCurrent(emf).getNotificaInvio();

			// MailSender m = MailSender.createMailSender(smtpServer, smtpPort,
			// senderEmail, senderName);
			MailSender m = MailSender.createMailSender(smtpServer, smtpPort, senderEmail, senderName, enableSSL, enableSSLNoCertCheck, smtpUsername, smtpPassword);

			// Allegati
			List<EmailAttachment> allegati = new ArrayList<EmailAttachment>();
			List<String> files = ListUtils.fromCommaSepared(notifica.getAllegati());
			if (files != null) {
				// for (String fileName : files) {
				// File file = new File(fileName);
				// if (file.exists()) {
				// EmailAttachment attachment = new EmailAttachment();
				// attachment.setName(file.getName());
				// attachment.setPath(file.getPath());
				// attachment.setDisposition(EmailAttachment.ATTACHMENT);
				//
				// allegati.add(attachment);
				// }
				// }

				allegati = MailSender.prepareAttachment(files);
			}

			try {
				m.sendMail(destinatari, null, null, notifica.getOggetto(), notifica.getMessaggio(), customHeaders, allegati, null);
				logger.info("Notifica Inviata " + notifica);
				notifica.setInviato(true);
				notifica.setDataInvio(DateUtilsLT.Now());
				notifica.setErrore("OK");
				JpaController.callUpdate(emf, notifica);

			} catch (Exception ex) {
				logger.error("Errore Invio Notifica " + notifica, ex);
				notifica.setInviato(false);
				notifica.setDataInvio(DateUtilsLT.Now());
				notifica.setErrore(ex.toString());
				JpaController.callUpdate(emf, notifica);
				throw new SendFailedException("Errore Invio Notifica", ex);
			}

			// res = true;
		} else {
			logger.warn("isEnableNotifySend false");
			notifica.setInviato(true);
			notifica.setErrore("INVIO DISABILITATO isEnableNotifySend");
			JpaController.callUpdate(emf, notifica);
		}
		// return res;
	}
}
