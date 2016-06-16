package it.attocchi.jpec.server.protocollo;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;


public class AzioneEsito {
	
	public enum AzioneEsitoStato {
		OK,
		REGOLA_NON_APPLICABILE,
		NOTIFICA,
		ERRORE
	}

	private AzioneEsito() {
		this.stato = AzioneEsitoStato.ERRORE;
	}

	public static AzioneEsito ok(String protocollo, String urlDocumentale) {
		AzioneEsito esitoOk = new AzioneEsito();
		esitoOk.stato = AzioneEsitoStato.OK;
		esitoOk.protocollo = protocollo;
		esitoOk.urlDocumentale = urlDocumentale;
		return esitoOk;
	}
	
	public static AzioneEsito ok(String protocollo, String urlDocumentale, String nota) {
		AzioneEsito esitoOk = ok(protocollo, urlDocumentale);
		esitoOk.errore = nota;
		return esitoOk;
	}
	
	public static AzioneEsito notifica(String messaggio, Throwable ex) {
		AzioneEsito esitoErrore = new AzioneEsito();
		esitoErrore.stato = AzioneEsitoStato.NOTIFICA;
		esitoErrore.errore = messaggio;
		esitoErrore.eccezione = ex;
		return esitoErrore;
	}
	
	public static AzioneEsito errore(String messaggio, Throwable ex) {
		AzioneEsito esitoErrore = new AzioneEsito();
		esitoErrore.errore = messaggio;
		esitoErrore.eccezione = ex;
		return esitoErrore;
	}
	
	public static AzioneEsito regolaNonApplicabile(String messaggio) {
		AzioneEsito esito = new AzioneEsito();
		esito.stato = AzioneEsitoStato.REGOLA_NON_APPLICABILE;
		esito.errore = messaggio;
		return esito;
	}	
	
	public AzioneEsitoStato stato;
	public String protocollo;
	public String urlDocumentale;
	public String errore;
	public Throwable eccezione;
	
	@Override
	public String toString() {
		return "ProtocolloEsito [stato=" + stato + ", protocollo=" + protocollo + ", errore=" + errore + "]";
	}
	
	private StringBuffer sb = new StringBuffer();
	
	public void logAndBuffer(Logger logger, String message, Object... argArray) {
		sb.append(MessageFormatter.format(message, argArray).getMessage());
		sb.append(System.getProperty("line.separator"));
		// aggiungi al log 
		logger.info(message, argArray);
	}
	
	public String getBufferedLog() {
		return sb.toString();
	}
}