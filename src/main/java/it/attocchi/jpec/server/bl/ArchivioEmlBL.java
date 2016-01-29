package it.attocchi.jpec.server.bl;

import it.attocchi.jpec.server.entities.MessaggioPec;
import it.attocchi.mail.utils.MailConnection;
import it.attocchi.mail.utils.MailUtils;
import it.attocchi.utils.DateUtilsLT;

import java.io.File;

import javax.mail.Message;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class ArchivioEmlBL {

	private static final String EML_NAME_PREFIX = "PEC_";
	
	protected static final Logger logger = Logger.getLogger(ArchivioEmlBL.class.getName());

	/**
	 * 
	 * @param emlStoreFolder
	 * @param emlInStoreFolder
	 * @param server
	 * @param mail
	 * @return Path of Saved File
	 * @throws Exception
	 */
	public static String salvaEmlRicevuto(String emlStoreFolder, String emlInStoreFolder, MailConnection server, Message mail) throws Exception {
		if (emlInStoreFolder == null)
			emlInStoreFolder = "IN";

		String res = null;

		String fullPath = emlStoreFolder;
		fullPath = FilenameUtils.concat(fullPath, DateUtilsLT.Now("yyyy"));
		fullPath = FilenameUtils.concat(fullPath, emlInStoreFolder);

		File storeDir = new File(fullPath);
		if (!storeDir.exists())
			storeDir.mkdirs();

		File emlFile = createEmlFile("", storeDir);

		// if (enableEmlStore) {
		if (storeDir.exists()) {

			// OutputStream os = new FileOutputStream(emlFile);
			// try {
			// mail.writeTo(os);
			// } finally {
			// os.close();
			// }
			// server.saveToEml(mail, emlFile);
			MailUtils.saveToEml(mail, emlFile);

			// saved = true;
			res = emlFile.getPath();
		} else {
			logger.error(emlStoreFolder + " does not exist");
		}
		// }

		return res;
	}

	public static File createEmlFile(String tipo, File storeDir) throws Exception {

		String prefix = StringUtils.isNotBlank(tipo) ? tipo + "_" + EML_NAME_PREFIX : EML_NAME_PREFIX;

		return File.createTempFile(prefix, ".eml", storeDir);
	}

	public static String spostaEml(String prefisso, MessaggioPec messaggio, MessaggioPec messaggioStato) throws Exception {
		String res = null;

		String emlOrigine = messaggioStato.getEmlFile();
		String emlInvio = messaggio.getEmlFile();

		if (StringUtils.isNotBlank(emlOrigine) && StringUtils.isNotBlank(emlInvio)) {

			File srcFile = new File(emlOrigine);
			File invioFile = new File(emlInvio);
			File destFile = new File(invioFile.getParent(), prefisso + "_" + srcFile.getName());

			FileUtils.moveFile(srcFile, destFile);

			res = destFile.getPath();
		}

		return res;
	}

	/**
	 * Prepara un File per il savaltaggio EML della posta Inviata, ma poi
	 * fisicamente vien salvato in spedizione
	 * 
	 * @param emlStoreFolder
	 * @param emlOutStoreFolder
	 * @param protocollo
	 * @return
	 * @throws Exception
	 */
	public static File fileEmlInviato(String emlStoreFolder, String emlOutStoreFolder, String protocollo) throws Exception {
		/* Gestione Cartelle */

		if (emlOutStoreFolder == null) {
			emlOutStoreFolder = "OUT";
		}
		String protocolloFolder = parseProtocollo(protocollo);

		String fullPath = emlStoreFolder;
		fullPath = FilenameUtils.concat(fullPath, DateUtilsLT.Now("yyyy"));
		fullPath = FilenameUtils.concat(fullPath, emlOutStoreFolder);
		fullPath = FilenameUtils.concat(fullPath, protocolloFolder);
		// fullPath = FilenameUtils.concat(fullPath, "Inviato");

		File directory = new File(fullPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		File storeEml = ArchivioEmlBL.createEmlFile("INVIATO", directory);

		return storeEml;
	}

	private static String parseProtocollo(String protocollo) throws Exception {
		String res = "";

		if (protocollo != null) {
			res = protocollo.replace("/", "_").replace("\\", "_").replace("[", "").replace("]", "");
		}

		return res;
	}

}
