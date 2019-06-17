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

public abstract class AbstractTestDb {

	protected final Logger logger = LoggerFactory.getLogger(AbstractTestDb.class);

	EntityManagerFactory emf = null;
	
	@Test
	public void doTest() throws Exception {
		beforeTest();
		customTest();
		afterTest();
	}
	
	public void beforeTest() throws Exception {
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
	
	abstract void customTest();
	
	public void afterTest() throws Exception {
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
