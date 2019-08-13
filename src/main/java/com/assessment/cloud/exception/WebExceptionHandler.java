package com.assessment.cloud.exception;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.context.config.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class WebExceptionHandler {

	public static class ErrBody {
		public int code;
		public String message;

		public ErrBody(int code, String message) {
			this.code = code;
			this.message = message;
		}
	}

	@ExceptionHandler(WebException.class)
    public ResponseEntity<?> handleTypeMismatchException(HttpServletRequest req, WebException ex) {
		return ex.getMessage()==null ? ex.builder().build() :
			ex.builder().body(new ErrBody(ex.code.value(), ex.getMessage()));
    }
	
	@ExceptionHandler(value = RuntimeException.class)
	public ResponseEntity<WebException> runtimeExceptionHandler(RuntimeException ex, HttpServletRequest request) {
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler(value = ResourceNotFoundException.class)
	public ResponseEntity<Void> resourceNotFoundExceptionHandler(ResourceNotFoundException ex,
			HttpServletRequest request) {
		return ResponseEntity.status(NOT_FOUND).build();
	}
}
