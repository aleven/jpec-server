package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.entities.AbstractEntityMarksWithIdLong;
import it.attocchi.jpa2.entities.EntityMarks;
import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.utils.HtmlUtils;

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

import org.apache.commons.lang3.StringUtils;

@Entity
@Table(schema = "", name = "pec01_messaggi")
public class MessaggioPec extends AbstractEntityMarksWithIdLong<MessaggioPec> {

	public enum Folder {
		IN,
		OUT
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec01_id")
	private long id;

	@Column(name = "pec01_destinatari")
	@Lob
	private String destinatari;

	@Column(name = "pec01_oggetto")
	@Lob
	private String oggetto;

	@Column(name = "pec01_messaggio")
	@Lob
	private String messaggio;

	@Column(name = "pec01_protocollo")
	private String protocollo;

	@Column(name = "pec01_data_invio")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInvio;

	@Column(name = "pec01_stato_inviato")
	private boolean inviato;

	@Column(name = "pec01_stato_accettato", nullable = false)
	private boolean accettato;

	@Column(name = "pec01_stato_consegnato", nullable = false)
	private boolean consegnato;

	@Column(name = "pec01_stato_anomalia", nullable = false)
	private boolean anomalia;

	@Column(name = "pec01_accettato_id")
	private Long accettatoIdMessaggio;

	@Column(name = "pec01_consegnato_id")
	private Long consegnatoIdMessaggio;

	@Column(name = "pec01_anomalia_id")
	private Long anomaliaIdMessaggio;

	@Column(name = "pec01_folder")
	private String folder;

	@Column(name = "pec01_eml_file")
	private String emlFile;

	// @Column(name = "pec01_id_utente", nullable = false)
	// private int idUtente;

	@Column(name = "pec01_mittente_email")
	private String emailMittente;
	@Column(name = "pec01_mittente_nome")
	private String nomeMittente;
	@Column(name = "pec01_mittente_username")
	private String usernameMittente;

	/* Dati Messaggi Ricevuti */

	@Column(name = "pec01_data_ricezione")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataRicezione;

	@Column(name = "pec01_data_invio_originale")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dataInvioOriginale;

	@Column(name = "pec01_processato")
	private boolean processato;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "dataCreazione", column = @Column(name = "pec01_dt_creazione")), @AttributeOverride(name = "dataModifica", column = @Column(name = "pec01_ts_modifica")), @AttributeOverride(name = "dataCancellazione", column = @Column(name = "pec01_dt_cancellazione")), @AttributeOverride(name = "utenteCreazioneId", column = @Column(name = "pec01_id_utente_creazione")), @AttributeOverride(name = "utenteModificaId", column = @Column(name = "pec01_id_utente_modifica")), @AttributeOverride(name = "utenteCancellazioneId", column = @Column(name = "pec01_id_utente_cancellazione")) })
	private EntityMarks entityMarks;

	@Column(name = "pec01_stato_inoltrato")
	private boolean inoltrato;

	@Column(name = "pec01_inoltrato_id_utente")
	private long inotratoIdUtente;

	@Column(name = "pec01_inoltrato_destinatari")
	private String inoltratoDestinatari;

	@Column(name = "pec01_inoltrato_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date inotratoData;

	@Column(name = "pec01_letto")
	private boolean letto;

	@Column(name = "pec01_letto_id_utente")
	private long lettoIdUtente;

	@Column(name = "pec01_letto_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lettoData;

	@Column(name = "pec01_postacert_file")
	private String postacertFile;

	@Column(name = "pec01_postacert_body")
	@Lob
	private String postacertBody;

	@Column(name = "pec01_postacert_contenttype")
	private String postacertContentType;

	@Column(name = "pec01_archiviato")
	private boolean archiviato;

	@Column(name = "pec01_archiviato_id_utente")
	private long archiviatoIdUtente;

	@Column(name = "pec01_archiviato_data")
	@Temporal(TemporalType.TIMESTAMP)
	private Date archiviatoData;

	@Column(name = "pec01_message_id")
	private String messageID;

	@Column(name = "pec01_x_riferimento_message_id")
	private String xRiferimentoMessageID;

	@Column(name = "pec01_x_ricevuta")
	private String xRicevuta;

	@Column(name = "pec01_x_tipo_ricevuta")
	private String xTipoRicevuta;

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

	@Override
	public EntityMarks getEntityMarks() {
		return entityMarks;
	}

	public void setEntityMarks(EntityMarks entityMarks) {
		this.entityMarks = entityMarks;
	}

	public boolean isInviato() {
		return inviato;
	}

	public void setInviato(boolean inviato) {
		this.inviato = inviato;
	}

	public boolean isAccettato() {
		return accettato;
	}

	public void setAccettato(boolean accettato) {
		this.accettato = accettato;
	}

	public boolean isConsegnato() {
		return consegnato;
	}

	public void setConsegnato(boolean consegnato) {
		this.consegnato = consegnato;
	}

	public Date getDataRicezione() {
		return dataRicezione;
	}

	public void setDataRicezione(Date dataRicezione) {
		this.dataRicezione = dataRicezione;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public Long getAccettatoIdMessaggio() {
		return accettatoIdMessaggio;
	}

	public void setAccettatoIdMessaggio(Long accettatoIdMessaggio) {
		this.accettatoIdMessaggio = accettatoIdMessaggio;
	}

	public Long getConsegnatoIdMessaggio() {
		return consegnatoIdMessaggio;
	}

	public void setConsegnatoIdMessaggio(Long consegnatoIdMessaggio) {
		this.consegnatoIdMessaggio = consegnatoIdMessaggio;
	}

	public Long getAnomaliaIdMessaggio() {
		return anomaliaIdMessaggio;
	}

	public void setAnomaliaIdMessaggio(Long anomaliaIdMessaggio) {
		this.anomaliaIdMessaggio = anomaliaIdMessaggio;
	}

	public boolean isAnomalia() {
		return anomalia;
	}

	public void setAnomalia(boolean anomalia) {
		this.anomalia = anomalia;
	}

	public String getEmlFile() {
		return emlFile;
	}

	public void setEmlFile(String emlFile) {
		this.emlFile = emlFile;
	}

	public String getEmailMittente() {
		return emailMittente;
	}

	public void setEmailMittente(String emailMittente) {
		this.emailMittente = emailMittente;
	}

	public String getNomeMittente() {
		return nomeMittente;
	}

	public void setNomeMittente(String nomeMittente) {
		this.nomeMittente = nomeMittente;
	}

	public String getUsernameMittente() {
		return usernameMittente;
	}

	public void setUsernameMittente(String usernameMittente) {
		this.usernameMittente = usernameMittente;
	}

	public boolean isProcessato() {
		return processato;
	}

	public void setProcessato(boolean processato) {
		this.processato = processato;
	}

	public Date getDataInvioOriginale() {
		return dataInvioOriginale;
	}

	public void setDataInvioOriginale(Date dataInvioOriginale) {
		this.dataInvioOriginale = dataInvioOriginale;
	}

	public boolean isInoltrato() {
		return inoltrato;
	}

	public void setInoltrato(boolean inoltrato) {
		this.inoltrato = inoltrato;
	}

	public long getInotratoIdUtente() {
		return inotratoIdUtente;
	}

	public void setInotratoIdUtente(long inotratoIdUtente) {
		this.inotratoIdUtente = inotratoIdUtente;
	}

	public Date getInotratoData() {
		return inotratoData;
	}

	public void setInotratoData(Date inotratoData) {
		this.inotratoData = inotratoData;
	}

	public String getInoltratoDestinatari() {
		return inoltratoDestinatari;
	}

	public void setInoltratoDestinatari(String inoltratoDestinatari) {
		this.inoltratoDestinatari = inoltratoDestinatari;
	}

	public String getStatoDescrizione() {
		String res = "";

		if (!isInviato()) {
			res = "Da Inviare";
		} else if (isAnomalia()) {
			res = "Anomalia";
		} else if (isInviato() && !isAccettato() && !isConsegnato()) {
			res = "Inviato";
		} else if (isInviato() && isAccettato() && !isConsegnato()) {
			res = "Accettato";
		} else if (isInviato() && isAccettato() && isConsegnato()) {
			res = "Consegnato";
		}

		if (isInoltrato()) {
			res = "Inoltrato";
		}

		return res;
	}

	public static MessaggioPec createNew(String utente, Folder folder) {
		MessaggioPec nuovo = new MessaggioPec();

		nuovo.folder = folder.name();

		nuovo.setProcessato(false);

		nuovo.setLetto(false);

		return nuovo;
	}

	// /**
	// * Meccanismo Lazy Load per Stream del Messaggio
	// *
	// * @return
	// */
	// public StreamedContent getConsegnatoFile() {
	//
	// StreamedContent file = null;
	//
	// if (consegnatoIdMessaggio > 0) {
	//
	// Messaggio m = JPA
	// String emlPath =
	// InputStream stream = ((ServletContext)
	// FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/images/optimusprime.jpg");
	// // file = new DefaultStreamedContent(stream, "image/jpg",
	// "downloaded_optimus.jpg");
	//
	// file = new DefaultStreamedContent(stream);
	// }
	//
	// return file;
	//
	// }

	public String getMessaggioHtml() {
		// return
		// HtmlUtils.encodeForEscape(HtmlUtils.encodeWebUrl(getMessaggio()));
		return HtmlUtils.encodeForEscape(getMessaggio());
	}

	public boolean isLetto() {
		return letto;
	}

	public void setLetto(boolean letto) {
		this.letto = letto;
	}

	public long getLettoIdUtente() {
		return lettoIdUtente;
	}

	public void setLettoIdUtente(long lettoIdUtente) {
		this.lettoIdUtente = lettoIdUtente;
	}

	public Date getLettoData() {
		return lettoData;
	}

	public void setLettoData(Date lettoData) {
		this.lettoData = lettoData;
	}

	public String getPostacertFile() {
		return postacertFile;
	}

	public void setPostacertFile(String postacertFile) {
		this.postacertFile = postacertFile;
	}

	public String getPostacertBody() {
		return postacertBody;
	}

	public void setPostacertBody(String postacertBody) {
		this.postacertBody = postacertBody;
	}

	public String getPostacertBodyHtml() {
		return HtmlUtils.encodeForEscape(getPostacertBody());
	}

	public String getPostacertContentType() {
		return postacertContentType;
	}

	public void setPostacertContentType(String postacertContentType) {
		this.postacertContentType = postacertContentType;
	}

	// public String getOggettoAbbreviate() {
	// String res = oggetto;
	// int max = ConfigurazionePec.get().getLayoutColumnMaxChar();
	// if (StringUtils.isNotBlank(res) && max > 0) {
	// res = StringUtils.abbreviate(res, max);
	// }
	// return res;
	// }

	public boolean isArchiviabile() {
		return StringUtils.isNotEmpty(getOggetto()) && !getOggetto().startsWith(MessaggioPecBL.OGGETTO_POSTA_CERTIFICATA);
	}

	public boolean isArchiviato() {
		return archiviato;
	}

	public void setArchiviato(boolean archiviato) {
		this.archiviato = archiviato;
	}

	public long getArchiviatoIdUtente() {
		return archiviatoIdUtente;
	}

	public void setArchiviatoIdUtente(long archiviatoIdUtente) {
		this.archiviatoIdUtente = archiviatoIdUtente;
	}

	public Date getArchiviatoData() {
		return archiviatoData;
	}

	public void setArchiviatoData(Date archiviatoData) {
		this.archiviatoData = archiviatoData;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getxRiferimentoMessageID() {
		return xRiferimentoMessageID;
	}

	public void setxRiferimentoMessageID(String xRiferimentoMessageID) {
		this.xRiferimentoMessageID = xRiferimentoMessageID;
	}

	public String getxRicevuta() {
		return xRicevuta;
	}

	public void setxRicevuta(String xRicevuta) {
		this.xRicevuta = xRicevuta;
	}

	public String getxTipoRicevuta() {
		return xTipoRicevuta;
	}

	public void setxTipoRicevuta(String xTipoRicevuta) {
		this.xTipoRicevuta = xTipoRicevuta;
	}

}
