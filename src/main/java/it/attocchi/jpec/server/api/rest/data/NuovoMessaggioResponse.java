package it.attocchi.jpec.server.api.rest.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NuovoMessaggioResponse {

	private long messageId;
	// private javax.ws.rs.core.Link link;
	private String link;

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

//	public void setLink(Link link) {
//		this.link = link;
//	}
//
//	@XmlElement(name = "link")
//	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
//	public Link getLink() {
//		return link;
//	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String link) {
		this.link = link;
	}
}
