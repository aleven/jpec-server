package it.attocchi.jpec.server.api.rest.data;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UploadAllegatoResponse {

	private long id;
	// private javax.ws.rs.core.Link link;
	private String link;

	// public void setLink(Link link) {
	// this.link = link;
	// }
	//
	// @XmlElement(name = "link")
	// @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
	// public Link getLink() {
	// return link;
	// }

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
