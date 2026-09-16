package com.example.chatbot.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.chatbot.exception.BirthDateValidationException;

class AgeCalculationServiceTest {
	private AgeCalculationService service;

	@BeforeEach
	void setUp() {
		Clock fixedClock = Clock.fixed(Instant.parse("2026-09-16T00:00:00Z"), ZoneOffset.UTC);
		service = new AgeCalculationService(fixedClock);
	}

	@Test
	void calculatesExactAgeFromBirthDate() {
		assertEquals("Now your age is: 26 years, 3 months, 1 day",
				service.calculateAge("2000-06-15"));
	}

	@Test
	void retainsZeroValuesAndUsesSingularLabels() {
		assertEquals("Now your age is: 1 year, 0 months, 0 days",
				service.calculateAge("2025-09-16"));
	}

	@Test
	void normalizesMonthEndDates() {
		service = new AgeCalculationService(
				Clock.fixed(Instant.parse("2026-09-30T00:00:00Z"), ZoneOffset.UTC));
		assertEquals("Now your age is: 1 year, 8 months, 0 days",
				service.calculateAge("2025-01-31"));
	}

	@Test
	void acceptsLeapYearBirthDate() {
		service = new AgeCalculationService(
				Clock.fixed(Instant.parse("2026-02-28T00:00:00Z"), ZoneOffset.UTC));
		assertEquals("Now your age is: 2 years, 0 months, 0 days",
				service.calculateAge("2024-02-29"));
	}

	@Test
	void rejectsMalformedOrImpossibleDate() {
		assertThrows(BirthDateValidationException.class,
				() -> service.calculateAge("2024-02-30"));
		assertThrows(BirthDateValidationException.class,
				() -> service.calculateAge("2000"));
		assertThrows(BirthDateValidationException.class,
				() -> service.calculateAge("2000-6-15"));
	}

	@Test
	void rejectsMissingAndFutureDate() {
		assertThrows(BirthDateValidationException.class, () -> service.calculateAge(""));
		assertThrows(BirthDateValidationException.class,
				() -> service.calculateAge("2026-09-17"));
	}
}
