package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.entities.AbstractEntityWithIdString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
