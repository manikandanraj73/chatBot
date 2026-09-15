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

import com.example.chatbot.service.AgeCalculationService;

class ChatBotControllerTest {
	private MockMvc mockMvc;
	private AgeCalculationService ageCalculationService;

	@BeforeEach
	void setUp() {
		ageCalculationService = mock(AgeCalculationService.class);
		mockMvc = MockMvcBuilders
				.standaloneSetup(new ChatBotController(ageCalculationService))
				.build();
	}

	@Test
	void returnsCalculatedAge() throws Exception {
		when(ageCalculationService.calculateAge(2000)).thenReturn("Now your age is: 26");

		mockMvc.perform(get("/age/2000"))
				.andExpect(status().isOk())
				.andExpect(content().string("Now your age is: 26"));
	}

	@Test
	void rejectsNonIntegerYear() throws Exception {
		mockMvc.perform(get("/age/abc"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Birth year must be an integer"));
	}

	@Test
	void rejectsMissingYear() throws Exception {
		mockMvc.perform(get("/age"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Birth year is required"));
	}

	@Test
	void rejectsNonFourDigitYear() throws Exception {
		when(ageCalculationService.calculateAge(999))
				.thenThrow(new IllegalArgumentException(
						"Birth year must be a four-digit year no later than the current year"));

		mockMvc.perform(get("/age/999"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(
						"Birth year must be a four-digit year no later than the current year"));
	}

	@Test
	void rejectsNegativeYear() throws Exception {
		when(ageCalculationService.calculateAge(-1))
				.thenThrow(new IllegalArgumentException(
						"Birth year must be a four-digit year no later than the current year"));

		mockMvc.perform(get("/age/-1"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(
						"Birth year must be a four-digit year no later than the current year"));
	}

	@Test
	void rejectsFutureYear() throws Exception {
		when(ageCalculationService.calculateAge(2027))
				.thenThrow(new IllegalArgumentException(
						"Birth year must be a four-digit year no later than the current year"));

		mockMvc.perform(get("/age/2027"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string(
						"Birth year must be a four-digit year no later than the current year"));
	}
}
