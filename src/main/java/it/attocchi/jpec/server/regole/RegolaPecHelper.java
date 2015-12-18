package it.attocchi.jpec.server.regole;

import it.attocchi.jpec.server.bl.RegolaPecBL;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.mail.parts.MailAttachmentUtil;
import it.attocchi.mail.utils.MailUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Message;

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
