import it.attocchi.jpec.server.pec.DaticertInfo;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDaticert {

	protected final Logger logger = LoggerFactory.getLogger(TestDaticert.class);

	@Test
	public void test() throws Exception {

		new DaticertInfo().leggiRicevutaDaFile("/home/mirco/Progetti/ivSource/jpec-server/stuff/daticert/pec_daticert.xml");
		new DaticertInfo().leggiRicevutaDaFile("/home/mirco/Progetti/ivSource/jpec-server/stuff/daticert/accettazione_daticert.xml");
		new DaticertInfo().leggiRicevutaDaFile("/home/mirco/Progetti/ivSource/jpec-server/stuff/daticert/anomalia_daticert.xml");
		new DaticertInfo().leggiRicevutaDaFile("/home/mirco/Progetti/ivSource/jpec-server/stuff/daticert/consegna_daticert.xml");
		new DaticertInfo().leggiRicevutaDaFile("/home/mirco/Progetti/ivSource/jpec-server/stuff/daticert/mancataconsegna_daticert.xml");
	}

	
}
