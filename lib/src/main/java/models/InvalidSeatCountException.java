package models;

@SuppressWarnings("serial")
public class InvalidSeatCountException extends RuntimeException {
	public InvalidSeatCountException(final String errorMessage) {
		super(errorMessage);
	}
	public InvalidSeatCountException(final String errorMessage, final Throwable err) {
	    super(errorMessage, err);
	}
}