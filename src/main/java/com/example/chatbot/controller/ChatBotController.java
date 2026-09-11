package com.example.chatbot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.service.ChatBotService;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {
	@Autowired
	private ChatBotService chatBotService;

	@PostMapping("/request")
	public String chat(@RequestBody ChatRequest chatRequest) {
		System.out.println("You : " + chatRequest.getMessage());
		String response = chatBotService.chat(chatRequest.getMessage());
		System.out.println("AI : " + response);
		return response;
	}
}
