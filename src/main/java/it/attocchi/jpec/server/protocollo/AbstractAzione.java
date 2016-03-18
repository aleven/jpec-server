package it.attocchi.jpec.server.protocollo;

public abstract class AbstractAzione implements AzioneGenerica {

	private AzioneContext context;
	private boolean testMode = false;

	@Override
	public AzioneGenerica inizialize(AzioneContext context) {
		this.context = context;
		return this;
	}

	@Override
	public AzioneContext getContext() {
		return context;
	}

	public boolean isTestMode() {
		return testMode;
	}

	public void setTestMode(boolean testMode) {
		this.testMode = testMode;
	}
	
	
}
