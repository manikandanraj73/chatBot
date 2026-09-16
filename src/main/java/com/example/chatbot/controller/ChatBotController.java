package com.example.chatbot.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.dto.HistoryDTO.HistoryItem;
import com.example.chatbot.dto.HistoryDTO.HistoryResponseItem;
import com.example.chatbot.service.AgeCalculationService;
import com.example.chatbot.service.ChatBotService;
import com.example.chatbot.service.ChatHistoryService;

@RestController
@RequestMapping
public class ChatBotController {
	private final AgeCalculationService ageCalculationService;
	private final ChatBotService chatBotService;
	private final ChatHistoryService chatHistoryService;

	@Autowired
	public ChatBotController(
			AgeCalculationService ageCalculationService,
			ChatBotService chatBotService,
			ChatHistoryService chatHistoryService) {
		this.ageCalculationService = ageCalculationService;
		this.chatBotService = chatBotService;
		this.chatHistoryService = chatHistoryService;
	}

	@PostMapping("/chatbot/request")
	public String chat(@RequestBody ChatRequest chatRequest) throws Exception {
		ChatHistoryService.RequestMetadata requestMetadata = chatHistoryService.captureRequest();
		System.out.println("You : " + chatRequest.getMessage());
		String response = chatBotService.chat(chatRequest.getMessage());
		chatHistoryService.recordSuccessfulExchange(
				chatRequest.getMessage(), response, requestMetadata);
		System.out.println("AI : " + response);
		return response;
	}

	@GetMapping("/chatbot/history")
	public ResponseEntity<?> history() {
		List<HistoryItem> items = chatHistoryService.getHistory();
		if (items.isEmpty()) {
			return ResponseEntity.ok("No items here !");
		}
		return ResponseEntity.ok(items.stream().map(HistoryResponseItem::new).toList());
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
