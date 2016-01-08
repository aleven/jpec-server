package it.attocchi.jpec.server.api.rest.data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.io.FilenameUtils;

@XmlRootElement
public class UploadAllegatoRequest {

	private String fileName;
	private String contentType;
	private long size;

	private long idMessaggio;

	// private byte[] fileBytes;
	// private String fileBase64;
	// private String filePath;
	// private String fileUrl;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(long idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	// public byte[] getFileBytes() {
	// return fileBytes;
	// }
	//
	// public void setFileBytes(byte[] fileBytes) {
	// this.fileBytes = fileBytes;
	// }

	// public String getFileBase64() {
	// return fileBase64;
	// }
	//
	// public void setFileBase64(String fileBase64) {
	// this.fileBase64 = fileBase64;
	// }
	//
	// public String getFilePath() {
	// return filePath;
	// }
	//
	// public void setFilePath(String filePath) {
	// this.filePath = filePath;
	// }
	//
	// public String getFileUrl() {
	// return fileUrl;
	// }
	//
	// public void setFileUrl(String fileUrl) {
	// this.fileUrl = fileUrl;
	// }

	@XmlTransient
	public static synchronized UploadAllegatoRequest fromFile(String file) throws IOException {
		UploadAllegatoRequest allegato = null;
		File f = new File(file);
		if (f.exists()) {
			allegato = new UploadAllegatoRequest();
			allegato.setFileName(FilenameUtils.getName(file));
			allegato.setSize(f.length());
			allegato.setContentType(Files.probeContentType(f.toPath()));
			// allegato.setFileBytes(FileUtils.readFileToByteArray(f));
			// allegato.setFileBase64(new
			// String(Base64.encodeBase64(FileUtils.readFileToByteArray(f))));
		}
		return allegato;
	}

}
