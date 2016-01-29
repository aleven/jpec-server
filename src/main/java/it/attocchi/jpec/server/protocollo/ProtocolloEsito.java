package it.attocchi.jpec.server.protocollo;

public class ProtocolloEsito {
	
	public enum ProtocolloEsitoStato {
		OK,
		ERRORE
	}

	private ProtocolloEsito() {
		this.stato = ProtocolloEsitoStato.ERRORE;
	}

	public static ProtocolloEsito ok(String protocollo) {
		ProtocolloEsito esitoErrore = new ProtocolloEsito();
		esitoErrore.stato = ProtocolloEsitoStato.OK;
		esitoErrore.protocollo = protocollo;
		return esitoErrore;
	}
	
	public static ProtocolloEsito errore(String messaggio) {
		ProtocolloEsito esitoErrore = new ProtocolloEsito();
		esitoErrore.errore = messaggio;
		return esitoErrore;
	}
	
	public ProtocolloEsitoStato stato;
	public String protocollo;
	public String errore;
	
	@Override
	public String toString() {
		return "ProtocolloEsito [stato=" + stato + ", protocollo=" + protocollo + ", errore=" + errore + "]";
	}
	
	
}