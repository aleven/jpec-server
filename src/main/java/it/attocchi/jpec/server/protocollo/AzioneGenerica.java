package it.attocchi.jpec.server.protocollo;


public interface AzioneGenerica {
	
	AzioneEsito esegui();

	AzioneGenerica inizialize(AzioneContext context);
	AzioneContext getContext();
	
}
