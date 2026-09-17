# Requirements: Full Date Age Calculation API

## Objective

Replace the year-only age calculation behavior with an age calculation based on a complete birth date.

## Functional Requirements

- Expose `GET /age/{birthDate}`.
- Accept dates strictly in `YYYY-MM-DD` format.
- Reject missing, malformed, impossible, and future birth dates with `400 Bad Request`.
- Calculate completed years, remaining months, and remaining days using calendar rules.
- Normalize month-end dates according to the documented calendar behavior.
- Preserve the response prefix `Now your age is:`.
- Include years, months, and days in every successful response, including zero values.
- Use grammatically correct singular and plural unit labels.
- Replace the earlier year-only `/age/{birthYear}` contract; a four-digit year alone is not accepted.

## Technical Requirements

- Keep parsing, validation, and calculation in `AgeCalculationService`.
- Use deterministic date testing with a fixed or injectable clock.
- Preserve the existing Java 21, Spring Boot, Maven, controller, service, exception, and test conventions.
- Do not add persistence, batch processing, or external integrations.
