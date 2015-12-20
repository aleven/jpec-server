package it.attocchi.jpec.server.protocollo;

public abstract class AbstractProtocollo implements ProtocolloGenerico {

	private ProtocolloContext context;

	@Override
	public ProtocolloGenerico inizialize(ProtocolloContext context) {
		this.context = context;
		return this;
	}

	@Override
	public ProtocolloContext getContext() {
		return context;
	}

}
