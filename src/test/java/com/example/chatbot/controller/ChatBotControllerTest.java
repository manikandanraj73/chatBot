package com.example.chatbot.controller;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.chatbot.exception.AgeCalculationExceptionHandler;
import com.example.chatbot.exception.BirthDateValidationException;
import com.example.chatbot.service.AgeCalculationService;

class ChatBotControllerTest {
	private MockMvc mockMvc;
	private AgeCalculationService ageCalculationService;

	@BeforeEach
	void setUp() {
		ageCalculationService = mock(AgeCalculationService.class);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new ChatBotController(ageCalculationService))
				.setControllerAdvice(new AgeCalculationExceptionHandler())
				.build();
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

		mockMvc.perform(get("/age/2000"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/age/2024-02-30"))
				.andExpect(status().isBadRequest());
		mockMvc.perform(get("/age/2026-09-17"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Birth date cannot be in the future"));
	}
}
