# Requirements: Age Calculation API

## Objective

Provide a Spring Boot HTTP endpoint that calculates a user's age from a four-digit birth year.

## Functional Requirements

- Expose `GET /age/{birthYear}`.
- Accept exactly one four-digit integer birth year.
- Calculate age as the current calendar year minus the supplied birth year.
- Return successful responses in the format `Now your age is: <age>`.
- Reject missing, non-integer, negative, non-four-digit, inappropriate, and future-year inputs.
- Return validation failures as `400 Bad Request` responses with clear messages.

## Technical Requirements

- Keep business logic in `AgeCalculationService`.
- Keep HTTP mapping and transport concerns in `ChatBotController`.
- Use the existing Java 21, Spring Boot, Maven, and test conventions.
- Keep the feature stateless with no persistence, authentication, batch processing, or external integration.
