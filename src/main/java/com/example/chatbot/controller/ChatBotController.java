package com.example.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.service.ChatBotService;
import com.example.chatbot.service.ChatBotServiceV2;
import com.example.chatbot.service.TableService;



@RestController
@RequestMapping("/chatbot")
public class ChatBotController {
	@Autowired
	private ChatBotService chatBotService;
	
	@Autowired
	private ChatBotServiceV2 chatBotServiceV2;

	@Autowired
	private TableService tableService;

	@PostMapping("/request")
	public String chatRequest(@RequestBody ChatRequest chatRequest) throws Exception {
		System.out.println("You : " + chatRequest.getMessage());
		String response = chatBotService.chat(chatRequest.getMessage());
		System.out.println("AI : " + response);
		return response;
	}
	
	@PostMapping("/v2")
	public String chatRequestV2(@RequestBody ChatRequest chatRequest) throws Exception {
		System.out.println("You (V2) : " + chatRequest.getMessage());
		String response = chatBotServiceV2.chat(chatRequest.getMessage());
		System.out.println("AI (V2) : " + response);
		return response;
	}

	@PostMapping("/table")
	public ResponseEntity<String> generateTable(@RequestParam(name = "number", required = false) Integer number) {
		// Accept number as a request parameter; service will validate and generate the table string
		tableService.validateNumber(number);
		String result = tableService.generateTableString(number);
		return ResponseEntity.ok(result);
	}
}
