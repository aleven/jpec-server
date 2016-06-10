package it.attocchi.jpec.server.bl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.filters.RegolaPecFilter;
import it.attocchi.jpec.server.protocollo.AzioneContext;
import it.attocchi.jpec.server.protocollo.AzioneEsito;
import it.attocchi.jpec.server.protocollo.AzioneEsito.AzioneEsitoStato;
import it.attocchi.jpec.server.protocollo.AzioneGenerica;
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

//		if (res == null || res.size() == 0) {
//			logger.warn("nessuna regola configurata per evento {}", evento);
//		}

		return res;
	}

	public static synchronized AzioneEsito applicaRegole(EntityManagerFactory emf, RegolaPecEventoEnum evento, AzioneContext contesto) throws Exception {
		List<RegolaPec> regoleDaApplicare = regole(emf, evento);
		AzioneEsito tutteLeRegoleVerificate = AzioneEsito.errore("", null);
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
			// tutteLeRegoleVerificate = applicaRegole(emf, regoleDaApplicare, email, messaggioPec, mailboxName);
			tutteLeRegoleVerificate = applicaRegole(emf, regoleDaApplicare, contesto);
		} else {
			logger.warn("nessuna regola configurata per evento {}", evento);
			tutteLeRegoleVerificate = AzioneEsito.ok("", "");
		}
		return tutteLeRegoleVerificate;
	}

	// Message email, MessaggioPec messaggioPec, String mailboxName
	public static synchronized AzioneEsito applicaRegole(EntityManagerFactory emf, List<RegolaPec> regoleDaApplicare, AzioneContext contesto) throws Exception {
		// default true
		AzioneEsito res = AzioneEsito.errore("", null);
		// boolean tutteLeRegoleVerificate = true;
		
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
									
			for (RegolaPec regola : regoleDaApplicare) {
				
				Map<String, Object> regolaContext = new HashMap<String, Object>();
				String classe = regola.getClasse();
				AzioneGenerica istanzaAzione = null;
				if (StringUtils.isNotBlank(classe)) {
					// istanzaAzione = AzioneBL.creaIstanzaAzione(emf, email, messaggioPec, mailboxName, classe);
					istanzaAzione = AzioneBL.creaIstanzaAzione(emf, classe, contesto);
					regolaContext.put("azione", istanzaAzione);
				} else {
					logger.warn("nessuna implementazione configurata per questa regola");
				}

				Binding binding = new Binding();
				binding.setVariable("email", contesto.getEmail());
				binding.setVariable("helper", new RegolaPecHelper(regola, contesto.getEmail()));
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
				
				logger.debug("regola: \"{}\"", regola.getNome());
				String criterioGroovy = regola.getCriterio();
				boolean criterioRegolaSoddisfatto = false;
				if (StringUtils.isNotBlank(criterioGroovy)) {
					logger.debug("criterio: \"{}\"", criterioGroovy);
					Object criterioGroovyResult = null;
					try {
						criterioGroovyResult = shell.evaluate(criterioGroovy);
					} catch (Exception ex) {
						logger.error("errore valutazione script groovy.", ex);
					}
					if (criterioGroovyResult != null && criterioGroovyResult instanceof Boolean) {
						criterioRegolaSoddisfatto = (Boolean) criterioGroovyResult;
						logger.debug("risultato: {}", criterioRegolaSoddisfatto);
					} else {
						logger.error("impossibile verificare come risultato boolean il criterio groovy applicato");
						criterioRegolaSoddisfatto = false;
					}
				} else {
					logger.warn("la regola non contiene criteri groovy da valutare");
					criterioRegolaSoddisfatto = true;
				}
				
				/* se i criteri sono verificati (o vuoti) applico le azioni e poi istanzio la classe da eseguire */
				if (criterioRegolaSoddisfatto) {
					
					String azioneGroovy = regola.getAzione();
					if (StringUtils.isNotBlank(azioneGroovy)) {
						logger.debug("azione: \"{}\"", azioneGroovy);
						try {
							Object azioneResult = shell.evaluate(azioneGroovy);
						} catch (Exception ex) {
							logger.error("errore valutazione criterio groovy.", ex);
						}						
					}
					
					if (istanzaAzione != null) {
						res = AzioneBL.eseguiIstanza(istanzaAzione);
						if (res.stato != AzioneEsitoStato.OK) {
							break;
						}
					} else {
						logger.warn("nessuna azione da eseguire");
						res = AzioneEsito.ok("", "");
					}
				} else {
					res = AzioneEsito.regolaNonApplicabile("criterio di Applicazione Regola Non Soddisfatto");
				}
				
				// tutteLeRegoleVerificate = tutteLeRegoleVerificate && criterioRegolaVerificato;
			}
			
		} else {
			logger.warn("nessuna regola da applicare specificata");
			res = AzioneEsito.ok("", "");
		}
				
		return res;
	}
		
//	public static synchronized boolean applicaRegole(EntityManagerFactory emf, List<RegolaPec> regoleDaApplicare, Message email, MessaggioPec messaggioPec, String mailboxName) throws Exception {
////		Map<String, Object> regolaContext = new HashMap<String, Object>();
////		regolaContext.put("protocollo", istanzaProtocollo);
//		
//		return applicaRegole(emf, regoleDaApplicare, email, messaggioPec, mailboxName);
//	}
}
