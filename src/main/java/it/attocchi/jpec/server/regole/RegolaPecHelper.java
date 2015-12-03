package it.attocchi.jpec.server.regole;

import it.attocchi.jpec.server.bl.RegolaPecBL;

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

	protected static final Logger logger = LoggerFactory.getLogger(RegolaPecBL.class);
	
	/**
	 * 
	 * @param email
	 * @return
	 */
	public boolean isMessaggioDaLeggere(Message email) {
		boolean res = false;
		try {
			res = !email.getFlags().contains(Flag.SEEN);
		} catch (Exception ex) {
			logger.error("errore isMessaggioDaLeggere", ex);
		}
		return res;
	}
	
}
