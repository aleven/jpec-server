package it.attocchi.jpec.server.pec;

public class DaticertDestinatario {
	private String tipo;
	private String destinatario;

	public DaticertDestinatario(String tipo, String destinatario) {
		super();
		this.tipo = tipo;
		this.destinatario = destinatario;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	@Override
	public String toString() {
		return "DaticertDestinatario [tipo=" + tipo + ", destinatario=" + destinatario + "]";
	}

}
