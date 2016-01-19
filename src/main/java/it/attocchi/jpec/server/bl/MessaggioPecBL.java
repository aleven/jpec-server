package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.api.rest.data.NuovoMessaggioRequest;
import it.attocchi.jpec.server.api.rest.data.UploadAllegatoRequest;
import it.attocchi.jpec.server.entities.AllegatoPec;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.MessaggioPec.Folder;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.filters.AllegatoPecFilter;
import it.attocchi.jpec.server.entities.filters.MessaggioPecFilter;
import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito.ProtocolloEsitoStato;
import it.attocchi.jpec.server.protocollo.ProtocolloGenerico;
import it.attocchi.jpec.server.protocollo.ProtocolloHelper;
import it.attocchi.mail.parts.EmailBody;
import it.attocchi.mail.utils.MailConnection;
import it.attocchi.mail.utils.MailSender;
import it.attocchi.mail.utils.MailUtils;
import it.attocchi.mail.utils.PecParser;
import it.attocchi.mail.utils.items.MailHeader;
import it.attocchi.utils.ListUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Header;
import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessaggioPecBL {

	public static final String HEADER_MESSAGE_ID = "Message-ID";
	public static final String HEADER_X_RICEVUTA = "X-Ricevuta";
	public static final String HEADER_X_TIPO_RICEVUTA = "X-TipoRicevuta";
	public static final String HEADER_X_RIFERIMENTO_MESSAGE_ID = "X-Riferimento-Message-ID";

	// public static final String OGGETTO_ACCETTAZIONE = "ACCETTAZIONE:";
	// public static final String OGGETTO_CONSEGNA = "CONSEGNA:";
	public static final String OGGETTO_ANOMALIA_MESSAGGIO = "ANOMALIA MESSAGGIO:";
	public static final String OGGETTO_POSTA_CERTIFICATA = "POSTA CERTIFICATA:";

	public static final String RICEVUTA_ACCETTAZIONE = "accettazione";
	public static final String RICEVUTA_CONSEGNA = "avvenuta-consegna";
	public static final String RICEVUTA_ANOMALIA_MESSAGGIO = "ANOMALIA MESSAGGIO:";

	protected static final Logger logger = LoggerFactory.getLogger(MessaggioPecBL.class);

	/**
	 * Legge e Salva i nuovi Messaggi Ricevuti sul Server PEC
	 * 
	 * @param emf
	 * @param utente
	 * @return
	 * @throws Exception
	 */
	public static synchronized boolean importaNuoviMessaggi(EntityManagerFactory emf, String utente) throws Exception {
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
						boolean regoleImportaConvalidate = RegolaPecBL.applicaRegole(emf, regoleImporta, mail, null);
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

								/* 
								 * PROTOCOLLA 
								 */
								boolean erroreInProtocollo = false;
								List<RegolaPec> regoleProtocolla = RegolaPecBL.regole(emf, RegolaPecEventoEnum.PROTOCOLLA);

								String protocolloImplGenerico = ConfigurazioneBL.getValueStringDB(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL);
								String protocolloImplMailbox = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL, mailboxName);
								String protocolloImpl = (StringUtils.isNotBlank(protocolloImplMailbox)) ? protocolloImplMailbox : protocolloImplGenerico;
								ProtocolloGenerico istanzaProtocollo = null;
								if (StringUtils.isNotBlank(protocolloImpl)) {
									Properties configurazioneMailbox = ConfigurazioneBL.getConfigurazione(mailboxName);
									logger.debug("implementazione protocolo: {}", protocolloImpl);
									istanzaProtocollo = ProtocolloHelper.creaIstanzaProtocollo(emf, protocolloImpl, mail, configurazioneMailbox);
								}
								
								Map<String, Object> regolaContext = new HashMap<String, Object>();
								regolaContext.put("protocollo", istanzaProtocollo);
								boolean regoleProtocollaConvalidate = RegolaPecBL.applicaRegole(emf, regoleProtocolla, mail, regolaContext);
								if (regoleProtocollaConvalidate) {
									if (istanzaProtocollo != null) {
										ProtocolloEsito esitoProtocollo = ProtocolloHelper.esegui(istanzaProtocollo);
										if (esitoProtocollo.stato == ProtocolloEsitoStato.OK) {
											messaggioPec.setProtocollo(esitoProtocollo.protocollo);
											logger.info("messaggio protocollato: {}", esitoProtocollo);
										} else {
											logger.error("si e' verificato un errore in fase di protocollazione: {}", esitoProtocollo.errore);
											// TODO: invia notifica ERRORE
											// PROTOCOLLO
											erroreInProtocollo = true;
										}
									} else {
										logger.warn("nessuna implementazione protocollo configurata");
									}
								}

								if (!erroreInProtocollo) {
									/*
									 * Estrazione postacert.eml
									 */
									String postacertExtract = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_POSTACERT_EXTRACT, mailboxName);
									if (StringUtils.isNotBlank(postacertExtract)) {
										/*
										 * Lo facciamo per quelle che entrano
										 * come PEC e non come ANOMALIE
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
									messaggioPec.markAsCreated(0);
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
									logger.warn("si e' verificato un errore in fase di protocollo ed il messaggio {}@{} non e' stato importato", headerMessageId, messaggioPec.getMailbox());
								}
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

	public static List<AllegatoPec> getAllegatiMessaggio(EntityManagerFactory emf, long idMessaggioPec) throws Exception {
		List<AllegatoPec> res = new ArrayList<AllegatoPec>();

		AllegatoPecFilter filtro = new AllegatoPecFilter();
		filtro.setIdMessaggio(idMessaggioPec);
		res = JpaController.callFind(emf, AllegatoPec.class, filtro);

		return res;
	}

	public static synchronized List<String> inviaMessaggiInCoda(EntityManagerFactory emf, String utente) throws Exception {
		List<String> res = new ArrayList<String>();
		MessaggioPecFilter filtro = new MessaggioPecFilter();
		filtro.setFolder(Folder.OUT);
		filtro.setSoloNonInviati(true);
		List<MessaggioPec> messaggiDaInviare = JpaController.callFind(emf, MessaggioPec.class, filtro);
		if (!messaggiDaInviare.isEmpty()) {
			for (MessaggioPec messaggioDaInviare : messaggiDaInviare) {
				try {
					String messageId = inviaMessaggio(emf, messaggioDaInviare.getId(), utente);
					if (StringUtils.isNotBlank(messageId)) {
						res.add(messageId);
					} else {
						logger.warn("Il messageId del messaggio inviato e' vuoto");
					}
				} catch (PecException ex) {
					logger.error("inviaMessaggiInCoda (" + messaggiDaInviare.toString() + ")", ex);
				}
			}
		}
		return res;
	}

	public static synchronized String inviaMessaggio(EntityManagerFactory emf, long idMessaggioPec, String utente) throws Exception {
		MessaggioPec messaggio = getMessaggioPec(emf, idMessaggioPec);
		List<AllegatoPec> allegati = getAllegatiMessaggio(emf, idMessaggioPec);

		if (messaggio.isInviato()) {
			throw new PecException("Il messaggio risulta già inviato.");
		}
		validateMessaggio(messaggio);

		return inviaEmail(emf, messaggio, allegati, utente);
	}

	private static void validateMessaggio(MessaggioPec messaggio) throws PecException {

		// if (StringUtils.isBlank(messaggio.getOggetto()) &&
		// StringUtils.isBlank(messaggio.getMessaggio()))
		// throw new
		// PecException("Specificare un oggetto ed un testo validi per il messaggio.");
		// if (StringUtils.isBlank(messaggio.getOggetto()))
		// throw new
		// PecException("Specificare un oggetto valido per il messaggio.");
		// if (StringUtils.isBlank(messaggio.getMessaggio()))
		// throw new
		// PecException("Specificare un testo valido per il messaggio.");

		if (StringUtils.isBlank(messaggio.getDestinatari()))
			throw new PecException("Specificare almeno un destinatario valido per il messaggio.");
	}

	/**
	 * Messaggio is UPDATED with EML file DATA, remember to store on DB after
	 * this
	 * 
	 * @param emf
	 * @param messaggio
	 * @param allegati
	 * @return
	 * @throws Exception
	 */
	private static synchronized String inviaEmail(EntityManagerFactory emf, MessaggioPec messaggio, List<AllegatoPec> allegati, String utente) throws Exception {
		String res = null;

		ConfigurazioneBL.resetCurrent();

		if (ConfigurazioneBL.getValueBooleanDB(emf, ConfigurazionePecEnum.PEC_ENABLE_EMAIL_SEND)) {

			String mailbox = messaggio.getMailbox();

			String smtpServer = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SMTP_SERVER, mailbox);
			int smtpPort = ConfigurazioneBL.getValueInt(emf, ConfigurazionePecEnum.PEC_SMTP_PORT, mailbox);
			String senderEmail = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SENDER_EMAIL, mailbox);
			String senderName = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SENDER_NAME, mailbox);
			boolean enableSSL = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SMTP_SSL, mailbox);
			boolean enableSSLNoCertCheck = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_SMTP_SSLNOCHECK, mailbox);
			String smtpUsername = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SMTP_USERNAME, mailbox);
			String smtpPassword = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_SMTP_PASSWORD, mailbox);

			boolean enableEmlStore = ConfigurazioneBL.getValueBoolean(emf, ConfigurazionePecEnum.PEC_ENABLE_EML_STORE, mailbox);
			String emlStoreFolder = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER, mailbox);
			// String emlOutStoreFolder = ConfigurazioneBL.getValueBoolean(emf,
			// ConfigurazionePecEnum.PEC_FOLDER_OUT, mailbox);

			MailSender m = MailSender.createMailSender(smtpServer, smtpPort, senderEmail, senderName, enableSSL, enableSSLNoCertCheck, smtpUsername, smtpPassword);

			List<EmailAttachment> attachments = null;
			if (allegati != null && allegati.size() > 0) {
				attachments = new ArrayList<EmailAttachment>();
				for (AllegatoPec a : allegati) {
					EmailAttachment attachment = new EmailAttachment();
					attachment.setName(a.getOnlyFileName());
					attachment.setPath(a.getStorePath());
					attachment.setDisposition(EmailAttachment.ATTACHMENT);

					attachments.add(attachment);
				}
			}

			List<MailHeader> customHeaders = new ArrayList<MailHeader>();
			// customHeaders.add(new MailHeader(HEADER_X_HYDRO_PROTOCOL,
			// messaggio.getProtocollo()));

			/* Archivio File */
			File storeEml = null;
			if (enableEmlStore) {
				// storeEml = ArchivioEmlBL.fileEmlInviato(emlStoreFolder,
				// emlOutStoreFolder, messaggio.getProtocollo());
				// if (storeEml != null) {
				// messaggio.setEmlFile(storeEml.getPath());
				// }
			}
			/**/

			String messageID = "";
			try {
				if (attachments == null || attachments.size() == 0) {
					logger.debug("invio messaggio senza allegati");
					messageID = m.sendMail(messaggio.getDestinatari(), null, null, messaggio.getOggetto(), messaggio.getMessaggio(), customHeaders, storeEml);
				} else {
					logger.debug("invio messaggio con allegati");
					messageID = m.sendMail(messaggio.getDestinatari(), null, null, messaggio.getOggetto(), messaggio.getMessaggio(), customHeaders, attachments, storeEml);
				}
				logger.debug("messaggio inviato {}", messageID);
				messaggio.setMessageID(messageID);
				messaggio.setInviato(true);
				messaggio.setDataInvio(new Date());
				messaggio.markAsUpdated(0);
				JpaController.callUpdate(emf, messaggio);
				logger.debug("aggiornato stato messaggio {}", messaggio);
			} catch (Exception ex) {
				logger.error("errore durante invio email", ex);
				messaggio.setErroreInvio(ex.getMessage());
				messaggio.markAsUpdated(0);
				JpaController.callUpdate(emf, messaggio);
				logger.debug("aggiornato stato messaggio {}", messaggio);
				throw ex;
			}

			res = messageID;
		} else {
			logger.warn("isEnableEmailSend false");
		}

		return res;
	}

	public static MessaggioPec creaMessaggio(EntityManagerFactory emf, NuovoMessaggioRequest requestData, String utente) throws PecException {
		MessaggioPec messaggio = null;
		JpaController controller = new JpaController(emf);
		try {
			controller.beginTransaction();

			/* Check mailbox */
			String mailbox = requestData.getMailbox();
			List<String> mailboxes = ConfigurazioneBL.getAllMailboxes(emf);
			boolean found = mailboxes.contains(mailbox);
			if (!found) {
				throw new PecException("La mailbox specificata non e' configurata (" + mailbox + ").");
			}

			/* create message */
			messaggio = MessaggioPec.createNew(utente, Folder.OUT, mailbox);
			// Assegnazione del Protocollo
			// String protocollo =
			// ProtocolloBL.getNextProtocolloFormat(controller, "[", "PEC",
			// "yy", "000", "]");
			messaggio.setProtocollo(requestData.getProtocollo());
			messaggio.setOggetto(requestData.getOggetto());
			messaggio.setMessaggio(requestData.getTestoMessaggio());

			messaggio.setDestinatari(ListUtils.toCommaSeparedNoBracket(requestData.getDestinatari()));
			messaggio.setDestinatariCC(ListUtils.toCommaSeparedNoBracket(requestData.getDestinatariCC()));
			messaggio.setDestinatariCCN(ListUtils.toCommaSeparedNoBracket(requestData.getDestinatariCCN()));

			/* Check data */
			validateMessaggio(messaggio);

			// messaggio.markAsCreated();
			controller.insert(messaggio);

			// if (ListUtils.isNotEmpty(requestData.getAllegati())) {
			// for (AllegatoRequest allegatoRequest : requestData.getAllegati())
			// {
			// // validateAllegato(allegatoRequest);
			//
			// AllegatoPec allegato = new AllegatoPec();
			// allegato.setFileName(allegatoRequest.getFileName());
			// allegato.setContetType(allegatoRequest.getContentType());
			// File f = File.createTempFile("PEC_", ".upload");
			//
			// byte[] decodedBytes =
			// Base64.decodeBase64(allegatoRequest.getFileBase64());
			// FileUtils.writeByteArrayToFile(f, decodedBytes);
			//
			// allegato.setStoreFileName(FilenameUtils.getName(f.getName()));
			// allegato.setStorePath(FilenameUtils.getFullPath((f.getName())));
			//
			// allegato.setIdMessaggio(messaggio.getId());
			//
			// controller.insert(allegato);
			// }
			// }

			controller.commitTransaction();

			// boolean resInvio = MessaggioBL.inviaEmail(getEmfShared(),
			// messaggio, allegati);
			//
			// // if (resInvio) {
			// // try {
			// //
			// // // MessaggioBL.inviaNotificaResponsabili(getEmfShared(),
			// // messaggio);
			// //
			// NotificaBL.notificaNuovoInvioAiResponsabili(getEmfShared(),
			// // controller, getIdUtenteLoggato(), messaggio);
			// //
			// // } catch (Exception ex) {
			// // addErrorMessage(ex);
			// // }
			// // }
			//
			// /* Potremmo non aver inviato l'email perch� disabilitato */
			// if (resInvio) {
			//
			// controller.beginTransaction();
			//
			// NotificaBL.creaNotificaNuovoInvioAiResponsabili(getEmfShared(),
			// controller, getIdUtenteLoggato(), messaggio);
			//
			// messaggio.setDataInvio(DateUtilsLT.Now());
			// messaggio.setInviato(true);
			// controller.update(messaggio);
			//
			// controller.commitTransaction();
			//
			// addInfoMessage("Messaggio Inviato");
			//
			// init();
			//
			// // MessaggioBL.importaNuoviMessaggi(getEmfShared(),
			// // getCurrentUser());
			//
			// res = true;
			// } else {
			// addWarnMessage("Messaggio Non Inviato!");
			// }

		} catch (PecException ex) {
			logger.error("creaMessaggio", ex);
			throw ex;

		} catch (Exception ex) {
			logger.error("creaMessaggio", ex);
			throw new PecException("Si e' verificato un errore durante il salvataggio del messaggio.", ex);

		} finally {
			JpaController.callRollback(controller);
			JpaController.callCloseEmf(controller);
		}

		return messaggio;
	}

	public static AllegatoPec saveFile(EntityManagerFactory emf, UploadAllegatoRequest allegatoRequest, InputStream file) throws Exception {
		validateAllegato(allegatoRequest, file);

		AllegatoPec allegato = new AllegatoPec();
		allegato.setFileName(allegatoRequest.getFileName());
		allegato.setContetType(allegatoRequest.getContentType());

		String folder = ConfigurazioneBL.getValueStringDB(emf, ConfigurazionePecEnum.PEC_ATTACH_STORE_FOLDER);
		File f = null;
		if (StringUtils.isBlank(folder)) {
			logger.warn("non e' configurata nessuna cartella per il salvataggio degli allegati, viene utilizzata la cartella predefinita.");
			f = File.createTempFile(FilenameUtils.getBaseName(allegatoRequest.getFileName()) + "_", "." + FilenameUtils.getExtension(allegatoRequest.getFileName()));
		} else {
			File d = new File(folder);
			if (!d.exists()) {
				throw new PecException("La cartella configurata per il salvataggio degli allegati non e' accessibile");
			}
			f = File.createTempFile(FilenameUtils.getBaseName(allegatoRequest.getFileName()) + "_", "." + FilenameUtils.getExtension(allegatoRequest.getFileName()), d);
		}

		FileUtils.copyInputStreamToFile(file, f);

		allegato.setStoreFileName(FilenameUtils.getName(f.getName()));
		allegato.setStorePath(f.getPath());

		allegato.setIdMessaggio(allegatoRequest.getIdMessaggio());
		allegato.markAsCreated(0);
		JpaController.callInsert(emf, allegato);

		return allegato;
	}

	private static void validateAllegato(UploadAllegatoRequest allegatoRequest, InputStream file) throws PecException {

		if (allegatoRequest == null)
			throw new PecException("Dati del file da allegare mancanti.");
		if (file == null)
			throw new PecException("Contenuto per il file da allegare mancante.");

		if (StringUtils.isBlank(allegatoRequest.getFileName()))
			throw new PecException("Specificare un nome valido per il file da allegare.");
		if (allegatoRequest.getIdMessaggio() <= 0)
			throw new PecException("Specificare un ID Messaggio Pec valido per il file da allegare.");

	}

	public static synchronized boolean aggiornaStatoMessaggi(EntityManagerFactory emf, String utente) throws Exception {
		boolean res = false;

		MessaggioPecFilter filtroRicevute = new MessaggioPecFilter();
		filtroRicevute.setFolder(Folder.IN);
		filtroRicevute.setSoloNonProcessati(true);
		filtroRicevute.setSoloRicevuteConRiferimento(true);

		MessaggioPecFilter filtroInviati = new MessaggioPecFilter();
		filtroInviati.setFolder(Folder.OUT);
		// filtroInviati.setEscludiConsegnati(true);
		filtroInviati.setConStatoDaAggiornare(true);
		filtroInviati.setConMessageID(true);

		List<MessaggioPec> ricevuteDaProcessare = JpaController.callFind(emf, MessaggioPec.class, filtroRicevute);
		List<MessaggioPec> messaggiInviati = JpaController.callFind(emf, MessaggioPec.class, filtroInviati);

		// TODO: probabilmente questa procedura va ottimizzata in caso di Tanti
		// Messaggi

		int i = 0;
		for (MessaggioPec ricevutaPec : ricevuteDaProcessare) {

			boolean messaggioCambioStato = false;
			/*
			 * Verifica LO STATO
			 */
			for (MessaggioPec messaggioInviato : messaggiInviati) {
				String oggettoRicevuto = ricevutaPec.getOggetto();
				String ricevuta = ricevutaPec.getxRicevuta();
				String ricevutaRiferimentoMessageId = ricevutaPec.getxRiferimentoMessageID();

				/*
				 * con il protocollo verifico se e' un messaggio di stato di
				 * questo messaggio inviato
				 */
				// if (StringUtils.isNotBlank(oggettoRicevuto) &&
				// StringUtils.isNotBlank(messaggioInviato.getProtocollo())) {
				// sono in presenza di una ricevuta in ingresso
				// la ricevuta ha un riferimento ad un messageid
				// sono in possessio del messaggio che ho inviato
				if (StringUtils.isNotBlank(ricevuta) && StringUtils.isNotBlank(ricevutaRiferimentoMessageId) && StringUtils.isNotBlank(messaggioInviato.getMessageID())) {
					if (ricevutaRiferimentoMessageId.equals(messaggioInviato.getMessageID())) {
						// if
						// (oggettoRicevuto.indexOf(messaggioInviato.getProtocollo())
						// > -1) {

						if (RICEVUTA_ACCETTAZIONE.equals(ricevuta)) {
							if (!messaggioInviato.isAccettato()) {
								messaggioInviato.setAccettato(true);
								messaggioInviato.setAccettatoIdMessaggio(ricevutaPec.getId());

								messaggioInviato.markAsUpdated(0);
								JpaController.callUpdate(emf, messaggioInviato);

								// String newPath =
								// ArchivioEmlBL.spostaEml("ACCETTAZIONE",
								// messaggioInviato, messaggioNuovoRicevuto);
								// if (StringUtils.isNotBlank(newPath)) {
								// messaggioNuovoRicevuto.setEmlFile(newPath);
								// }
								ricevutaPec.setProtocollo(messaggioInviato.getProtocollo());
								ricevutaPec.markAsUpdated(0);
								JpaController.callUpdate(emf, ricevutaPec);

								// // TODO: Guardare che non sia gia' stata
								// fatta
								// // una
								// // notifica in precedenza !!!
								// // if
								// //
								// (Configurazione.get(emf).isEnableNotifyStatus())
								// // {
								// logger.info("creaNotificaCambioStatoAdUtente "
								// + OGGETTO_ACCETTAZIONE);
								// NotificaBL.creaNotificaCambioStatoAdUtente(emf,
								// null, utente.getIdUtente(),
								// OGGETTO_ACCETTAZIONE, messaggioNuovoRicevuto,
								// messaggioInviato);
								// // }
							}

							messaggioCambioStato = true;
							break;

						} else if (RICEVUTA_CONSEGNA.equals(ricevuta)) {
							if (!messaggioInviato.isConsegnato()) {
								messaggioInviato.setConsegnato(true);
								messaggioInviato.setConsegnatoIdMessaggio(ricevutaPec.getId());
								messaggioInviato.markAsUpdated(0);
								JpaController.callUpdate(emf, messaggioInviato);

								// String newPath =
								// ArchivioEmlBL.spostaEml("CONSEGNA",
								// messaggioInviato, messaggioNuovoRicevuto);
								// if (StringUtils.isNotBlank(newPath)) {
								// messaggioNuovoRicevuto.setEmlFile(newPath);
								// }
								ricevutaPec.setProtocollo(messaggioInviato.getProtocollo());
								ricevutaPec.markAsUpdated(0);
								JpaController.callUpdate(emf, ricevutaPec);

								// // if
								// //
								// (Configurazione.get(emf).isEnableNotifyStatus())
								// // {
								// logger.info("creaNotificaCambioStatoAdUtente "
								// + OGGETTO_CONSEGNA);
								// NotificaBL.creaNotificaCambioStatoAdUtente(emf,
								// null, utente.getIdUtente(), OGGETTO_CONSEGNA,
								// messaggioNuovoRicevuto, messaggioInviato);
								// // }
							}
							messaggioCambioStato = true;
							break;

						} else if (oggettoRicevuto.startsWith(OGGETTO_ANOMALIA_MESSAGGIO)) {
							if (!messaggioInviato.isAnomalia()) {
								messaggioInviato.setAnomalia(true);
								messaggioInviato.setAnomaliaIdMessaggio(ricevutaPec.getId());
								messaggioInviato.markAsUpdated(0);
								JpaController.callUpdate(emf, messaggioInviato);

								// String newPath =
								// ArchivioEmlBL.spostaEml("ANOMALIA",
								// messaggioInviato, messaggioNuovoRicevuto);
								// if (StringUtils.isNotBlank(newPath)) {
								// messaggioNuovoRicevuto.setEmlFile(newPath);
								// }
								ricevutaPec.setProtocollo(messaggioInviato.getProtocollo());
								ricevutaPec.markAsUpdated(0);
								JpaController.callUpdate(emf, ricevutaPec);

								// // if
								// //
								// (Configurazione.get(emf).isEnableNotifyStatus())
								// // {
								// logger.info("creaNotificaCambioStatoAdUtente "
								// + OGGETTO_ANOMALIA_MESSAGGIO);
								// NotificaBL.creaNotificaCambioStatoAdUtente(emf,
								// null, utente.getIdUtente(),
								// OGGETTO_ANOMALIA_MESSAGGIO,
								// messaggioNuovoRicevuto, messaggioInviato);
								// // }
							}
							messaggioCambioStato = true;
							break;

						}

					}
				} else {
					logger.warn("Oggetto o Protocollo non valorizzati in questo messaggio con id=" + messaggioInviato.getId());
				}
			}

			// if (!messaggioCambioStato) {
			// logger.info("creaNotificaRicezioneAiResponsabili");
			// NotificaBL.creaNotificaRicezioneAiResponsabili(emf, null,
			// utente.getIdUtente(), messaggioNuovoRicevuto);
			// }

			ricevutaPec.setProcessato(true);
			ricevutaPec.markAsUpdated(0);
			JpaController.callUpdate(emf, ricevutaPec);

			i++;
		}

		logger.info(i + " messaggi ricevute verificati");

		// /*
		// * Verifico se per dei messaggi inviati devo salvare una notifica di
		// * Obsolescenza
		// */
		// /* Ricarico la lista al termine dei controlli precedenti */
		// messaggiInviati = JpaController.callFind(emf, Messaggio.class,
		// filtroInviati);
		// i = 0;
		// for (Messaggio messaggioInviato : messaggiInviati) {
		// logger.info("creaNotificaRicezioneAiResponsabili");
		//
		// DateTime messageDate = new DateTime(messaggioInviato.getDataInvio());
		// DateTime now = new DateTime();
		// Minutes age = Minutes.minutesBetween(messageDate, now);
		//
		// int maxAge =
		// Configurazione.get(emf).getMessageWaitFeedbackInMinutes();
		// if (maxAge > 0 && age.getMinutes() >= maxAge) {
		// NotificaBL.creaNotificaObsoletoAiResponsabili(emf, null,
		// utente.getIdUtente(), messaggioInviato);
		// NotificaBL.creaNotificaObsoletoAlMittente(emf, null,
		// utente.getIdUtente(), messaggioInviato);
		//
		// i++;
		// }
		// }
		//
		// logger.info(i + " messaggi inviati obsoleti verificati ");

		res = true;

		return res;
	}
}
