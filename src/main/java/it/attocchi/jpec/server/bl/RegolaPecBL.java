package it.attocchi.jpec.server.bl;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.entities.filters.RegolaPecFilter;
import it.attocchi.jpec.server.regole.RegolaPecHelper;

import java.util.List;

import javax.mail.Message;
import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegolaPecBL {

	protected static final Logger logger = LoggerFactory.getLogger(RegolaPecBL.class);

	public static List<RegolaPec> regole(EntityManagerFactory emf, RegolaPecEventoEnum evento) throws Exception {
		RegolaPecFilter filtro = new RegolaPecFilter();
		filtro.setEvento(evento);

		List<RegolaPec> res = JpaController.callFind(emf, RegolaPec.class, filtro);

		if (res == null || res.size() == 0) {
			logger.warn("nessuna regola configurata per evento {}", evento);
		}

		return res;
	}

	public static boolean applicaRegole(EntityManagerFactory emf, RegolaPecEventoEnum evento, Message email) throws Exception {
		List<RegolaPec> regoleDaApplicare = regole(emf, evento);
		boolean tutteLeRegoleVerificate = false;
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
			tutteLeRegoleVerificate = applicaRegole(emf, regoleDaApplicare, email);
		} else {
			logger.warn("nessuna regola configurata per evento {}", evento);
			tutteLeRegoleVerificate = true;
		}
		return tutteLeRegoleVerificate;
	}

	public static boolean applicaRegole(EntityManagerFactory emf, List<RegolaPec> regoleDaApplicare, Message email) throws Exception {
		// default true
		boolean tutteLeRegoleVerificate = true;
		if (regoleDaApplicare != null && regoleDaApplicare.size() > 0) {
			for (RegolaPec regola : regoleDaApplicare) {
				logger.debug("applico la regola {}", regola.getNome());
				String groovyCode = regola.getCriterio();
				if (StringUtils.isNotBlank(groovyCode)) {
					Binding binding = new Binding();
					binding.setVariable("email", email);
					binding.setVariable("helper", new RegolaPecHelper(regola, email));

//					String grrovyBody = groovyCode.trim();
//					if (!grrovyBody.startsWith("{")) {
//						groovyCode = "{" + grrovyBody + "}(email, helper)";
//					} else {
//						groovyCode = "" + grrovyBody + "(email, helper)";
//					}

					GroovyShell shell = new GroovyShell(binding);
					Object res = false;
					try {
						res = shell.evaluate(groovyCode);
					} catch (Exception ex) {
						logger.error("errore valutazione criterio groovy:\n{}", groovyCode);
						logger.error("dettagli", ex);
					}

					if (res != null && res instanceof Boolean) {
						tutteLeRegoleVerificate = tutteLeRegoleVerificate && (Boolean) res;
						logger.debug("risultato del criterio: {}", res);
					} else {
						logger.error("impossibile verificare come risultato boolean il criterio della {}", regola.getNome());
					}
				} else {
					logger.warn("regola {} senza criterio", regola.getNome());
				}
			}
		} else {
			logger.warn("nessuna regola da applicare");
		}
		return tutteLeRegoleVerificate;
	}
}
