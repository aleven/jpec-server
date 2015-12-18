package it.attocchi.jpec.server.protocollo;

public abstract class AbstractProtocollo implements ProtocolloGenerico {

	private ProtocolloContext context;

	@Override
	public void inizialize(ProtocolloContext context) {
		this.context = context;
	}

	@Override
	public ProtocolloContext getContext() {
		return context;
	}

}
