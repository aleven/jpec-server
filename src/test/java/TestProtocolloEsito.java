import it.attocchi.jpec.server.protocollo.AzioneEsito;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestProtocolloEsito {

	protected final Logger logger = LoggerFactory.getLogger(TestProtocolloEsito.class);

	@Test
	public void test() throws Exception {

		logger.info(this.getClass().getName());

		try {
			AzioneEsito e = AzioneEsito.errore("a", null);
			
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
