package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.JpaController;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(schema = "", name = "pec03_config")
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
	public static Map<String, ConfigurazionePec> init(EntityManagerFactory emf) {
		return get(emf);
	}

	public static Map<String, ConfigurazionePec> get(EntityManagerFactory emf) {
		if (cache == null) {
			try {
				JpaController controller = new JpaController(emf);
				List<ConfigurazionePec> data = controller.findAll(ConfigurazionePec.class);

				// se non ci sono dati inserisco quelli di default
				if (data == null || data.size() == 0) {
					for (ConfigurazionePecEnum value : ConfigurazionePecEnum.values()) {
						ConfigurazionePec newConfigurazione = new ConfigurazionePec();
						newConfigurazione.setNome(value.name());
						controller.insert(newConfigurazione);
					}
					// refresh dei dati
					data = controller.findAll(ConfigurazionePec.class);
				}

				cache = new HashMap<String, ConfigurazionePec>();
				for (ConfigurazionePec configurazione : data) {
					cache.put(configurazione.getNome(), configurazione);
				}

			} catch (Exception ex) {
				logger.error("getCurrent", ex);
			}
		}

		return cache;
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
		cache = null;
	}

	@Id
	@Column(name = "pec03_nome")
	private String nome;

	@Column(name = "pec03_valore")
	private String valore;

	@Column(name = "pec03_descrizione")
	private String descrizione;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getValore() {
		return valore;
	}

	public void setValore(String valore) {
		this.valore = valore;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

}
