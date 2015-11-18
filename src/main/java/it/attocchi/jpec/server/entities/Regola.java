package it.attocchi.jpec.server.entities;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "pec06_regole")
public class Regola implements Serializable {

	protected static final Logger logger = Logger.getLogger(Regola.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec06_id")
	private long id;

	@Column(name = "pec06_evento")
	private String evento;

	@Column(name = "pec06_criterio")
	@Lob
	private String criterio;

	@Column(name = "pec06_azione")
	@Lob
	private String azione;

	@Column(name = "pec06_ordine")
	private int ordine;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public String getAzione() {
		return azione;
	}

	public void setAzione(String azione) {
		this.azione = azione;
	}

	public int getOrdine() {
		return ordine;
	}

	public void setOrdine(int ordine) {
		this.ordine = ordine;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
