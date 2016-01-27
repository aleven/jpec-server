package it.attocchi.jpec.server.api.rest.data;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceMessaggioResponse {

	private long id;
	private String protocollo;
	private String stato;
	private Date data;

	
}
