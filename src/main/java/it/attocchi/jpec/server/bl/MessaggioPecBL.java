package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.MessaggioPec.Folder;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.filters.MessaggioPecFilter;
import it.attocchi.jpec.server.protocollo.ProtocolloHelper;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessaggioPecBL {

	static final String HEADER_MESSAGE_ID = "Message-ID";
	static final String HEADER_X_RICEVUTA = "X-Ricevuta";
	static final String HEADER_X_TIPO_RICEVUTA = "X-TipoRicevuta";
	static final String HEADER_X_RIFERIMENTO_MESSAGE_ID = "X-Riferimento-Message-ID";

	// public static final String STATO_ACCETTAZIONE = "ACCETTAZIONE:";
	// public static final String STATO_CONSEGNA = "CONSEGNA:";
	// public static final String STATO_ANOMALIA_MessaggioPec =
	// "ANOMALIA MessaggioPec:";

	public static final String OGGETTO_POSTA_CERTIFICATA = "POSTA CERTIFICATA:";

	protected static final Logger logger = LoggerFactory.getLogger(MessaggioPecBL.class);

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

		ConfigurazioneBL.resetCurrent();

		if (ConfigurazioneBL.getValueBooleanDB(emf, ConfigurazionePecEnum.PEC_ENABLE_EMAIL_CHECK)) {

			for (String mailboxName : ConfigurazioneBL.getAllMailboxes(emf)) {
				logger.info("verifica mailbox {}", mailboxName);

				String popServer = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER, mailboxName);
				int popPort = ConfigurazioneBL.getValueInt(emf, ConfigurazionePecEnum.PEC_SERVER_PORT, mailboxName);
				String popUsername = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_USERNAME, mailboxName);
				String popPassword = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_PASSWORD, mailboxName);
				boolean enablePopSSL = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_SSL, mailboxName);
				boolean enablePopSSLNoCertCheck = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_SSLNOCHECK, mailboxName);

				boolean enableEmlStore = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_EML_STORE, mailboxName);
				String emlStoreFolder = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER, mailboxName);
				String emlInStoreFolder = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_FOLDER_IN, mailboxName);

				boolean deleteMessageFromServer = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SERVER_DELETE_MESSAGE, mailboxName);

				logger.info("verifica messaggi da " + popServer + ":" + popPort);

				MailConnection server = null;
				try {
					server = new MailConnection(popServer, popPort, popUsername, popPassword);

					server.setEnableSSLNoCertCheck(enablePopSSLNoCertCheck);
					server.setEnableDeleteMessageFromServer(deleteMessageFromServer);

					String serverMode = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SERVER_MODE, mailboxName);
					if ("IMAP".equals(serverMode.toUpperCase())) {
						if (enablePopSSL) {
							logger.info("connessione via IMAPS...");
							server.connectIMAPS();
						} else {
							logger.info("connessione via IMAP...");
							server.connectIMAP();
						}
					} else if ("POP3".equals(serverMode.toUpperCase())) {
						if (enablePopSSL) {
							logger.info("connessione via POP3S...");
							server.connectPOP3S();
						} else {
							logger.info("connessione via POP3...");
							server.connectPOP3();
						}
					} else {
						throw new Exception("Impostare una modalita di connessione valida con il server (IMAP, POP3)");
					}

					// List<String> listaMessageID =
					// JpaController.callFindProjection(emf, MessaggioPec.class,
					// String.class, MessaggioPec_.messageID, null);

					// if (deleteMessageFromServer) {
					// server.setDeleteMessageFromServer(true);
					// }

					List<RegolaPec> regoleImporta = RegolaPecBL.regole(emf, RegolaPecEventoEnum.IMPORTA);

					List<Message> mails = server.getMessages();

					logger.info(mails.size() + " messaggi nel server");
					logger.info("inizio verifica messaggi gia' importati...");
					int i = 0;
					for (Message mail : mails) {
						// MailMessage m = MailMessage.create(mail);
						boolean regoleImportaConvalidate = RegolaPecBL.applicaRegole(emf, regoleImporta, mail);
						if (regoleImportaConvalidate) {

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
							 * NON IMPORTO MESSAGGI CHE HANNO LO STESSO
							 * MESSAGEID
							 */
							/*
							 * non posso guardare la data ricezione che e'
							 * sempre nulla
							 */
							MessaggioPecFilter filtro = new MessaggioPecFilter();
							// filtro.setDataInvioOriginale(mail.getSentDate());
							// --
							// filtro.setDataRicezione(mail.getReceivedDate());
							// filtro.setOggetto(mail.getSubject());
							filtro.setMessageID(headerMessageId);
							/*
							 * CORREZZIONE VELOCE DEL 13/03/2013. Quando
							 * contrassegnato come archiviato altrimenti ritorna
							 * dentro come nuovo
							 */
							filtro.setIncludiEliminati(true);
							filtro.setMostraArchiviati(true);

							MessaggioPec messaggioEsistente = JpaController.callFindFirst(emf, MessaggioPec.class, filtro);
							if (messaggioEsistente == null) {
								// boolean saved = false;
								String pathFile = "";
								if (enableEmlStore) {
									// pathFile =
									// ArchivioEmlBL.salvaEmlRicevuto(emlStoreFolder,
									// emlInStoreFolder, server, mail);
								}

								/* Importiamo il MessaggioPec */
								MessaggioPec messaggioPec = MessaggioPec.createNew(utente, Folder.IN, mailboxName);

								// dati header PEC
								messaggioPec.setMessageID(headerMessageId);
								messaggioPec.setxRicevuta(headerXRicevuta);
								messaggioPec.setxTipoRicevuta(headerXTipoRicevuta);
								messaggioPec.setxRiferimentoMessageID(headerXRiferimentoMessageId);

								messaggioPec.setOggetto(mail.getSubject());
								messaggioPec.setDataInvioOriginale(mail.getSentDate());
								messaggioPec.setDataRicezione(mail.getReceivedDate());

								EmailBody body = MailUtils.getBody(mail);
								messaggioPec.setMessaggio(body.getBody());

								messaggioPec.setEmailMittente(ListUtils.toCommaSeparedNoBracket(MailUtils.getAllSenders(mail)));
								messaggioPec.setDestinatari(ListUtils.toCommaSeparedNoBracket(MailUtils.getAllRecipents(mail)));

								/* PROTOCOLLA */
								List<RegolaPec> regoleProtocolla = RegolaPecBL.regole(emf, RegolaPecEventoEnum.PROTOCOLLA);
								boolean regoleProtocollaConvalidate = RegolaPecBL.applicaRegole(emf, regoleProtocolla, mail);
								if (regoleProtocollaConvalidate) {
									String protocolloImplGenerico = ConfigurazioneBL.getValueStringDB(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL);
									logger.info("configurazione protocolo generica {}", protocolloImplGenerico);
									String protocolloImplMailbox = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL, mailboxName);
									logger.info("configurazione protocolo mailbox {}", protocolloImplMailbox);
									String protocolloImpl = (StringUtils.isNotBlank(protocolloImplMailbox)) ? protocolloImplMailbox : protocolloImplGenerico;
									if (StringUtils.isNotBlank(protocolloImpl)) {
										logger.info("utilizzo implementazione protocolo {}", protocolloImpl);
										String protocollo = new ProtocolloHelper().esegui(emf, protocolloImpl, mail);
										messaggioPec.setProtocollo(protocollo);
										logger.info("messaggio protocollato: {}", protocollo);
									} else {
										logger.warn("nessuna implementazione protocollo configurata");
									}
								}

								/*
								 * Estrazione postacert.eml
								 */
								String postacertExtract = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_POSTACERT_EXTRACT, mailboxName);
								if (StringUtils.isNotBlank(postacertExtract)) {
									/*
									 * Lo facciamo per quelle che entrano come
									 * PEC e non come ANOMALIE
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
									messaggioPec.setPostacertFile(postacertFile.getPath());
									if (bodyPostacert != null) {
										messaggioPec.setPostacertBody(bodyPostacert.getBody());
										messaggioPec.setPostacertContentType(bodyPostacert.getContentType());
									}
									// }
								}

								if (StringUtils.isNotBlank(pathFile)) {
									messaggioPec.setEmlFile(pathFile);
								}
								JpaController.callInsert(emf, messaggioPec);

								/*
								 * Tutto e' andato bene ed ho salvato, se
								 * l'opzione e' attivo contrassegno il
								 * MessaggioPec
								 */
								if (deleteMessageFromServer) {
									server.markMessageDeleted(mail);
								}

								i++;
							} else {
								logger.warn("messaggio {} gia' importato per mailbox {}", headerMessageId, messaggioEsistente.getMailbox());
							}
						} else {
							logger.warn("regole di importazione per il messaggio non sono state superate");
						}
					}
					logger.info(i + " nuovi messaggi importati");

					// if (deleteMessageFromServer) {
					// // server.deleteMessagesFromServer();
					// // server.ExpungeFolder();
					// }
				} catch (Exception ex) {
					logger.error("errore mailbox " + mailboxName, ex);
				} finally {
					MailConnection.close(server);
				}
			}

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
