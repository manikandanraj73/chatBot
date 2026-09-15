package com.example.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgeCalculationServiceTest {
	private AgeCalculationService service;

	@BeforeEach
	void setUp() {
		Clock fixedClock = Clock.fixed(Instant.parse("2026-06-15T00:00:00Z"), ZoneOffset.UTC);
		service = new AgeCalculationService(fixedClock);
	}

	@Test
	void calculatesAgeFromBirthYear() {
		assertEquals("Now your age is: 26", service.calculateAge(2000));
	}

	@Test
	void rejectsYearOutsideFourDigitRange() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> service.calculateAge(999));

		assertEquals("Birth year must be a four-digit year no later than the current year",
				exception.getMessage());
	}

	@Test
	void rejectsNegativeYear() {
		IllegalArgumentException exception = assertThrows(
				IllegalArgumentException.class,
				() -> service.calculateAge(-1));

		assertEquals("Birth year must be a four-digit year no later than the current year",
				exception.getMessage());
	}

	@Test
	void rejectsFutureYear() {
		assertThrows(IllegalArgumentException.class, () -> service.calculateAge(2027));
	}
}
