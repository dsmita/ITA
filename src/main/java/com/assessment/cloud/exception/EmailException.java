package com.assessment.cloud.exception;

public class EmailException extends Exception {

	private static final long serialVersionUID = -1742907465940760466L;

	public EmailException(String message) {
		super(message);
	}

	public EmailException(String message, Throwable cause) {
		super(message, cause);
	}

}
