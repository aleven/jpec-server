package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.ConfigurazionePec;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurazioneBL {

	protected static final Logger logger = LoggerFactory.getLogger(ConfigurazioneBL.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, ConfigurazionePec> dbConfig = null;
	private static Map<String, Properties> mailboxes = null;

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
	public static void init(EntityManagerFactory emf) {
		loadFromDB(emf);
		loadFromFiles(emf);
	}

	public static Map<String, ConfigurazionePec> loadFromDB(EntityManagerFactory emf) {
		if (dbConfig == null) {
			JpaController controller = null;
			try {
				controller = new JpaController(emf);
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
					for (ConfigurazionePecEnum chiave : ConfigurazionePecEnum.values()) {
						ConfigurazionePec newConfigurazione = new ConfigurazionePec();
						newConfigurazione.setNome(chiave.name());
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
			} finally {
				if (controller != null)
					controller.closeEmAndEmf();
			}
		}

		return dbConfig;
	}

	public static Map<String, Properties> loadFromFiles(EntityManagerFactory emf) {
		if (mailboxes == null) {
			String mailboxesPath = getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER);
			if (StringUtils.isNotBlank(mailboxesPath)) {
				logger.info("verifica configurazione mailbox_*.properties in {}", mailboxesPath);
				File mailboxesDir = new File(mailboxesPath);
				File[] files = mailboxesDir.listFiles();
				if (files != null) {
					for (File file : files) {
						if (file.getName().startsWith("mailbox_") && file.getName().endsWith(".properties")) {
							String propertyFileName = file.getName();
							try {
								Properties props = new Properties();
								props.load(new FileInputStream(file));
								String mailboxName = props.getProperty(ConfigurazionePecEnum.PEC_MAILBOX_NAME.name());
								if (StringUtils.isNoneBlank(mailboxName)) {
									if (mailboxes == null) {
										mailboxes = new HashMap<String, Properties>();
									}
									mailboxes.put(mailboxName, props);
								}
								logger.info("caricato configurazione mailbox {} da {}", mailboxName, propertyFileName);
							} catch (Exception ex) {
								logger.error("impossibile caricare configurazione mailbox", ex);
							}
						}
					}
				}
			}
		}
		return mailboxes;
	}

	public static String getValueStringDB(EntityManagerFactory emf, ConfigurazionePecEnum chiave) {
		 return loadFromDB(emf).get(chiave.name()) != null ? loadFromDB(emf).get(chiave.name()).getValore() : null;
	}

	/**
	 * 
	 * @param emf
	 * @param chiave
	 * @param mailboxRequested
	 *            blank for default mailbox (the database configuration)
	 * @return
	 */
	public static String getValueString(EntityManagerFactory emf, ConfigurazionePecEnum chiave, String mailboxRequested) {
		String res = null;

		if (StringUtils.isBlank(mailboxRequested)) {
			res = getValueStringDB(emf, chiave);
		} else {
			boolean isDBMailbox = mailboxRequested.equals(getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOX_NAME));
			if (isDBMailbox) {
				res = loadFromDB(emf).get(chiave.name()).getValore();
			} else {
				Map<String, Properties> configuredMailbox = loadFromFiles(emf);
				for (String mailboxName : configuredMailbox.keySet()) {
					if (mailboxRequested.equals(mailboxName)) {
						Properties p = configuredMailbox.get(mailboxName);
						res = p.getProperty(chiave.name());
						break;
					}
				}
			}

		}
		return res;

	}

	public static Integer getValueInt(EntityManagerFactory emf, ConfigurazionePecEnum chiave, String mailboxRequested) {
		return Integer.parseInt(getValueString(emf, chiave, mailboxRequested));
	}

	public static Boolean getValueBooleanDB(EntityManagerFactory emf, ConfigurazionePecEnum chiave) {
		return Boolean.parseBoolean(getValueStringDB(emf, chiave));
	}

	public static Boolean getValueBoolean(EntityManagerFactory emf, ConfigurazionePecEnum chiave, String mailboxRequested) {
		return Boolean.parseBoolean(getValueString(emf, chiave, mailboxRequested));
	}

	public static void saveValueString(EntityManagerFactory emf, ConfigurazionePecEnum chiave, String valore) {
		JpaController controller = null;
		try {
			controller = new JpaController(emf);
			ConfigurazionePec data = controller.find(ConfigurazionePec.class, chiave.name());
			if (data != null && !valore.equals(data.getValore())) {
				data.setValore(valore);
				controller.update(data);
			} else {
				ConfigurazionePec newConfigurazione = new ConfigurazionePec();
				newConfigurazione.setNome(chiave.name());
				newConfigurazione.setValore(valore);
				controller.insert(newConfigurazione);
			}

		} catch (Exception ex) {
			logger.error("getCurrent", ex);
		} finally {
			if (controller != null)
				controller.closeEmAndEmf();
		}
	}

	public static void resetCurrent() {
		dbConfig = null;
		mailboxes = null;
	}

	/**
	 * 
	 * @param emf
	 * @param contextRealPath
	 */
	public static synchronized void initializeFromContextPath(EntityManagerFactory emf, String contextRealPath) {
		init(emf);
		String mailboxFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER);
		if (StringUtils.isBlank(mailboxFolder) && StringUtils.isNotBlank(contextRealPath)) {
			logger.info("inizializzazione configurazione DB con folder del Context {}", contextRealPath);
			saveValueString(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER, contextRealPath);
			resetCurrent();
			init(emf);
		}
	}

	public static List<String> getAllMailboxes(EntityManagerFactory emf) {
		List<String> res = new ArrayList<String>();
		String dbMailbox = getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOX_NAME);
		if (StringUtils.isNotBlank(dbMailbox)) {
			res.add(dbMailbox);
		}
		loadFromFiles(emf);
		if (mailboxes != null) {
			res.addAll(mailboxes.keySet());
		}
		return res;
	}
	
	public static Properties getConfigurazione(String mailboxName) {
		return (mailboxes != null) ? mailboxes.get(mailboxName) : null;
	}
}
