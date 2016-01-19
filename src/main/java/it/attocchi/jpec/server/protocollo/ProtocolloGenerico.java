package it.attocchi.jpec.server.protocollo;


public interface ProtocolloGenerico {
	
	ProtocolloEsito esegui();

	ProtocolloGenerico inizialize(ProtocolloContext context);
	ProtocolloContext getContext();
	
}
