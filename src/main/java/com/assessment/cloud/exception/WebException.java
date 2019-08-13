package com.assessment.cloud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;

public class WebException extends RuntimeException {

	private static final long serialVersionUID = 689611849956948233L;

	public HttpStatus code;

	BodyBuilder resp;

	public WebException(HttpStatus code) {
		this(code, null);
	}

	public WebException(HttpStatus code, String msg) {
		super(msg == null ? null : msg);
		this.code = code;
		resp = ResponseEntity.status(code);
	}

	public WebException header(String name, String... values) {
		resp.header(name, values);
		return this;
	}

	public WebException header(String name, int value) {
		resp.header(name, "" + value);
		return this;
	}

	public BodyBuilder builder() {
		return resp;
	}

}
