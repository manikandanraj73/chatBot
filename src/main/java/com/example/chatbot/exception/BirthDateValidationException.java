package com.example.chatbot.exception;

public class BirthDateValidationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BirthDateValidationException(String message) {
		super(message);
	}
}
