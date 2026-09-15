package com.example.chatbot.service;

import java.time.Clock;
import java.time.Year;

import org.springframework.stereotype.Service;

@Service
public class AgeCalculationService {
	private final Clock clock;

	public AgeCalculationService() {
		this(Clock.systemDefaultZone());
	}

	AgeCalculationService(Clock clock) {
		this.clock = clock;
	}

	public String calculateAge(int birthYear) {
		int currentYear = Year.now(clock).getValue();
		if (birthYear < 1000 || birthYear > currentYear) {
			throw new IllegalArgumentException(
					"Birth year must be a four-digit year no later than the current year");
		}

		return "Now your age is: " + (currentYear - birthYear);
	}
}
