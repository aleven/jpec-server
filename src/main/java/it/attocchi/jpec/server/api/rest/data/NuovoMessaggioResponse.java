package it.attocchi.jpec.server.api.rest.data;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement
public class NuovoMessaggioResponse {

	private long messageId;
	private javax.ws.rs.core.Link link;

	public long getMessageId() {
		return messageId;
	}

	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	@XmlElement(name = "link")
	@XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	public Link getLink() {
		return link;
	}
	
}
