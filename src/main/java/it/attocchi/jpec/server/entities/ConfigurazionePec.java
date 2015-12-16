package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.JpaController;
import it.attocchi.jpa2.entities.AbstractEntityWithIdString;
import it.attocchi.jpec.server.bl.ConfigurazionePecEnum;

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
public class ConfigurazionePec extends AbstractEntityWithIdString {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public String getId() {
		return getNome();
	}

}
