package it.attocchi.jpec.server.bl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

public class Utils {

	public static File saveFile(String nome, String estensione) throws IOException {
		return saveFile(nome, estensione, null);
	}

	/**
	 * tmp_ + nome + _ + . + estensione
	 * 
	 * @param nome
	 * @param estensione
	 * @param directory
	 * @return
	 * @throws IOException
	 */
	public static File saveFile(String nome, String estensione, File directory) throws IOException {
		File tmp = null;

		if (StringUtils.isBlank(nome))
			nome = "tmp";
		else if (nome.length() < 3)
			nome = "tmp_" + nome;

		if (StringUtils.isBlank(estensione))
			estensione = "tmp";

		if (directory != null)
			tmp = File.createTempFile(nome + "_", "." + estensione, directory);
		else
			tmp = File.createTempFile(nome + "_", "." + estensione);

		return tmp;
	}

}
