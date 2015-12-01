package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.ConfigurazionePec;
import it.attocchi.jpec.server.entities.ConfigurazionePecEnum;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.MessaggioPec.Folder;
import it.attocchi.jpec.server.entities.MessaggioPec_;
import it.attocchi.jpec.server.entities.filters.MessaggioPecFilter;
import it.attocchi.mail.parts.EmailBody;
import it.attocchi.mail.utils.MailConnection;
import it.attocchi.mail.utils.MailUtils;
import it.attocchi.mail.utils.PecParser;
import it.attocchi.utils.ListUtils;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.mail.Header;
import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class MessaggioPecBL {

	static final String HEADER_MESSAGE_ID = "Message-ID";
	static final String HEADER_X_RICEVUTA = "X-Ricevuta";
	static final String HEADER_X_TIPO_RICEVUTA = "X-TipoRicevuta";
	static final String HEADER_X_RIFERIMENTO_MESSAGE_ID = "X-Riferimento-Message-ID";

//	public static final String STATO_ACCETTAZIONE = "ACCETTAZIONE:";
//	public static final String STATO_CONSEGNA = "CONSEGNA:";
//	public static final String STATO_ANOMALIA_MessaggioPec = "ANOMALIA MessaggioPec:";

//	public static final String OGGETTO_POSTA_CERTIFICATA = "POSTA CERTIFICATA:";

	protected static final Logger logger = Logger.getLogger(MessaggioPecBL.class.getName());

	/**
	 * Legge e Salva i nuovi Messaggi Ricevuti sul Server PEC
	 * 
	 * @param emf
	 * @param utente
	 * @return
	 * @throws Exception
	 */
	public static boolean importaNuoviMessaggi(EntityManagerFactory emf, String utente) throws Exception {
		boolean res = false;
		// List<MailMessage> popEmails = new ArrayList<MailMessage>();

		ConfigurazionePec.resetCurrent();

		if (ConfigurazionePec.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_EMAIL_CHECK)) {

			String popServer = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER);
			int popPort = ConfigurazionePec.getValueInt(emf, ConfigurazionePecEnum.PEC_SERVER_PORT);
			String popUsername = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_USERNAME);
			String popPassword = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_PASSWORD);
			boolean enablePopSSL = ConfigurazionePec.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_SSL);
			boolean enablePopSSLNoCertCheck = ConfigurazionePec.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_SSLNOCHECK);

			boolean enableEmlStore = ConfigurazionePec.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_EML_STORE);
			String emlStoreFolder = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER);
			String emlInStoreFolder = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_FOLDER_IN);

			boolean deleteMessageFromServer = ConfigurazionePec.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_DELETE_MESSAGE);

			logger.info("verifica messaggi da " + popServer + ":" + popPort);

			MailConnection server = null;
			server = new MailConnection(popServer, popPort, popUsername, popPassword);

			server.setEnableSSLNoCertCheck(enablePopSSLNoCertCheck);
			server.setEnableDeleteMessageFromServer(deleteMessageFromServer);

			String serverMode = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_MODE);
			if ("IMAP".equals(serverMode.toUpperCase())) {
				if (enablePopSSL)
					server.connectIMAPS();
				else
					server.connectIMAP();
			} else if ("POP3".equals(serverMode.toUpperCase())) {
				if (enablePopSSL)
					server.connectPOP3S();
				else
					server.connectPOP3();
			} else {
				throw new Exception("Impostare una modalita di connessione valida con il server (IMAP, POP3)");
			}

			// List<String> listaMessageID = JpaController.callFindProjection(emf, MessaggioPec.class, String.class, MessaggioPec_.messageID, null);
			
			// if (deleteMessageFromServer) {
			// server.setDeleteMessageFromServer(true);
			// }

			List<Message> mails = server.getMessages();

			logger.info(mails.size() + " messaggi nel server");
			logger.info("inizio verifica messaggi gia' importati...");
			int i = 0;
			for (Message mail : mails) {
				// MailMessage m = MailMessage.create(mail);
				
				String headerMessageId = "";
				String headerXRicevuta = "";
				String headerXTipoRicevuta = "";
				String headerXRiferimentoMessageId = "";

				logger.debug("--");
				logger.debug("getMessageNumber=" + mail.getMessageNumber());
				if (mail.getAllHeaders() != null) {
					Enumeration headers = mail.getAllHeaders();
					while (headers.hasMoreElements()) {
						Header h = (Header) headers.nextElement();
						logger.debug(" " + h.getName() + ":" + h.getValue());
						String headerName = h.getName();
						if (HEADER_MESSAGE_ID.equalsIgnoreCase(headerName)) {
							headerMessageId = h.getValue();
						} else if (HEADER_X_RICEVUTA.equalsIgnoreCase(headerName)) {
							headerXRicevuta = h.getValue();
						} else if (HEADER_X_TIPO_RICEVUTA.equalsIgnoreCase(headerName)) {
							headerXTipoRicevuta = h.getValue();							
						} else if (HEADER_X_RIFERIMENTO_MESSAGE_ID.equalsIgnoreCase(headerName)) {
							headerXRiferimentoMessageId = h.getValue();
						}
					}
				}
				logger.debug("getSubject=" + mail.getSubject());
				logger.debug(" decoded=" + javax.mail.internet.MimeUtility.decodeText(mail.getSubject()));

				// popEmails.add(m);

				/*
				 * NON IMPORTO MESSAGGI CHE HANNO LO STESSO MESSAGEID
				 */
				/* non posso guardare la data ricezione che e' sempre nulla */
				MessaggioPecFilter filtro = new MessaggioPecFilter();
				// filtro.setDataInvioOriginale(mail.getSentDate());
				// -- filtro.setDataRicezione(mail.getReceivedDate());
				// filtro.setOggetto(mail.getSubject());
				filtro.setMessageID(headerMessageId);
				
				/*
				 * CORREZZIONE VELOCE DEL 13/03/2013. Quando contrassegnato come
				 * archiviato altrimenti ritorna dentro come nuovo
				 */
				filtro.setIncludiEliminati(true);
				filtro.setMostraArchiviati(true);

				MessaggioPec MessaggioPec = JpaController.callFindFirst(emf, MessaggioPec.class, filtro);

				if (MessaggioPec == null) {

					// boolean saved = false;
					String pathFile = "";
					if (enableEmlStore) {
						pathFile = ArchivioEmlBL.salvaEmlRicevuto(emlStoreFolder, emlInStoreFolder, server, mail);
					}

					/* Importiamo il MessaggioPec */
					MessaggioPec nuovo = MessaggioPec.createNew(utente, Folder.IN);
					
					// dati header PEC
					nuovo.setMessageID(headerMessageId);
					nuovo.setxRicevuta(headerXRicevuta);
					nuovo.setxTipoRicevuta(headerXTipoRicevuta);
					nuovo.setxRiferimentoMessageID(headerXRiferimentoMessageId);

					nuovo.setOggetto(mail.getSubject());
					nuovo.setDataInvioOriginale(mail.getSentDate());
					nuovo.setDataRicezione(mail.getReceivedDate());

					EmailBody body = MailUtils.getBody(mail);
					nuovo.setMessaggio(body.getBody());

					nuovo.setEmailMittente(ListUtils.toCommaSeparedNoBracket(MailUtils.getAllSenders(mail)));
					nuovo.setDestinatari(ListUtils.toCommaSeparedNoBracket(MailUtils.getAllRecipents(mail)));

					/*
					 * Estrazione postacert.eml
					 */
					String postacertExtract = ConfigurazionePec.getValueString(emf, ConfigurazionePecEnum.PEC_POSTACERT_EXTRACT);
					if (StringUtils.isNotBlank(postacertExtract)) {
						/*
						 * Lo facciamo per quelle che entrano come PEC e non
						 * come ANOMALIE
						 */
						// if
						// (mail.getSubject().startsWith(OGGETTO_POSTA_CERTIFICATA))
						// {
						File path = new File(pathFile);
						path = new File(path.getParentFile(), FilenameUtils.removeExtension(path.getName()));
						if (!path.exists())
							path.mkdirs();
						File postacertFile = new File(path, postacertExtract);
						PecParser pecParser = new PecParser(postacertExtract, true, postacertFile);
						pecParser.dumpPart(mail);
						EmailBody bodyPostacert = pecParser.getTesto();
						nuovo.setPostacertFile(postacertFile.getPath());
						if (bodyPostacert != null) {
							nuovo.setPostacertBody(bodyPostacert.getBody());
							nuovo.setPostacertContentType(bodyPostacert.getContentType());
						}
						// }
					}

					if (StringUtils.isNotBlank(pathFile)) {
						nuovo.setEmlFile(pathFile);
					}
					JpaController.callInsert(emf, nuovo);

					/*
					 * Tutto e' andato bene ed ho salvato, se l'opzione e'
					 * attivo contrassegno il MessaggioPec da elminare
					 */
					if (deleteMessageFromServer) {
						server.markMessageDeleted(mail);
					}

					i++;
				}

			}
			logger.info(i + " nuovi messaggi importati");

			// if (deleteMessageFromServer) {
			// // server.deleteMessagesFromServer();
			// // server.ExpungeFolder();
			// }

			MailConnection.close(server);

			// addInfoMessage("Aggiornato");
			res = true;

		} else {
			logger.warn("check email disabled");
		}

		return res;
	}

	public static MessaggioPec getMessaggioPec(EntityManagerFactory emf, long idMessaggioPec) throws Exception {
		MessaggioPec res = null;

		res = JpaController.callFindById(emf, MessaggioPec.class, idMessaggioPec);

		return res;
	}

}
