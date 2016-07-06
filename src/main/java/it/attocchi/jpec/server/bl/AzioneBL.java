package it.attocchi.jpec.server.bl;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.jpec.server.protocollo.AzioneContext;
import it.attocchi.jpec.server.protocollo.AzioneEsito;
import it.attocchi.jpec.server.protocollo.AzioneGenerica;

import java.util.Properties;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzioneBL {

	protected static final Logger logger = LoggerFactory.getLogger(AzioneBL.class);
	
	// Message email, MessaggioPec pec, String mailboxName, 
	public static synchronized AzioneGenerica creaIstanzaAzione(EntityManagerFactory emf, String classe, AzioneContext context) throws PecException {
		AzioneGenerica istanzaAzione = null;
		
//		String protocolloImplGenerico = ConfigurazioneBL.getValueStringDB(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL);
//		String protocolloImplMailbox = ConfigurazioneBL.getValueString(emf, ConfigurazionePecEnum.PEC_PROTOCOLLO_IMPL, mailboxName);
//		String protocolloImpl = (StringUtils.isNotBlank(protocolloImplMailbox)) ? protocolloImplMailbox : protocolloImplGenerico;
		String azioneImpl = classe;

		if (StringUtils.isNotBlank(azioneImpl)) {
			Properties configurazioneMailbox = ConfigurazioneBL.getConfigurazione(context.getMailboxName());
			logger.info("implementazione classe: {}", azioneImpl);
			// istanzaProtocollo = ProtocolloBL.creaIstanzaProtocollo(emf, protocolloImpl, messaggioEmail, configurazioneMailbox);
			try {
				Object protocolloInstance = Class.forName(azioneImpl).newInstance();
				if (protocolloInstance instanceof AzioneGenerica) {
					istanzaAzione = (AzioneGenerica) protocolloInstance;

					// AzioneContext context = AzioneContext.buildContextMessaggi(emf, email, pec, configurazioneMailbox);
					context.setConfigurazioneMailbox(configurazioneMailbox);
					istanzaAzione.inizialize(context);

				} else {
					throw new PecException("la classe specificata non implementa l'interfaccia " + AzioneGenerica.class.getName());
				}
			} catch (Exception ex) {
				// res.errore = ex.getMessage();
				String message = String.format("errore creazione istanza classe azione %s", azioneImpl);
				logger.error(message, ex);
				// throw new PecException(message, ex);
			}
		} else {
			logger.warn("nessuna classe specificata per l'esecuzione di una azione");
		}
		
		return istanzaAzione;
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

	public static synchronized AzioneEsito eseguiIstanza(AzioneGenerica istanzaAzione) {
		AzioneEsito res = AzioneEsito.errore("", null);

		if (istanzaAzione != null) {
			try {
				res = istanzaAzione.esegui();
			} catch (Exception ex) {
				logger.debug("errore creazione istanza classe {}", istanzaAzione.getClass().getName());
				res = AzioneEsito.errore(ex.getMessage(), ex);
			}
		} else {
			logger.debug("nessuna classe specificata per la generazione di una azione");
			res = AzioneEsito.ok("", "");
		}

		return res;
	}

}
