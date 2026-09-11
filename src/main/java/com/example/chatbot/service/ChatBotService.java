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
public class ChatBotService {
	@Value("${openrouter.api.key}")
	private String apiKey;

	RestTemplate restTemplate = new RestTemplate();

	public String chat(String request) {
		String url = "https://openrouter.ai/api/v1/chat/completions";
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization","Bearer "+apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		Map<String, Object> body = Map.of(
			    "model", "openrouter/free",
			    "messages", List.of(
			    		Map.of("role","user",
			    				"content",request))
			);
		
		HttpEntity<?>entity = new HttpEntity<>(body,headers);

		System.out.println("key fetched : " + (apiKey != null));

		ResponseEntity<String> aiResponse = restTemplate.exchange(url, HttpMethod.POST,entity,String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode node= objectMapper.readTree(aiResponse.getBody());
		String answer = node.path("choices").get(0).path("message").path("content").asString();
										
		return answer;
	}
	

}
