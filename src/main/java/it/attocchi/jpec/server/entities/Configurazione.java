package it.attocchi.jpec.server.entities;

import it.attocchi.jpa2.JpaController;

import java.io.Serializable;

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
public class Configurazione implements Serializable {

	protected static final Logger logger = LoggerFactory.getLogger(Configurazione.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Configurazione cache = null;

	public static Configurazione get() {
		if (cache == null) {
			logger.error("Configurazione not intialized");
		}
		return cache;
	}

	/**
	 * Bisognerebbe prevedere l'inizializzazione da programma in modo che poi vi
	 * si accede via cache. Questo e' utile nel caso di chiamate da proprieta'
	 * di oggetti, come Messaggio che non hanno accesso al DB
	 * 
	 * @param emf
	 * @return
	 */
	public static Configurazione init(EntityManagerFactory emf) {
		return get(emf);
	}

	public static Configurazione get(EntityManagerFactory emf) {
		if (cache == null) {
			try {
				JpaController c = new JpaController(emf);
				cache = c.findFirst(Configurazione.class);

				if (cache == null) {
					// Ne creaiamo una nuova
					cache = new Configurazione();
					cache.setAllegatiStoreDb(false);

					c.insert(cache);

					cache = c.findFirst(Configurazione.class);
				}

			} catch (Exception ex) {
				logger.error("getCurrent", ex);
			}
		}

		return cache;
	}

	public static void reloadCurrent() {
		cache = null;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "pec03_id")
	private long id;

	@Column(name = "pec03_attach_store_db", nullable = false)
	private boolean allegatiStoreDb;

	@Column(name = "pec03_attach_store_folder")
	private String allegatiStoreFolder;

	@Column(name = "pec03_enable_email_send", nullable = false)
	private boolean enableEmailSend;

	@Column(name = "pec03_smtp_server")
	private String smtpServer;

	@Column(name = "pec03_smtp_port", nullable = false)
	private int smtpPort;

	@Column(name = "pec03_sender_email")
	private String senderEmail;

	@Column(name = "pec03_sender_name")
	private String senderName;

	@Column(name = "pec03_smtp_ssl", nullable = false)
	private boolean enableSmtpSSL;

	@Column(name = "pec03_smtp_sslnocheck", nullable = false)
	private boolean enableSmtpSSLNoCertCheck;

	@Column(name = "pec03_smtp_username")
	private String smtpUsername;

	@Column(name = "pec03_smtp_password")
	private String smtpPassword;

	@Column(name = "pec03_enable_email_check", nullable = false)
	private boolean enableEmailCheck;

	@Column(name = "pec03_pop_server")
	private String popServer;
	@Column(name = "pec03_pop_port")
	private int popPort;
	@Column(name = "pec03_pop_username")
	private String popUsername;
	@Column(name = "pec03_pop_password")
	private String popPassword;
	@Column(name = "pec03_pop_ssl")
	private boolean enablePopSSL;
	@Column(name = "pec03_pop_sslnocheck")
	private boolean enablePopSSLNoCertCheck;

	@Column(name = "pec03_notifiche_smtp_server")
	private String smtpServerNotifiche;
	@Column(name = "pec03_notifiche_smtp_port", nullable = false)
	private int smtpPortNotifiche;
	@Column(name = "pec03_notifiche_sender_email")
	private String senderEmailNotifiche;
	@Column(name = "pec03_notifiche_sender_name")
	private String senderNameNotifiche;

	@Column(name = "pec03_eml_store_folder")
	private String emlStoreFolder;

	@Column(name = "pec03_enable_eml_store", nullable = false)
	private boolean enableEmlStore;

	@Column(name = "pec03_enable_notity_send", nullable = false)
	private boolean enableNotifySend;

	@Column(name = "pec03_enable_notity_status", nullable = false)
	private boolean enableNotifyStatus;

	@Column(name = "pec03_notifica_invio_destinatari")
	private String notificaInvioDestinatari;

	@Column(name = "pec03_notifica_cambiostato_destinatari")
	private String notificaCambioStatoDestinatari;

	@Column(name = "pec03_folder_out")
	private String outStoreFolder;

	@Column(name = "pec03_folder_in")
	private String inStoreFolder;

	@Column(name = "pec03_delete_server_message", nullable = false)
	private boolean deleteMessageFromServer;

	@Column(name = "pec03_message_wait_feedback_minutes", nullable = false)
	private int messageWaitFeedbackInMinutes;

	@Column(name = "pec03_notifica_inoltro_destinatari", nullable = false)
	private String notificaInoltroDestinatari;

	@Column(name = "pec03_notifica_ricezione_messaggio", nullable = false)
	private String notificaRicezioneMessaggio;

	@Column(name = "pec03_notifica_cambiostato_messaggio", nullable = false)
	private String notificaCambioStatoMessaggio;

	@Column(name = "pec03_notifica_obsoleto_messaggio", nullable = false)
	private String notificaObsoletoMessaggio;

	@Column(name = "pec03_notifica_prefisso_invio", nullable = false)
	private String notificaPrefissoInvio;

	@Column(name = "pec03_notifica_prefisso_cambio_stato", nullable = false)
	private String notificaPrefissoCambioStato;

	@Column(name = "pec03_notifica_prefisso_ricezione", nullable = false)
	private String notificaPrefissoRicezione;

	@Column(name = "pec03_notifica_prefisso_obsoleto", nullable = false)
	private String notificaPrefissoObsoleto;

	@Column(name = "pec03_notifica_prefisso_inoltro", nullable = false)
	private String notificaPrefissoInoltro;

	@Column(name = "pec03_layout_column_max_char", nullable = false)
	private int layoutColumnMaxChar;

	@Column(name = "pec03_postacert_extract", nullable = false)
	private String postacertExtract;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean isAllegatiStoreDb() {
		return allegatiStoreDb;
	}

	public void setAllegatiStoreDb(boolean allegatiStoreDb) {
		this.allegatiStoreDb = allegatiStoreDb;
	}

	public String getAllegatiStoreFolder() {
		return allegatiStoreFolder;
	}

	public void setAllegatiStoreFolder(String allegatiStoreFolder) {
		this.allegatiStoreFolder = allegatiStoreFolder;
	}

	public boolean isEnableEmailSend() {
		return enableEmailSend;
	}

	public void setEnableEmailSend(boolean enableEmailSend) {
		this.enableEmailSend = enableEmailSend;
	}

	public String getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(String smtpServer) {
		this.smtpServer = smtpServer;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

	public void setSmtpPort(int smtpPort) {
		this.smtpPort = smtpPort;
	}

	public String getSenderEmail() {
		return senderEmail;
	}

	public void setSenderEmail(String senderEmail) {
		this.senderEmail = senderEmail;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean isEnableSmtpSSL() {
		return enableSmtpSSL;
	}

	public void setEnableSmtpSSL(boolean enableSmtpSSL) {
		this.enableSmtpSSL = enableSmtpSSL;
	}

	public boolean isEnableSmtpSSLNoCertCheck() {
		return enableSmtpSSLNoCertCheck;
	}

	public void setEnableSmtpSSLNoCertCheck(boolean enableSmtpSSLNoCertCheck) {
		this.enableSmtpSSLNoCertCheck = enableSmtpSSLNoCertCheck;
	}

	public String getSmtpUsername() {
		return smtpUsername;
	}

	public void setSmtpUsername(String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public void setSmtpPassword(String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}

	public String getPopServer() {
		return popServer;
	}

	public void setPopServer(String popServer) {
		this.popServer = popServer;
	}

	public int getPopPort() {
		return popPort;
	}

	public void setPopPort(int popPort) {
		this.popPort = popPort;
	}

	public String getPopUsername() {
		return popUsername;
	}

	public void setPopUsername(String popUsername) {
		this.popUsername = popUsername;
	}

	public String getPopPassword() {
		return popPassword;
	}

	public void setPopPassword(String popPassword) {
		this.popPassword = popPassword;
	}

	public boolean isEnablePopSSL() {
		return enablePopSSL;
	}

	public void setEnablePopSSL(boolean enablePopSSL) {
		this.enablePopSSL = enablePopSSL;
	}

	public boolean isEnablePopSSLNoCertCheck() {
		return enablePopSSLNoCertCheck;
	}

	public void setEnablePopSSLNoCertCheck(boolean enablePopSSLNoCertCheck) {
		this.enablePopSSLNoCertCheck = enablePopSSLNoCertCheck;
	}

	public boolean isEnableEmailCheck() {
		return enableEmailCheck;
	}

	public void setEnableEmailCheck(boolean enableEmailCheck) {
		this.enableEmailCheck = enableEmailCheck;
	}

	public String getSmtpServerNotifiche() {
		return smtpServerNotifiche;
	}

	public void setSmtpServerNotifiche(String smtpServerNotifiche) {
		this.smtpServerNotifiche = smtpServerNotifiche;
	}

	public int getSmtpPortNotifiche() {
		return smtpPortNotifiche;
	}

	public void setSmtpPortNotifiche(int smtpPortNotifiche) {
		this.smtpPortNotifiche = smtpPortNotifiche;
	}

	public String getSenderEmailNotifiche() {
		return senderEmailNotifiche;
	}

	public void setSenderEmailNotifiche(String senderEmailNotifiche) {
		this.senderEmailNotifiche = senderEmailNotifiche;
	}

	public String getSenderNameNotifiche() {
		return senderNameNotifiche;
	}

	public void setSenderNameNotifiche(String senderNameNotifiche) {
		this.senderNameNotifiche = senderNameNotifiche;
	}

	public String getEmlStoreFolder() {
		return emlStoreFolder;
	}

	public void setEmlStoreFolder(String emlStoreFolder) {
		this.emlStoreFolder = emlStoreFolder;
	}

	public boolean isEnableNotifySend() {
		return enableNotifySend;
	}

	public void setEnableNotifySend(boolean enableNotifySend) {
		this.enableNotifySend = enableNotifySend;
	}

	public String getNotificaInvioDestinatari() {
		return notificaInvioDestinatari;
	}

	public void setNotificaInvioDestinatari(String notificaInvioDestinatari) {
		this.notificaInvioDestinatari = notificaInvioDestinatari;
	}

	public String getNotificaCambioStatoDestinatari() {
		return notificaCambioStatoDestinatari;
	}

	public void setNotificaCambioStatoDestinatari(String notificaCambioStatoDestinatari) {
		this.notificaCambioStatoDestinatari = notificaCambioStatoDestinatari;
	}

	public boolean isEnableEmlStore() {
		return enableEmlStore;
	}

	public void setEnableEmlStore(boolean enableEmlStore) {
		this.enableEmlStore = enableEmlStore;
	}

	public boolean isEnableNotifyStatus() {
		return enableNotifyStatus;
	}

	public void setEnableNotifyStatus(boolean enableNotifyStatus) {
		this.enableNotifyStatus = enableNotifyStatus;
	}

	public String getOutStoreFolder() {
		return outStoreFolder;
	}

	public void setOutStoreFolder(String outStoreFolder) {
		this.outStoreFolder = outStoreFolder;
	}

	public String getInStoreFolder() {
		return inStoreFolder;
	}

	public void setInStoreFolder(String inStoreFolder) {
		this.inStoreFolder = inStoreFolder;
	}

	public boolean isDeleteMessageFromServer() {
		return deleteMessageFromServer;
	}

	public void setDeleteMessageFromServer(boolean deleteMessageFromServer) {
		this.deleteMessageFromServer = deleteMessageFromServer;
	}

	public int getMessageWaitFeedbackInMinutes() {
		return messageWaitFeedbackInMinutes;
	}

	public void setMessageWaitFeedbackInMinutes(int messageWaitFeedbackInMinutes) {
		this.messageWaitFeedbackInMinutes = messageWaitFeedbackInMinutes;
	}

	public String getNotificaInoltroDestinatari() {
		return notificaInoltroDestinatari;
	}

	public void setNotificaInoltroDestinatari(String notificaInoltroDestinatari) {
		this.notificaInoltroDestinatari = notificaInoltroDestinatari;
	}

	public String getNotificaRicezioneMessaggio() {
		return notificaRicezioneMessaggio;
	}

	public void setNotificaRicezioneMessaggio(String notificaRicezioneMessaggio) {
		this.notificaRicezioneMessaggio = notificaRicezioneMessaggio;
	}

	public String getNotificaCambioStatoMessaggio() {
		return notificaCambioStatoMessaggio;
	}

	public void setNotificaCambioStatoMessaggio(String notificaCambioStatoMessaggio) {
		this.notificaCambioStatoMessaggio = notificaCambioStatoMessaggio;
	}

	public String getNotificaObsoletoMessaggio() {
		return notificaObsoletoMessaggio;
	}

	public void setNotificaObsoletoMessaggio(String notificaObsoletoMessaggio) {
		this.notificaObsoletoMessaggio = notificaObsoletoMessaggio;
	}

	public String getNotificaPrefissoInvio() {
		return notificaPrefissoInvio;
	}

	public void setNotificaPrefissoInvio(String notificaPrefissoInvio) {
		this.notificaPrefissoInvio = notificaPrefissoInvio;
	}

	public String getNotificaPrefissoCambioStato() {
		return notificaPrefissoCambioStato;
	}

	public void setNotificaPrefissoCambioStato(String notificaPrefissoCambioStato) {
		this.notificaPrefissoCambioStato = notificaPrefissoCambioStato;
	}

	public String getNotificaPrefissoRicezione() {
		return notificaPrefissoRicezione;
	}

	public void setNotificaPrefissoRicezione(String notificaPrefissoRicezione) {
		this.notificaPrefissoRicezione = notificaPrefissoRicezione;
	}

	public String getNotificaPrefissoObsoleto() {
		return notificaPrefissoObsoleto;
	}

	public void setNotificaPrefissoObsoleto(String notificaPrefissoObsoleto) {
		this.notificaPrefissoObsoleto = notificaPrefissoObsoleto;
	}

	public String getNotificaPrefissoInoltro() {
		return notificaPrefissoInoltro;
	}

	public void setNotificaPrefissoInoltro(String notificaPrefissoInoltro) {
		this.notificaPrefissoInoltro = notificaPrefissoInoltro;
	}

	public int getLayoutColumnMaxChar() {
		return layoutColumnMaxChar;
	}

	public void setLayoutColumnMaxChar(int layoutColumnMaxChar) {
		this.layoutColumnMaxChar = layoutColumnMaxChar;
	}

	public String getPostacertExtract() {
		return postacertExtract;
	}

	public void setPostacertExtract(String postacertExtract) {
		this.postacertExtract = postacertExtract;
	}

}
