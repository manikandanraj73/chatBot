package com.example.chatbot.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class ChatBotServiceV2 {
	@Value("${openrouter.api.key}")
	private String apiKey;
	
	@Value("${openrouter.model:gpt-3.5-turbo}")
	private String model;

	RestTemplate restTemplate = new RestTemplate();

	public String chat(String request) throws Exception {
		String url = "https://openrouter.ai/api/v1/chat/completions";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> body = Map.of(
			"model", model,
			"messages", List.of(
				Map.of("role", "user", "content", request)
			),
			"temperature", 0.7,
			"max_tokens", 100
		);
		
		HttpEntity<?> entity = new HttpEntity<>(body, headers);
		ResponseEntity<String> aiResponse = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node = objectMapper.readTree(aiResponse.getBody());
		String answer = node.path("choices").get(0).path("message").path("content").asText();
		
		return answer;
	}
}
