package it.attocchi.jpec.server.regole;

import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.bl.RegolaPecBL;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.mail.parts.MailAttachmentUtil;
import it.attocchi.mail.utils.MailUtils;

import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			logger.error("errore isMessaggioDaLeggere", ex);
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
			logger.error("errore isMessaggioDaLeggere", ex);
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
			logger.error("errore isMessaggioDaLeggere", ex);
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
			List<MailAttachmentUtil> attachments = MailUtils.getAttachments(messaggioEmail);
			if (attachments != null) {
				for (MailAttachmentUtil attachment : attachments) {
					Pattern pattern = Pattern.compile(regExp);
					Matcher matcher = pattern.matcher(attachment.getFileName());
					res = matcher.matches();
					if (res) {
						break;
					}
				}
			}
		} catch (Exception ex) {
			logger.error("errore isMessaggioDaLeggere", ex);
		}
		return res;
	}	
}
