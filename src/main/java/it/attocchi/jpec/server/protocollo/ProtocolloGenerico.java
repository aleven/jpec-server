package it.attocchi.jpec.server.protocollo;

public interface ProtocolloGenerico {
	
	String esegui();

	void inizialize(ProtocolloContext context);
	ProtocolloContext getContext();
	
}
