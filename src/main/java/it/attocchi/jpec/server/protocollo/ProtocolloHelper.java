package it.attocchi.jpec.server.protocollo;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolloHelper {

	protected final Logger logger = LoggerFactory.getLogger(ProtocolloHelper.class);

	public ProtocolloHelper() {
		super();
	}

	public String esegui(EntityManagerFactory emf, String protocolloImpl, Message messaggioEmail) {
		String res = "";

		if (StringUtils.isNotBlank(protocolloImpl)) {
			try {
				Object protocolloInstance = Class.forName(protocolloImpl).newInstance();
				if (protocolloInstance instanceof ProtocolloGenerico) {
					ProtocolloGenerico protocollo = (ProtocolloGenerico) protocolloInstance;

					ProtocolloContext context = new ProtocolloContext(emf, protocolloImpl, messaggioEmail);
					protocollo.inizialize(context);

					res = protocollo.esegui();
				} else {
					logger.error("la classe specificata per la generazione del protocollo non implementa l'interfaccia {}", ProtocolloGenerico.class);
				}
			} catch (Exception ex) {
				logger.error("errore creazione istanza classe protocollo {}", protocolloImpl);
			}
		} else {

		}

		return res;
	}
}
