package it.attocchi.jpec.server.pec;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class DaticertInfo {

	protected final Logger logger = LoggerFactory.getLogger(DaticertInfo.class);

	public List<DaticertDestinatario> leggiRicevutaDaFile(String file) throws Exception {
		logger.debug("leggiRicevutaDaFile= {}", file);
		List<DaticertDestinatario> destinatari = new ArrayList<DaticertDestinatario>();

		DocumentBuilderFactory strumentiDati = DocumentBuilderFactory.newInstance();
		DocumentBuilder manipoloDati = strumentiDati.newDocumentBuilder();
		Document doc = manipoloDati.parse(file);
		destinatari = leggiRicevuta(doc);
		return destinatari;
	}

	public List<DaticertDestinatario> leggiRicevutaDaXml(String xml) throws Exception {
		logger.debug("leggiRicevutaDaXml");
		List<DaticertDestinatario> destinatari = new ArrayList<DaticertDestinatario>();

		if (StringUtils.isNotBlank(xml)) {
			DocumentBuilderFactory strumentiDati = DocumentBuilderFactory.newInstance();
			DocumentBuilder manipoloDati = strumentiDati.newDocumentBuilder();
			Document doc = manipoloDati.parse(new ByteArrayInputStream(xml.getBytes()));
			destinatari = leggiRicevuta(doc);
		}
		return destinatari;
	}

	public List<DaticertDestinatario> leggiRicevuta(Document doc) throws Exception {
		List<DaticertDestinatario> destinatari = new ArrayList<DaticertDestinatario>();

		try {

			doc.getDocumentElement().normalize();

			NodeList destinatariNodeList = doc.getElementsByTagName("destinatari");

			for (int i = 0; i < destinatariNodeList.getLength(); i++) {
				Node destinatariNode = destinatariNodeList.item(i);

				// NodeList intestazioneChildNodeList =
				// intestazioneNode.getChildNodes();
				// for (int k = 0; i < intestazioneChildNodeList.getLength();
				// k++) {
				// Node intestazioneChildNode =
				// intestazioneChildNodeList.item(k);
				// logger.info("{}: type ({}):",
				// intestazioneChildNode.getNodeName(),
				// intestazioneChildNode.getNodeType());
				// }

				if (destinatariNode.getNodeType() == Node.ELEMENT_NODE) {
					Element elemento = (Element) destinatariNode;

					String tipo = elemento.getAttribute("tipo");
					logger.debug("tipo= {}", tipo);

					Node destintariNodeChild = destinatariNode.getFirstChild();
					if (destintariNodeChild.getNodeType() == Node.TEXT_NODE) {
						Text textNode = (Text) destintariNodeChild;
						String destinatario = textNode.getNodeValue();
						logger.debug("destinatario= {}", destinatario);

						destinatari.add(new DaticertDestinatario(tipo, destinatario));
					}

					// String destinatario =
					// elemento.getElementsByTagName("destinatari").item(0).getTextContent();
					// logger.info("destinatario= {}", destinatario);

					// titoli.add();
					// descrizioni.add(elemento.getElementsByTagName("descrizione").item(0).getTextContent());
					// links.add(elemento.getElementsByTagName("link").item(0).getTextContent());
					// prezzo.add(elemento.getElementsByTagName("prezzo").item(0).getTextContent());

				}
			}

		} catch (Exception ex) {
			logger.error("test", ex);
		} finally {

		}

		return destinatari;
	}

}
