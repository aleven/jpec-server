package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.entities.AbstractEntityMarksWithIdLong;
import it.attocchi.jpa2.entities.EntityMarks;

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

import org.apache.commons.io.FilenameUtils;

@Entity
@Table(name = "pec02_allegati")
public class AllegatoPec extends AbstractEntityMarksWithIdLong<AllegatoPec> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec02_id")
	private long id;

	@Column(name = "pec02_id_pec01")
	private long idMessaggio;

	@Lob
	@Column(name = "pec02_data")
	private byte[] data;

	@Column(name = "pec02_file_name")
	private String fileName;

	@Column(name = "pec02_content_type")
	private String contetType;

	@Column(name = "pec02_size")
	private long size;

	@Column(name = "pec02_store_file_name")
	private String storeFileName;

	@Column(name = "pec02_store_path")
	private String storePath;
	
	@Column(name = "pec02_store_url")
	private String storeUrl;	

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "dataCreazione", column = @Column(name = "pec02_dt_creazione")), @AttributeOverride(name = "dataModifica", column = @Column(name = "pec02_ts_modifica")), @AttributeOverride(name = "dataCancellazione", column = @Column(name = "pec02_dt_cancellazione")), @AttributeOverride(name = "utenteCreazioneId", column = @Column(name = "pec02_id_utente_creazione")), @AttributeOverride(name = "utenteModificaId", column = @Column(name = "pec02_id_utente_modifica")), @AttributeOverride(name = "utenteCancellazioneId", column = @Column(name = "pec02_id_utente_cancellazione")) })
	private EntityMarks entityMarks;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(long idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	@Override
	public EntityMarks getEntityMarks() {
		return entityMarks;
	}

	@Override
	public void setEntityMarks(EntityMarks entityMarks) {
		this.entityMarks = entityMarks;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContetType() {
		return contetType;
	}

	public void setContetType(String contetType) {
		this.contetType = contetType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getStoreFileName() {
		return storeFileName;
	}

	public void setStoreFileName(String storeFileName) {
		this.storeFileName = storeFileName;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getOnlyFileName() {
		return FilenameUtils.getName(fileName);
	}

	// public StreamedContent getStreamedContent() {
	// InputStream in = new ByteArrayInputStream(data);
	// return new DefaultStreamedContent(in, contetType, getOnlyFileName());
	// }
}
