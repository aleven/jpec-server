package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.entities.AbstractEntityMarksWithIdLong;
import it.attocchi.jpa2.entities.EntityMarks;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(schema = "", name = "pec05_notifiche")
public class NotificaPec extends AbstractEntityMarksWithIdLong<NotificaPec> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec05_id")
	private long id;

	@Column(name = "pec05_tipo")
	private String tipo;

	@Column(name = "pec05_id_messaggio_padre")
	private long idMessaggioPadre;

	@Column(name = "pec05_destinatari")
	@Lob
	private String destinatari;

	@Column(name = "pec05_oggetto")
	@Lob
	private String oggetto;

	@Column(name = "pec05_messaggio")
	@Lob
	private String messaggio;

	@Column(name = "pec05_allegati")
	@Lob
	private String allegati;

	@Column(name = "pec05_stato_inviato")
	private boolean inviato;

	@Column(name = "pec05_data_invio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInvio;

	@Column(name = "pec05_protocollo")
	private String protocollo;

	@Column(name = "pec05_errore")
	@Lob
	private String errore;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(String destinatari) {
		this.destinatari = destinatari;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public String getAllegati() {
		return allegati;
	}

	public void setAllegati(String allegati) {
		this.allegati = allegati;
	}

	public boolean isInviato() {
		return inviato;
	}

	public void setInviato(boolean inviato) {
		this.inviato = inviato;
	}

	public Date getDataInvio() {
		return dataInvio;
	}

	public void setDataInvio(Date dataInvio) {
		this.dataInvio = dataInvio;
	}

	public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getErrore() {
		return errore;
	}

	public void setErrore(String errore) {
		this.errore = errore;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public long getIdMessaggioPadre() {
		return idMessaggioPadre;
	}

	public void setIdMessaggioPadre(long idMessaggioPadre) {
		this.idMessaggioPadre = idMessaggioPadre;
	}

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "dataCreazione", column = @Column(name = "pec05_dt_creazione")), @AttributeOverride(name = "dataModifica", column = @Column(name = "pec05_ts_modifica")), @AttributeOverride(name = "dataCancellazione", column = @Column(name = "pec05_dt_cancellazione")), @AttributeOverride(name = "utenteCreazioneId", column = @Column(name = "pec05_id_utente_creazione")), @AttributeOverride(name = "utenteModificaId", column = @Column(name = "pec05_id_utente_modifica")), @AttributeOverride(name = "utenteCancellazioneId", column = @Column(name = "pec05_id_utente_cancellazione")) })
	private EntityMarks entityMarks;

	@Override
	public EntityMarks getEntityMarks() {
		return entityMarks;
	}

	@Override
	public void setEntityMarks(EntityMarks entityMarks) {
		this.entityMarks = entityMarks;
	}

}
