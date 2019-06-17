import java.io.Console;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import it.attocchi.jpec.server.api.rest.ServiceAzioni;
import it.attocchi.jpec.server.bl.MessaggioPecBL;
import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.jpec.server.exceptions.PecException;
import it.attocchi.jpec.server.pec.DaticertDestinatario;
import it.attocchi.jpec.server.pec.DaticertInfo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestAzioni {

	protected final Logger logger = LoggerFactory.getLogger(TestAzioni.class);
	
	@Test
	public void testAzioneRiceviPassword() throws Exception {
		String mailbox = "mirco.attocchi@pec.it";
		String password = "test";
		
		
		EntityManagerFactory emf = null;
		try {
			emf = Persistence.createEntityManagerFactory("jpec-server-pu_TEST");

			List<PecException> erroriMessaggiImportati = MessaggioPecBL.importaNuoviMessaggi(emf, "REST.ANONYMOUS");
		} catch (Exception ex) {
			logger.error("TestDaticertDb", ex);
		} finally {
			if (emf != null)
				emf.close();
		}
		
	}

	
}
