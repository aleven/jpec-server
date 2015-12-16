package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.ConfigurazionePec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurazioneBL {

	protected static final Logger logger = LoggerFactory.getLogger(ConfigurazioneBL.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, ConfigurazionePec> dbConfig = null;
	private static Map<String, Map<String, ConfigurazionePec>> fileConfig = null;

	public static ConfigurazionePec get(ConfigurazionePecEnum chiave) {
		ConfigurazionePec res = null;
		if (dbConfig == null) {
			logger.error("Configurazione not intialized");
		} else {
			if (dbConfig.containsKey(chiave.name())) {
				res = dbConfig.get(chiave.name());
			} else {
				logger.error("Configurazione {} not intialized", chiave.name());
			}
		}
		return res;
	}

	/**
	 * Bisognerebbe prevedere l'inizializzazione da programma in modo che poi vi
	 * si accede via cache. Questo e' utile nel caso di chiamate da proprieta'
	 * di oggetti, come Messaggio che non hanno accesso al DB
	 * 
	 * @param emf
	 * @return
	 */
	public static Map<String, ConfigurazionePec> init(EntityManagerFactory emf) {
		return get(emf);
	}

	public static Map<String, ConfigurazionePec> get(EntityManagerFactory emf) {
		if (dbConfig == null) {
			try {
				JpaController controller = new JpaController(emf);
				List<ConfigurazionePec> data = controller.findAll(ConfigurazionePec.class);

				if (data == null || data.size() == 0) {
					// se non ci sono dati inserisco quelli di default
					for (ConfigurazionePecEnum value : ConfigurazionePecEnum.values()) {
						ConfigurazionePec newConfigurazione = new ConfigurazionePec();
						newConfigurazione.setNome(value.name());
						controller.insert(newConfigurazione);
					}
					// refresh dei dati
					data = controller.findAll(ConfigurazionePec.class);
				} else {
					// aggiungiamo le configurazioni mancanti
					for (ConfigurazionePecEnum value : ConfigurazionePecEnum.values()) {
						ConfigurazionePec newConfigurazione = new ConfigurazionePec();
						newConfigurazione.setNome(value.name());
						if (!data.contains(newConfigurazione)) {
							controller.insert(newConfigurazione);
						}
					}
				}

				dbConfig = new HashMap<String, ConfigurazionePec>();
				for (ConfigurazionePec configurazione : data) {
					dbConfig.put(configurazione.getNome(), configurazione);
				}				
			} catch (Exception ex) {
				logger.error("getCurrent", ex);
			}
		}

		return dbConfig;
	}

	public static String getValueString(EntityManagerFactory emf, ConfigurazionePecEnum chiave) {
		return get(emf).get(chiave.name()).getValore();
	}

	public static Integer getValueInt(EntityManagerFactory emf, ConfigurazionePecEnum chiave) {
		return Integer.parseInt(get(emf).get(chiave.name()).getValore());
	}

	public static Boolean getValueBoolean(EntityManagerFactory emf, ConfigurazionePecEnum chiave) {
		return Boolean.parseBoolean(get(emf).get(chiave.name()).getValore());
	}

	public static void resetCurrent() {
		dbConfig = null;
	}

}
