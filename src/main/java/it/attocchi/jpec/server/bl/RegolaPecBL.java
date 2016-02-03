package it.attocchi.jpec.server.bl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.filters.RegolaPecFilter;
import it.attocchi.jpec.server.protocollo.ProtocolloGenerico;
import it.attocchi.jpec.server.regole.RegolaPecHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegolaPecBL {

	protected static final Logger logger = LoggerFactory.getLogger(RegolaPecBL.class);

	public static synchronized List<RegolaPec> regole(EntityManagerFactory emf, RegolaPecEventoEnum evento) throws Exception {
		RegolaPecFilter filtro = new RegolaPecFilter();
		filtro.setEvento(evento);

		List<RegolaPec> res = JpaController.callFind(emf, RegolaPec.class, filtro);

		if (res == null || res.size() == 0) {
			logger.warn("nessuna regola configurata per evento {}", evento);
		}

		return res;
	}

	public static synchronized boolean applicaRegole(EntityManagerFactory emf, RegolaPecEventoEnum evento, Message email, Map<String, Object> regolaContext) throws Exception {
		List<RegolaPec> regoleDaApplicare = regole(emf, evento);
		boolean tutteLeRegoleVerificate = false;
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
			tutteLeRegoleVerificate = applicaRegole(emf, regoleDaApplicare, email, regolaContext);
		} else {
			logger.warn("nessuna regola configurata per evento {}", evento);
			tutteLeRegoleVerificate = true;
		}
		return tutteLeRegoleVerificate;
	}

	public static synchronized boolean applicaRegole(EntityManagerFactory emf, List<RegolaPec> regoleDaApplicare, Message email, Map<String, Object> regolaContext) throws Exception {
		// default true
		boolean tutteLeRegoleVerificate = true;
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
			for (RegolaPec regola : regoleDaApplicare) {
				logger.debug("verifica della regola: {}", regola.getNome());
				String criterioGroovy = regola.getCriterio();
				if (StringUtils.isNotBlank(criterioGroovy)) {
					logger.debug("valutazione del criterio: {}", criterioGroovy);
					Binding binding = new Binding();
					binding.setVariable("email", email);
					binding.setVariable("helper", new RegolaPecHelper(regola, email));
					if (regolaContext != null && !regolaContext.isEmpty()) {
						for (String key : regolaContext.keySet()) {
							binding.setVariable(key, regolaContext.get(key));
						}
					}

					// String grrovyBody = groovyCode.trim();
					// if (!grrovyBody.startsWith("{")) {
					// groovyCode = "{" + grrovyBody + "}(email, helper)";
					// } else {
					// groovyCode = "" + grrovyBody + "(email, helper)";
					// }

					GroovyShell shell = new GroovyShell(binding);
					Object criterioResult = false;
					try {
						criterioResult = shell.evaluate(criterioGroovy);
					} catch (Exception ex) {
						logger.error("errore valutazione criterio groovy.");
						logger.error("dettagli", ex);
					}

					if (criterioResult != null && criterioResult instanceof Boolean) {
						boolean criterioRes = (Boolean) criterioResult;
						logger.debug("risultato esecuzione criterio: {}", criterioRes);
						
						if (criterioRes) {
							String azioneGroovy = regola.getAzione();
							logger.debug("valutazione dell'azione: {}", azioneGroovy);
							if (StringUtils.isNotBlank(azioneGroovy)) {
								Object azioneResult = shell.evaluate(azioneGroovy);
							}
						}
						tutteLeRegoleVerificate = tutteLeRegoleVerificate && criterioRes;
						
					} else {
						logger.error("impossibile verificare come risultato boolean il criterio applicato");
					}
				} else {
					logger.warn("attenzione la regola non contiene criteri da valutare");
				}
			}
		} else {
			logger.warn("nessuna regola da applicare");
		}
		return tutteLeRegoleVerificate;
	}
		
	public static synchronized boolean applicaRegoleProtocollo(EntityManagerFactory emf, List<RegolaPec> regoleDaApplicare, Message email, MessaggioPec pec, ProtocolloGenerico istanzaProtocollo) throws Exception {
		Map<String, Object> regolaContext = new HashMap<String, Object>();
		regolaContext.put("protocollo", istanzaProtocollo);
		
		return applicaRegole(emf, regoleDaApplicare, email, regolaContext);
	}
}
