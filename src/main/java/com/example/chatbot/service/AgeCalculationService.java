package com.example.chatbot.service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import org.springframework.stereotype.Service;

import com.example.chatbot.exception.BirthDateValidationException;

@Service
public class AgeCalculationService {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
			.ofPattern("uuuu-MM-dd")
			.withResolverStyle(ResolverStyle.STRICT);

	private final Clock clock;

	public AgeCalculationService() {
		this(Clock.systemDefaultZone());
	}

	AgeCalculationService(Clock clock) {
		this.clock = clock;
	}

	public String calculateAge(String birthDateValue) {
		if (birthDateValue == null || birthDateValue.isBlank()) {
			throw new BirthDateValidationException("Birth date is required");
		}
		if (!birthDateValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
			throw new BirthDateValidationException(
					"Birth date must use the YYYY-MM-DD format and be a valid date");
		}

		LocalDate birthDate;
		try {
			birthDate = LocalDate.parse(birthDateValue, DATE_FORMATTER);
		} catch (DateTimeParseException exception) {
			throw new BirthDateValidationException(
					"Birth date must use the YYYY-MM-DD format and be a valid date");
		}

		LocalDate currentDate = LocalDate.now(clock);
		if (birthDate.isAfter(currentDate)) {
			throw new BirthDateValidationException("Birth date cannot be in the future");
		}

		Period age = Period.between(birthDate, currentDate);
		if (isMonthEnd(birthDate) && isMonthEnd(currentDate) && age.getDays() > 0) {
			age = Period.of(age.getYears(), age.getMonths() + 1, 0).normalized();
		}

		return "Now your age is: " + formatValue(age.getYears(), "year") + ", "
				+ formatValue(age.getMonths(), "month") + ", "
				+ formatValue(age.getDays(), "day");
	}

	private boolean isMonthEnd(LocalDate date) {
		return date.getDayOfMonth() == date.lengthOfMonth();
	}

	private String formatValue(int value, String unit) {
		return value + " " + unit + (value == 1 ? "" : "s");
	}
}
