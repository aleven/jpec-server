import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestNotificaInvia {

	protected final Logger logger = LoggerFactory.getLogger(TestNotificaInvia.class);

	@Test
	public void test() throws Exception {

		logger.info(this.getClass().getName());

		try {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpec-server-pu_TEST");

			// NotificaPecBL.creaNotificaErroreAiResponsabili(emf,
			// transactionController, idUtente, messaggio, mailbox,
			// dettaglioErrore);

			emf.close();

		} catch (Exception ex) {
			logger.error("test", ex);
		} finally {

		}

	}
}
