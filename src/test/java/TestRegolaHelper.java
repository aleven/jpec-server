import it.attocchi.jpec.server.entities.RegolaPec;
import it.attocchi.jpec.server.regole.RegolaPecHelper;

import java.io.FileInputStream;
import java.io.InputStream;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.junit.Assert;
import org.junit.Test;

public class TestRegolaHelper {

	@Test
	public void testAllegatoEccezioneXml() throws Exception {
		RegolaPec regolaTest = new RegolaPec();
		regolaTest.setNome("TEST");
		InputStream is = new FileInputStream("/home/mirco/Desktop/RicevutaEccezione.eml");
		Message mime = new MimeMessage(null, is);
		RegolaPecHelper helper = new RegolaPecHelper(regolaTest, mime);
		Assert.assertTrue(helper.attachmentNameMatch("Eccezione.xml"));
	}
	
	@Test
	public void testAllegatoPostacertXml() throws Exception {
		RegolaPec regolaTest = new RegolaPec();
		regolaTest.setNome("TEST");
		InputStream is = new FileInputStream("/home/mirco/Desktop/RicevutaEccezione.eml");
		Message mime = new MimeMessage(null, is);
		RegolaPecHelper helper = new RegolaPecHelper(regolaTest, mime);
		Assert.assertTrue(helper.attachmentNameMatch("postacert.eml"));
	}
	
	@Test
	public void testAllegatoSenzaEccezioneXml() throws Exception {
		RegolaPec regolaTest = new RegolaPec();
		regolaTest.setNome("TEST");
		InputStream is = new FileInputStream("/home/mirco/Desktop/RicevutaEccezione.eml");
		Message mime = new MimeMessage(null, is);
		RegolaPecHelper helper = new RegolaPecHelper(regolaTest, mime);
		Assert.assertFalse(helper.attachmentNameMatch("TEEST.xml"));
	}
	
	@Test
	public void testRegolaBustaAnomalia() throws Exception {
		RegolaPec regolaTest = new RegolaPec();
		regolaTest.setNome("TEST");
		InputStream is = new FileInputStream("/home/mirco/Desktop/AnomaliaMessaggio.eml");
		Message mime = new MimeMessage(null, is);
		RegolaPecHelper helper = new RegolaPecHelper(regolaTest, mime);
		Assert.assertTrue(helper.isMessaggioBustaAnomalia());
	}
	
	@Test
	public void testRegolaNonBustaAnomalia() throws Exception {
		RegolaPec regolaTest = new RegolaPec();
		regolaTest.setNome("TEST");
		InputStream is = new FileInputStream("/home/mirco/Desktop/RicevutaEccezione.eml");
		Message mime = new MimeMessage(null, is);
		RegolaPecHelper helper = new RegolaPecHelper(regolaTest, mime);
		Assert.assertFalse(helper.isMessaggioBustaAnomalia());
	}		
}
