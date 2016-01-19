package it.attocchi.jpec.server.protocollo;

public class ProtocolloEsito {
	
	public enum ProtocolloEsitoStato {
		OK,
		ERRORE
	}

	public ProtocolloEsito() {
		this.stato = ProtocolloEsitoStato.ERRORE;
	}

	public ProtocolloEsito(String protocollo) {
		this.stato = ProtocolloEsitoStato.OK;
		this.protocollo = protocollo;
	}

	public ProtocolloEsitoStato stato;
	public String protocollo;
	public String errore;
	
	@Override
	public String toString() {
		return "ProtocolloEsito [stato=" + stato + ", protocollo=" + protocollo + ", errore=" + errore + "]";
	}
	
	
}