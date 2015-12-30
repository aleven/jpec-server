package it.attocchi.jpec.server.exceptions;

public class PecException extends Exception {

	public PecException() {
		super();
	}

	public PecException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PecException(String message, Throwable cause) {
		super(message, cause);
	}

	public PecException(String message) {
		super(message);
	}

	public PecException(Throwable cause) {
		super(cause);
	}

}
