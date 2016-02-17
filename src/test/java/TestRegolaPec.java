import it.attocchi.jpec.server.bl.RegolaPecBL;
import it.attocchi.jpec.server.bl.RegolaPecEventoEnum;
import it.attocchi.jpec.server.entities.RegolaPec;

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
			boolean regoleImportaConvalidate = RegolaPecBL.applicaRegole(emf, regoleImporta, null, null);

			Assert.assertTrue(regoleImportaConvalidate);
			
			emf.close();
		} catch (Exception ex) {
			logger.error("test", ex);
		} finally {

		}

	}
}
