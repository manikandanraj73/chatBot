package com.example.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.service.AgeCalculationService;
import com.example.chatbot.service.ChatBotService;
import com.example.chatbot.service.ChatBotServiceV2;
import com.example.chatbot.service.TableService;

@RestController
@RequestMapping
public class ChatBotController {
	@Autowired
	private ChatBotService chatBotService;

	@Autowired
	private ChatBotServiceV2 chatBotServiceV2;

	@Autowired
	private TableService tableService;

	private final AgeCalculationService ageCalculationService;

	@Autowired
	public ChatBotController(AgeCalculationService ageCalculationService) {
		this.ageCalculationService = ageCalculationService;
	}

	@PostMapping("/chatbot/request")
	public String chat(@RequestBody ChatRequest chatRequest) throws Exception {
		System.out.println("You : " + chatRequest.getMessage());
		String response = chatBotService.chat(chatRequest.getMessage());
		System.out.println("AI : " + response);
		return response;
	}

	@PostMapping("/chatbot/v2")
	public String chatRequestV2(@RequestBody ChatRequest chatRequest) throws Exception {
		System.out.println("You (V2) : " + chatRequest.getMessage());
		String response = chatBotServiceV2.chat(chatRequest.getMessage());
		System.out.println("AI (V2) : " + response);
		return response;
	}

	@PostMapping("/chatbot/table")
	public ResponseEntity<String> generateTable(@RequestParam(name = "number", required = false) Integer number) {
		tableService.validateNumber(number);
		String result = tableService.generateTableString(number);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/age/{birthYear}")
	public String calculateAge(@PathVariable int birthYear) {
		return ageCalculationService.calculateAge(birthYear);
	}

	@GetMapping({ "/age", "/age/" })
	public ResponseEntity<String> missingBirthYear() {
		throw new IllegalArgumentException("Birth year is required");
	}
}
