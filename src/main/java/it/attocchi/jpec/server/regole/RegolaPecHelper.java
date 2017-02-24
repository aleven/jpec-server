package it.attocchi.jpec.server.regole;

import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.bl.RegolaPecBL;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.mail.utils.PecParser2;

import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.activation.DataHandler;
import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Header;
import javax.mail.Message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author mirco
 *
 */
public class RegolaPecHelper {

	protected final Logger logger = LoggerFactory.getLogger(RegolaPecBL.class);

	private Message messaggioEmail;
	private RegolaPec regola;

	public RegolaPecHelper(RegolaPec regola, Message messaggioEmail) {
		super();
		this.messaggioEmail = messaggioEmail;
		this.regola = regola;
	}

	/**
	 * 
	 * @param messaggioEmail
	 * @return
	 */
	public boolean isMessaggioDaLeggere() {
		boolean res = false;
		try {
			logger.debug("criterio isMessaggioDaLeggere applicato da regola {} su messaggio {}", regola, messaggioEmail);
			res = !messaggioEmail.getFlags().contains(Flag.SEEN);
		} catch (Exception ex) {
			logger.error("errore isMessaggioDaLeggere", ex);
		}
		return res;
	}

	/**
	 * 
	 * @param messaggioEmail
	 * @return
	 */
	public boolean isMessaggioRicevuta() {
		boolean res = false;
		try {
			logger.debug("criterio isMessaggioDaLeggere applicato da regola {} su messaggio {}", regola, messaggioEmail);
			String headerXRicevuta = "";
			if (messaggioEmail.getAllHeaders() != null) {
				Enumeration headers = messaggioEmail.getAllHeaders();
				while (headers.hasMoreElements()) {
					Header h = (Header) headers.nextElement();
					String headerName = h.getName();
					if (MessaggioPecBL.HEADER_X_RICEVUTA.equalsIgnoreCase(headerName)) {
						headerXRicevuta = h.getValue();
						break;
					}
				}
			}
			res = StringUtils.isNoneBlank(headerXRicevuta);
		} catch (Exception ex) {
			logger.error("errore isMessaggioRicevuta", ex);
		}
		return res;
	}

	public boolean isMessaggioBustaAnomalia() {
		boolean res = false;
		try {
			logger.debug("criterio isMessaggioBustaAnomalia applicato da regola {} su messaggio {}", regola, messaggioEmail);
			String headerXTrasporto = "";
			if (messaggioEmail.getAllHeaders() != null) {
				Enumeration headers = messaggioEmail.getAllHeaders();
				while (headers.hasMoreElements()) {
					Header h = (Header) headers.nextElement();
					String headerName = h.getName();
					if (MessaggioPecBL.HEADER_X_TRASPORTO.equalsIgnoreCase(headerName)) {
						headerXTrasporto = h.getValue();
						break;
					}
				}
			}
			res = MessaggioPecBL.BUSTA_ANOMALIA.equals(headerXTrasporto);
		} catch (Exception ex) {
			logger.error("errore isMessaggioRicevuta", ex);
		}
		return res;
	}

	public boolean isNotMessaggioRicevuta() {
		return !isMessaggioRicevuta();
	}

	/**
	 * 
	 * @param regExp
	 * @return
	 */
	public boolean mittenteMatch(String regExp) {
		boolean res = false;
		try {
			logger.debug("criterio mittenteMatch applicato da regola {} su messaggio {}", regola, messaggioEmail);
			Address[] senders = messaggioEmail.getFrom();
			if (senders != null) {
				for (Address sender : senders) {
					Pattern pattern = Pattern.compile(regExp);
					Matcher matcher = pattern.matcher(sender.toString());
					res = matcher.matches();
					if (res) {
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("errore mittenteMatch", ex);
		}
		return res;
	}

	/**
	 * verifica match con l'oggetto dell'email  
	 * @param regExp pattern della regular expression 
	 * @return true if pattern match
	 */
	public boolean subjectMatch(String regExp) {
		boolean res = false;
		try {
			logger.debug("criterio subjectMatch applicato da regola {} su messaggio {}", regola, messaggioEmail);
			String subject = messaggioEmail.getSubject();
			if (subject != null) {
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(subject);
				res = matcher.matches();
			}
		} catch (Exception ex) {
			logger.error("errore subjectMatch", ex);
		}
		return res;
	}

	/**
	 * verifica match con uno qualsiasi degli indirizzi specificati in reply-to
	 * @param regExp pattern della regular expression 
	 * @return true if pattern match
	 */
	public boolean replyToMatch(String regExp) {
		boolean res = false;
		try {
			logger.debug("criterio replyToMatch applicato da regola {} su messaggio {}", regola, messaggioEmail);
			Address[] senders = messaggioEmail.getReplyTo();
			if (senders != null) {
				for (Address sender : senders) {
					Pattern pattern = Pattern.compile(regExp);
					Matcher matcher = pattern.matcher(sender.toString());
					res = matcher.matches();
					if (res) {
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("errore replyToMatch", ex);
		}
		return res;
	}

	/**
	 * 
	 * @param regExp
	 * @return
	 */
	public boolean destinatarioMatch(String regExp) {
		boolean res = false;
		try {
			logger.debug("criterio destinatarioMatch applicato da regola {} su messaggio {}", regola, messaggioEmail);
			Address[] recipients = messaggioEmail.getAllRecipients();
			if (recipients != null) {
				for (Address recipient : recipients) {
					Pattern pattern = Pattern.compile(regExp);
					Matcher matcher = pattern.matcher(recipient.toString());
					res = matcher.matches();
					if (res) {
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("errore destinatarioMatch", ex);
		}
		return res;
	}

	/**
	 * 
	 * @param regExp
	 * @return
	 */
	public boolean attachmentNameMatch(String regExp) {
		boolean res = false;
		try {
			logger.debug("criterio attachmentNameMatch applicato da regola {} su messaggio {}", regola, messaggioEmail);
			// List<MailAttachmentUtil> attachments =
			// MailUtils.getAttachments(messaggioEmail);
			PecParser2 p2 = new PecParser2();
			p2.dumpPart(messaggioEmail);
			Map<String, DataHandler> attachments = p2.getAttachments();
			if (attachments != null) {
				for (String attachmentName : attachments.keySet()) {
					if (StringUtils.isNotBlank(attachmentName)) {
						Pattern pattern = Pattern.compile(regExp);
						Matcher matcher = pattern.matcher(attachmentName);
						res = matcher.matches();
						if (res) {
							break;
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error("errore attachmentNameMatch", ex);
		}
		return res;
	}
}
