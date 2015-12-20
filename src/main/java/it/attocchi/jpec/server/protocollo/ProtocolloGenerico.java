package it.attocchi.jpec.server.protocollo;

public interface ProtocolloGenerico {
	
	String esegui();

	ProtocolloGenerico inizialize(ProtocolloContext context);
	ProtocolloContext getContext();
	
}
