package com.example.chatbot.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jakarta.servlet.ServletException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.chatbot.exception.AgeCalculationExceptionHandler;
import com.example.chatbot.exception.BirthDateValidationException;
import com.example.chatbot.service.AgeCalculationService;
import com.example.chatbot.service.ChatBotService;
import com.example.chatbot.service.ChatHistoryService;

class ChatBotControllerTest {
	private MockMvc mockMvc;
	private AgeCalculationService ageCalculationService;
	private ChatBotService chatBotService;
	private ChatHistoryService chatHistoryService;

	@BeforeEach
	void setUp() {
		ageCalculationService = mock(AgeCalculationService.class);
		chatBotService = mock(ChatBotService.class);
		chatHistoryService = mock(ChatHistoryService.class);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new ChatBotController(
						ageCalculationService, chatBotService, chatHistoryService))
				.setControllerAdvice(new AgeCalculationExceptionHandler())
				.build();
	}

	@Test
	void returnsChatbotResponseAndRecordsSuccessfulExchange() throws Exception {
		ChatHistoryService.RequestMetadata metadata =
				new ChatHistoryService.RequestMetadata(
						java.time.Instant.parse("2026-09-16T10:30:00Z"), 0);
		when(chatHistoryService.captureRequest()).thenReturn(metadata);
		when(chatBotService.chat("Hello")).thenReturn("Hi there");

		mockMvc.perform(post("/chatbot/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"Hello\"}"))
				.andExpect(status().isOk())
				.andExpect(content().string("Hi there"));

		verify(chatHistoryService).recordSuccessfulExchange("Hello", "Hi there", metadata);
	}

	@Test
	void failedChatbotRequestDoesNotRecordHistory() throws Exception {
		when(chatHistoryService.captureRequest())
				.thenReturn(new ChatHistoryService.RequestMetadata(
						java.time.Instant.parse("2026-09-16T10:30:00Z"), 0));
		when(chatBotService.chat("Hello")).thenThrow(new RuntimeException("provider failure"));

		assertThrows(ServletException.class, () -> mockMvc.perform(post("/chatbot/request")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"message\":\"Hello\"}")));

		verify(chatHistoryService, never())
				.recordSuccessfulExchange(any(), any(), any());
	}

	@Test
	void returnsHistoryAsJsonArray() throws Exception {
		when(chatHistoryService.getHistory()).thenReturn(java.util.List.of(
				new com.example.chatbot.dto.HistoryDTO.HistoryItem(
						"Hello",
						"Hi\nthere",
						java.time.Instant.parse("2026-09-16T10:30:00Z"),
						0)));

		mockMvc.perform(get("/chatbot/history"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(content().json(
						"[{\"message\":\"Hello\",\"response\":\"Hi\\nthere\","
								+ "\"sentAt\":\"2026-09-16T10:30:00Z\"}]"));
	}

	@Test
	void returnsEmptyHistoryMessage() throws Exception {
		when(chatHistoryService.getHistory()).thenReturn(java.util.List.of());

		mockMvc.perform(get("/chatbot/history"))
				.andExpect(status().isOk())
				.andExpect(content().string("No items here !"));
	}

	@Test
	void returnsCalculatedAge() throws Exception {
		when(ageCalculationService.calculateAge("2000-06-15"))
				.thenReturn("Now your age is: 26 years, 3 months, 1 day");

		mockMvc.perform(get("/age/2000-06-15"))
				.andExpect(status().isOk())
				.andExpect(content().string("Now your age is: 26 years, 3 months, 1 day"));
	}

	@Test
	void rejectsMissingDate() throws Exception {
		mockMvc.perform(get("/age"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Birth date is required"));
	}

	@Test
	void rejectsInvalidDates() throws Exception {
		when(ageCalculationService.calculateAge("2000"))
				.thenThrow(new BirthDateValidationException(
						"Birth date must use the YYYY-MM-DD format and be a valid date"));
		when(ageCalculationService.calculateAge("2024-02-30"))
				.thenThrow(new BirthDateValidationException(
						"Birth date must use the YYYY-MM-DD format and be a valid date"));
		when(ageCalculationService.calculateAge("2026-09-17"))
				.thenThrow(new BirthDateValidationException("Birth date cannot be in the future"));

		mockMvc.perform(get("/age/2000")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/age/2024-02-30")).andExpect(status().isBadRequest());
		mockMvc.perform(get("/age/2026-09-17"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Birth date cannot be in the future"));
	}
}
