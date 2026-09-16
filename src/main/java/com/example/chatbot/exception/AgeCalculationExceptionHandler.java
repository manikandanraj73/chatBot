package com.example.chatbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class AgeCalculationExceptionHandler {

	@ExceptionHandler(BirthDateValidationException.class)
	public ResponseEntity<String> handleInvalidBirthDate(BirthDateValidationException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleInvalidPathValue() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Birth date must use the YYYY-MM-DD format");
	}
}
