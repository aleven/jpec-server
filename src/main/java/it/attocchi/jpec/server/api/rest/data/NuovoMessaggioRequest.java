package it.attocchi.jpec.server.api.rest.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NuovoMessaggioRequest {

	private String mailbox;
	private String protocollo;

	private List<String> destinatari;
	private List<String> destinatariCC;
	private List<String> destinatariCCN;
	private String oggetto;
	private String testoMessaggio;
	private boolean inviaTestoComeHtml;
	private List<String> allegati;

	public String getMailbox() {
		return mailbox;
	}

	public void setMailbox(String mailbox) {
		this.mailbox = mailbox;
	}

	public String getProtocollo() {
		return protocollo;
	}

	public void setProtocollo(String protocollo) {
		this.protocollo = protocollo;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getTestoMessaggio() {
		return testoMessaggio;
	}

	public void setTestoMessaggio(String testoMessaggio) {
		this.testoMessaggio = testoMessaggio;
	}

	public boolean isInviaTestoComeHtml() {
		return inviaTestoComeHtml;
	}

	public void setInviaTestoComeHtml(boolean inviaTestoComeHtml) {
		this.inviaTestoComeHtml = inviaTestoComeHtml;
	}

	public List<String> getAllegati() {
		return allegati;
	}

	public void setAllegati(List<String> allegati) {
		this.allegati = allegati;
	}

	public List<String> getDestinatari() {
		return destinatari;
	}

	public void setDestinatari(List<String> destinatari) {
		this.destinatari = destinatari;
	}

	public List<String> getDestinatariCC() {
		return destinatariCC;
	}

	public void setDestinatariCC(List<String> destinatariCC) {
		this.destinatariCC = destinatariCC;
	}

	public List<String> getDestinatariCCN() {
		return destinatariCCN;
	}

	public void setDestinatariCCN(List<String> destinatariCCN) {
		this.destinatariCCN = destinatariCCN;
	}

	public void addDestinatario(String destinatario) {
		if (destinatari == null)
			destinatari = new ArrayList<String>();
		destinatari.add(destinatario);
	}

	public void addDestinatarioCC(String destinatarioCC) {
		if (destinatariCC == null)
			destinatariCC = new ArrayList<String>();
		destinatariCC.add(destinatarioCC);
	}

	public void addDestinatarioCCN(String destinatarioCCN) {
		if (destinatariCCN == null)
			destinatariCCN = new ArrayList<String>();
		destinatariCCN.add(destinatarioCCN);
	}
}
