package it.attocchi.jpec.server.protocollo;

public abstract class AbstractAzione implements AzioneGenerica {

	private AzioneContext context;

	@Override
	public AzioneGenerica inizialize(AzioneContext context) {
		this.context = context;
		return this;
	}

	@Override
	public AzioneContext getContext() {
		return context;
	}
}
