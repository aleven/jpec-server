package it.attocchi.jpec.server.protocollo;

import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.jpec.server.protocollo.ProtocolloEsito.ProtocolloEsitoStato;

import java.util.Properties;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloHelper {

	protected static final Logger logger = LoggerFactory.getLogger(ProtocolloHelper.class);

	public ProtocolloHelper() {
		super();
	}

	public static synchronized ProtocolloGenerico creaIstanzaProtocollo(EntityManagerFactory emf, String protocolloImpl, Message messaggioEmail, Properties configurazioneMailbox) {
		ProtocolloGenerico protocollo = null;
		if (StringUtils.isNotBlank(protocolloImpl)) {
			try {
				Object protocolloInstance = Class.forName(protocolloImpl).newInstance();
				if (protocolloInstance instanceof ProtocolloGenerico) {
					protocollo = (ProtocolloGenerico) protocolloInstance;

					ProtocolloContext context = new ProtocolloContext(emf, messaggioEmail, configurazioneMailbox);
					protocollo.inizialize(context);

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
		return protocollo;
	}

	public synchronized ProtocolloEsito eseguiOLD(EntityManagerFactory emf, String protocolloImpl, Message messaggioEmail, Properties configurazioneMailbox) {
		ProtocolloEsito res = new ProtocolloEsito();
		res.stato = ProtocolloEsitoStato.ERRORE;

		if (StringUtils.isNotBlank(protocolloImpl)) {
			try {
				Object protocolloInstance = Class.forName(protocolloImpl).newInstance();
				if (protocolloInstance instanceof ProtocolloGenerico) {
					ProtocolloGenerico protocollo = (ProtocolloGenerico) protocolloInstance;

					ProtocolloContext context = new ProtocolloContext(emf, messaggioEmail, configurazioneMailbox);
					protocollo.inizialize(context);

					res = protocollo.esegui();
				} else {
					throw new PecException("la classe specificata per la generazione del protocollo non implementa l'interfaccia " + ProtocolloGenerico.class.getName());
				}
			} catch (Exception ex) {
				logger.debug("errore creazione istanza classe protocollo {}", protocolloImpl);
				res.errore = ex.getMessage();
			}
		} else {
			logger.debug("nessuna classe specificata per la generazione di un protocollo");
		}

		return res;
	}

	public static synchronized ProtocolloEsito esegui(ProtocolloGenerico istanzaProtocollo) {
		ProtocolloEsito res = new ProtocolloEsito();
		res.stato = ProtocolloEsitoStato.ERRORE;

		if (istanzaProtocollo != null) {
			try {
				res = istanzaProtocollo.esegui();
			} catch (Exception ex) {
				logger.debug("errore creazione istanza classe protocollo {}", istanzaProtocollo.getClass().getName());
				res.errore = ex.getMessage();
			}
		} else {
			logger.debug("nessuna classe specificata per la generazione di un protocollo");
		}

		return res;
	}
}
