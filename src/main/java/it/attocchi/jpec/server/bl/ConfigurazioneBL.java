package it.attocchi.jpec.server.bl;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpec.server.entities.ConfigurazionePec;
import it.attocchi.jpec.server.exceptions.PecException;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		/* DEFAULT SETTING da DB */
		res = getValueStringDB(emf, chiave);
		/*
		 * se specificato carico l'impostazione specifica della mailbox (o
		 * overridata dalla mailbox rispetto a quella di default)
		 */
		if (StringUtils.isNotBlank(mailboxRequested)) {
			Map<String, Properties> configuredMailbox = loadFromFiles(emf);
			for (String mailboxName : configuredMailbox.keySet()) {
				if (mailboxRequested.equals(mailboxName)) {
					Properties p = configuredMailbox.get(mailboxName);
					String mailboxRes = p.getProperty(chiave.name());
					if (StringUtils.isNotBlank(mailboxRes)) {
						res = mailboxRes;
					}
					break;
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
	public static synchronized void initializeFromContextPath(EntityManagerFactory emf, String contextRealPath) throws PecException {
		init(emf);

		boolean someUpdated = false;
		String mailboxFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER);
		if (StringUtils.isBlank(mailboxFolder) && StringUtils.isNotBlank(contextRealPath)) {
			Path webinf = Paths.get(contextRealPath, "WEB-INF");
			logger.info("inizializzazione cartella impostazioni mailboxes {}", webinf.toString());
			saveValueString(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER, webinf.toString());
			someUpdated = true;
		}

		String allegatiFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_ATTACH_STORE_FOLDER);
		if (StringUtils.isBlank(allegatiFolder) && StringUtils.isNotBlank(contextRealPath)) {
			String allegati = Paths.get(contextRealPath, "WEB-INF", "allegati").toString();
			logger.info("inizializzazione cartella salvataggio allegati {}", allegati);
			saveValueString(emf, ConfigurazionePecEnum.PEC_ATTACH_STORE_FOLDER, allegati);
			someUpdated = true;
		}

		String emlFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER);
		if (StringUtils.isBlank(emlFolder) && StringUtils.isNotBlank(contextRealPath)) {
			String eml = Paths.get(contextRealPath, "WEB-INF", "eml").toString();
			logger.info("inizializzazione cartella salvataggio eml {}", eml);
			saveValueString(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER, eml);
			someUpdated = true;
		}

		String in = getValueStringDB(emf, ConfigurazionePecEnum.PEC_FOLDER_IN);
		if (StringUtils.isBlank(in)) {
			String defaultValue = "IN";
			logger.info("inizializzazione cartella salvataggio eml {}", defaultValue);
			saveValueString(emf, ConfigurazionePecEnum.PEC_FOLDER_IN, defaultValue);
			someUpdated = true;
		}
		String out = getValueStringDB(emf, ConfigurazionePecEnum.PEC_FOLDER_IN);
		if (StringUtils.isBlank(out)) {
			String defaultValue = "OUT";
			logger.info("inizializzazione cartella salvataggio eml {}", defaultValue);
			saveValueString(emf, ConfigurazionePecEnum.PEC_FOLDER_IN, defaultValue);
			someUpdated = true;
		}

		if (someUpdated) {
			resetCurrent();
			init(emf);
		}

		/* verifica accesso cartelle */
		mailboxFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_MAILBOXES_FOLDER);
		allegatiFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_ATTACH_STORE_FOLDER);
		emlFolder = getValueStringDB(emf, ConfigurazionePecEnum.PEC_EML_STORE_FOLDER);

		checkFolder(mailboxFolder, true, true, false);
		checkFolder(allegatiFolder, true, true, true);
		checkFolder(emlFolder, true, true, true);
	}

	/**
	 * Procedura per verificare se cartella esiste, e se accesso in lettura o
	 * scruttura
	 * 
	 * @param folder
	 *            cartella da verificare
	 * @param autoCreate
	 *            auto creazione
	 * @param testRead
	 *            test lettura
	 * @param testWrite
	 *            test scrittura
	 * @throws PecException
	 */
	private static void checkFolder(String folder, boolean autoCreate, boolean testRead, boolean testWrite) throws PecException {
		File f = new File(folder);
		if (autoCreate && !f.exists()) {
			f.mkdirs();
			logger.warn("creato la cartella {}", f.toString());
		}

		if (testRead && !f.canRead()) {
			throw new PecException("Impossibile leggere dalla cartella " + f.toString());
		}

		if (testWrite && !f.canWrite()) {
			throw new PecException("Impossibile scrivere sulla cartella " + f.toString());
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
