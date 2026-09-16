package com.example.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@GetMapping("/age/{birthDate}")
	public String calculateAge(@PathVariable String birthDate) {
		return ageCalculationService.calculateAge(birthDate);
	}

	@GetMapping({ "/age", "/age/" })
	public ResponseEntity<String> missingBirthDate() {
		return ResponseEntity.badRequest().body("Birth date is required");
	}

}
