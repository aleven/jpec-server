package it.attocchi.jpec.server.bl;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.jpec.server.protocollo.ProtocolloContext;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito;
import it.attocchi.jpec.server.protocollo.ProtocolloGenerico;

import java.util.Properties;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloBL {

	protected static final Logger logger = LoggerFactory.getLogger(ProtocolloBL.class);

	public static synchronized ProtocolloGenerico creaIstanzaProtocollo(EntityManagerFactory emf, Message email, MessaggioPec pec, String mailboxName) {
		ProtocolloGenerico istanzaProtocollo = null;
		
		String protocolloImplGenerico = ConfigurazioneBL.getValueStringDB(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL);
		String protocolloImplMailbox = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL, mailboxName);
		String protocolloImpl = (StringUtils.isNotBlank(protocolloImplMailbox)) ? protocolloImplMailbox : protocolloImplGenerico;

		if (StringUtils.isNotBlank(protocolloImpl)) {
			Properties configurazioneMailbox = ConfigurazioneBL.getConfigurazione(mailboxName);
			logger.debug("implementazione protocolo: {}", protocolloImpl);
			// istanzaProtocollo = ProtocolloBL.creaIstanzaProtocollo(emf, protocolloImpl, messaggioEmail, configurazioneMailbox);
			try {
				Object protocolloInstance = Class.forName(protocolloImpl).newInstance();
				if (protocolloInstance instanceof ProtocolloGenerico) {
					istanzaProtocollo = (ProtocolloGenerico) protocolloInstance;

					ProtocolloContext context = new ProtocolloContext(emf, email, pec, configurazioneMailbox);
					istanzaProtocollo.inizialize(context);

				} else {
					throw new PecException("la classe specificata per la generazione del protocollo non implementa l'interfaccia " + ProtocolloGenerico.class.getName());
				}
			} catch (Exception ex) {
				logger.debug("errore creazione istanza classe protocollo {}", protocolloImpl);
				// res.errore = ex.getMessage();
			}
		} else {
			logger.debug("nessuna classe specificata per la generazione di un protocollo");
		}
		
		return istanzaProtocollo;
	}

	// public synchronized ProtocolloEsito eseguiOLD(EntityManagerFactory emf,
	// String protocolloImpl, Message messaggioEmail, Properties
	// configurazioneMailbox) {
	// ProtocolloEsito res = ProtocolloEsito.errore("");
	//
	// if (StringUtils.isNotBlank(protocolloImpl)) {
	// try {
	// Object protocolloInstance = Class.forName(protocolloImpl).newInstance();
	// if (protocolloInstance instanceof ProtocolloGenerico) {
	// ProtocolloGenerico protocollo = (ProtocolloGenerico) protocolloInstance;
	//
	// ProtocolloContext context = new ProtocolloContext(emf, messaggioEmail,
	// configurazioneMailbox);
	// protocollo.inizialize(context);
	//
	// res = protocollo.esegui();
	// } else {
	// throw new
	// PecException("la classe specificata per la generazione del protocollo non implementa l'interfaccia "
	// + ProtocolloGenerico.class.getName());
	// }
	// } catch (Exception ex) {
	// logger.debug("errore creazione istanza classe protocollo {}",
	// protocolloImpl);
	// res = ProtocolloEsito.errore(ex.getMessage());
	// }
	// } else {
	// logger.debug("nessuna classe specificata per la generazione di un protocollo");
	// }
	//
	// return res;
	// }

	public static synchronized ProtocolloEsito eseguiIstanza(ProtocolloGenerico istanzaProtocollo) {
		ProtocolloEsito res = ProtocolloEsito.errore("");

		if (istanzaProtocollo != null) {
			try {
				res = istanzaProtocollo.esegui();
			} catch (Exception ex) {
				logger.debug("errore creazione istanza classe protocollo {}", istanzaProtocollo.getClass().getName());
				res = ProtocolloEsito.errore(ex.getMessage());
			}
		} else {
			logger.debug("nessuna classe specificata per la generazione di un protocollo");
		}

		return res;
	}

}
