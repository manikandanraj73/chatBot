package com.example.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.service.AgeCalculationService;
import com.example.chatbot.service.ChatBotService;

@RestController
@RequestMapping
public class ChatBotController {
	@Autowired
	private ChatBotService chatBotService;

	private final AgeCalculationService ageCalculationService;

	@Autowired
	public ChatBotController(AgeCalculationService ageCalculationService) {
		this.ageCalculationService = ageCalculationService;
	}

	@PostMapping("/chatbot/request")
	public String chat(@RequestBody ChatRequest chatRequest) {
		System.out.println("You : " + chatRequest.getMessage());
		String response = chatBotService.chat(chatRequest.getMessage());
		System.out.println("AI : " + response);
		return response;
	}

	@GetMapping("/age/{birthYear}")
	public String calculateAge(@PathVariable int birthYear) {
		return ageCalculationService.calculateAge(birthYear);
	}

	@GetMapping({ "/age", "/age/" })
	public ResponseEntity<String> missingBirthYear() {
		return ResponseEntity.badRequest().body("Birth year is required");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleInvalidBirthYear(IllegalArgumentException exception) {
		return ResponseEntity.badRequest().body(exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<String> handleNonIntegerBirthYear() {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body("Birth year must be an integer");
	}
}
