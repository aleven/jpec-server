package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.JpaController;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "pec03_config")
public class ConfigurazionePec implements Serializable {

	protected static final Logger logger = LoggerFactory.getLogger(ConfigurazionePec.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Map<String, ConfigurazionePec> cache = null;

	public static ConfigurazionePec get(ConfigurazionePecEnum chiave) {
		ConfigurazionePec res = null;
		if (cache == null) {
			logger.error("Configurazione not intialized");
		} else {
			if (cache.containsKey(chiave.name())) {
				res = cache.get(chiave.name());
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
	public static ConfigurazionePec init(EntityManagerFactory emf) {
		return get(emf);
	}

	public static ConfigurazionePec get(EntityManagerFactory emf) {
		if (cache == null) {
			try {
				JpaController c = new JpaController(emf);
				cache = c.findAll(ConfigurazionePec.class);

				if (cache == null) {
					// Ne creaiamo una nuova
					cache = new ConfigurazionePec();
					cache.setAllegatiStoreDb(false);

					c.insert(cache);

					cache = c.findFirst(ConfigurazionePec.class);
				}

			} catch (Exception ex) {
				logger.error("getCurrent", ex);
			}
		}

		return cache;
	}

	public static void resetCurrent() {
		cache = null;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec03_id")
	private long id;

	@Column(name = "pec03_nome")
	private String nome;

	@Column(name = "pec03_descrizione")
	private String descrizione;

	@Column(name = "pec03_valore")
	private String valore;

	@Column(name = "pec03_tipocostante")
	private String tipocostante;

}
