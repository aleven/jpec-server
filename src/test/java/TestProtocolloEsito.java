import it.attocchi.jpec.server.protocollo.ProtocolloEsito;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestNotificaInvia {

	protected final Logger logger = LoggerFactory.getLogger(TestNotificaInvia.class);

	@Test
	public void test() throws Exception {

		logger.info(this.getClass().getName());

		try {
			ProtocolloEsito e = ProtocolloEsito.errore("a", null);
			
			e.logAndBuffer(logger, "test buffer1 {}", new Date());
			e.logAndBuffer(logger, "test buffer2 {}", new Date());
			e.logAndBuffer(logger, "test buffer3 {}", new Date());
			e.logAndBuffer(logger, "test buffer4 {}", new Date());
			
			System.out.println(e.getBufferedLog());

		} catch (Exception ex) {
			logger.error("test", ex);
		} finally {

		}

	}
}
