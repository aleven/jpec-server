import it.attocchi.jpec.server.bl.RegolaPecBL;
import it.attocchi.jpec.server.bl.RegolaPecEventoEnum;
import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.protocollo.AzioneEsito;
import it.attocchi.jpec.server.protocollo.AzioneEsito.AzioneEsitoStato;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestRegolaPec {

	protected final Logger logger = LoggerFactory.getLogger(TestRegolaPec.class);

	@Test
	public void test() throws Exception {

		logger.info(this.getClass().getName());

		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpec-server-pu_TEST");

			List<RegolaPec> regoleImporta = RegolaPecBL.regole(emf, RegolaPecEventoEnum.IMPORTA_MESSAGGIO);
			AzioneEsito regoleImportaConvalidate = RegolaPecBL.applicaRegole(emf, regoleImporta, null, null, null);

			Assert.assertTrue(regoleImportaConvalidate.stato == AzioneEsitoStato.OK);
			
			emf.close();
		} catch (Exception ex) {
			logger.error("test", ex);
		} finally {

		}

	}
}
