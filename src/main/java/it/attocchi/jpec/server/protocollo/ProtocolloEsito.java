package it.attocchi.jpec.server.protocollo;

import org.slf4j.Logger;
import org.slf4j.helpers.MessageFormatter;


public class ProtocolloEsito {
	
	public enum ProtocolloEsitoStato {
		OK,
		ERRORE
	}

	private ProtocolloEsito() {
		this.stato = ProtocolloEsitoStato.ERRORE;
	}

	public static ProtocolloEsito ok(String protocollo, String urlDocumentale) {
		ProtocolloEsito esitoOk = new ProtocolloEsito();
		esitoOk.stato = ProtocolloEsitoStato.OK;
		esitoOk.protocollo = protocollo;
		esitoOk.urlDocumentale = urlDocumentale;
		return esitoOk;
	}
	
	public static ProtocolloEsito errore(String messaggio, Throwable ex) {
		ProtocolloEsito esitoErrore = new ProtocolloEsito();
		esitoErrore.errore = messaggio;
		esitoErrore.eccezione = ex;
		return esitoErrore;
	}
	
	public ProtocolloEsitoStato stato;
	public String protocollo;
	public String urlDocumentale;
	public String errore;
	public Throwable eccezione;
	
	@Override
	public String toString() {
		return "ProtocolloEsito [stato=" + stato + ", protocollo=" + protocollo + ", errore=" + errore + "]";
	}
	
	StringBuffer sb = new StringBuffer();
	
	public void logAndBuffer(Logger logger, String message, Object... argArray) {
		sb.append(MessageFormatter.format(message, argArray));
		sb.append(System.getProperty("line.separator"));
		// aggiungi al log 
		logger.info(message, argArray);
	}
	
	public String getBufferedLog() {
		return sb.toString();
	}
}