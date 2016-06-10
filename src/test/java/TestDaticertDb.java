import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.pec.DaticertDestinatario;
import it.attocchi.jpec.server.pec.DaticertInfo;

import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDaticertDb {

	protected final Logger logger = LoggerFactory.getLogger(TestDaticertDb.class);

	@Test
	public void test() throws Exception {
		EntityManagerFactory emf = null;
		try {
			emf = Persistence.createEntityManagerFactory("jpec-server-pu_TEST");

			List<MessaggioPec> messaggi = MessaggioPecBL.getMessaggioPecIn(emf);
			for (MessaggioPec pec : messaggi) {
				List<DaticertDestinatario> res = new DaticertInfo().leggiRicevutaDaXml(pec.getDaticertXml());
				logger.info("{}", res);
			}
		} catch (Exception ex) {
			logger.error("TestDaticertDb", ex);
		} finally {
			if (emf != null)
				emf.close();
		}

	}

}
