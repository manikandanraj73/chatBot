# Feature Specification: Full Date Age Calculation API

**Feature Branch**: `002-full-date-age-calculation`

**Created**: 2026-09-16

**Status**: Draft

**Input**: User description: "Analyse the age calculation API and update it to need a whole date like year/month/date, with the usual response and a simple change like #years, #months, #days."

## Clarifications

### Session 2026-09-16

- Q: Should the new full-date endpoint replace the existing year-only `GET /age/{birthYear}` behavior, or should the API support both date formats during a compatibility period? → A: Replace the year-only behavior; accept only `YYYY-MM-DD` for `/age/{birthDate}`.
- Q: When a birth date falls on a month-end day and the current month has fewer days, how should the API calculate the remaining months and days? → A: Normalize month-end dates and calculate completed years, then months, then days using calendar rules.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Calculate Exact Age from Birth Date (Priority: P1)

As an API consumer, I want to submit my complete birth date and receive my age in years, months, and days so that the result is more accurate than a year-only calculation.

**Why this priority**: Exact date-based age calculation is the core enhancement and replaces the approximate year-only result for callers that need a precise age.

**Independent Test**: Submit a valid birth date and verify that the response includes the completed years, remaining months, and remaining days between that date and the current date.

**Acceptance Scenarios**:

1. **Given** the current date is 2026-09-16, **When** a consumer sends `GET /age/2000-06-15`, **Then** the API returns `Now your age is: 26 years, 3 months, 1 day`.
2. **Given** a birth date exactly one year before the current date, **When** the consumer submits it, **Then** the API returns `Now your age is: 1 year, 0 months, 0 days`.
3. **Given** a birth date with a day or month component, **When** the consumer submits it, **Then** the response reflects the complete date difference rather than only subtracting years.

---

### User Story 2 - Reject Invalid Birth Dates (Priority: P1)

As an API consumer, I want a clear error when the date is missing, malformed, impossible, or in the future so that I can correct my request.

**Why this priority**: Invalid dates can produce misleading ages and must be rejected consistently at the API boundary.

**Independent Test**: Submit missing, malformed, impossible, and future date values and verify that each receives an unsuccessful response with a clear validation message.

**Acceptance Scenarios**:

1. **Given** no birth date is supplied, **When** the request is submitted, **Then** the API returns a `400 Bad Request` explaining that the birth date is required.
2. **Given** the supplied value does not follow `YYYY-MM-DD`, **When** the request is submitted, **Then** the API returns a `400 Bad Request` explaining that the birth date format is invalid.
3. **Given** the supplied date is impossible, such as `2024-02-30`, **When** the request is submitted, **Then** the API returns a `400 Bad Request` explaining that the birth date is invalid.
4. **Given** the supplied birth date is later than the current date, **When** the request is submitted, **Then** the API returns a `400 Bad Request` explaining that a future birth date is not allowed.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The API MUST accept one complete birth date containing year, month, and day.
- **FR-002**: The API MUST expose the calculation through `GET /age/{birthDate}`.
- **FR-002a**: The full-date contract MUST replace the earlier year-only `/age/{birthYear}` behavior; a four-digit year alone MUST NOT be accepted by the enhanced endpoint.
- **FR-003**: The `birthDate` value MUST use the `YYYY-MM-DD` format with a four-digit year, two-digit month, and two-digit day.
- **FR-004**: The API MUST reject a missing or empty birth date with `400 Bad Request` and a clear required-field message.
- **FR-005**: The API MUST reject malformed or impossible calendar dates with `400 Bad Request` and a clear validation message.
- **FR-006**: The API MUST reject a birth date later than the current date.
- **FR-007**: For valid input, the API MUST calculate the elapsed age as completed years followed by remaining months and remaining days.
- **FR-007a**: The calculation MUST normalize month-end dates when the target month has fewer days, preserving calendar-period behavior for dates such as January 31 and February 29.
- **FR-008**: A successful response MUST retain the existing `Now your age is:` prefix and include the result in the form `Now your age is: # years, # months, # days`.
- **FR-009**: Singular values MUST use grammatically correct labels where applicable, such as `1 year`, `1 month`, and `1 day`; zero values MUST remain visible.
- **FR-010**: Validation failures MUST be unsuccessful `400 Bad Request` responses and MUST NOT return a successful age result.

### Key Entities

- **Birth Date**: The user's complete calendar date of birth, including year, month, and day.
- **Age Result**: The elapsed completed years, remaining months, and remaining days from the birth date to the current date.
- **Validation Error**: An unsuccessful result describing a missing, malformed, impossible, or future birth date.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of valid birth-date requests return years, months, and days calculated from the complete date.
- **SC-002**: 100% of missing, malformed, impossible, and future birth-date requests return `400 Bad Request` with a clear validation message.
- **SC-003**: 100% of successful responses retain the `Now your age is:` prefix and show all three units: years, months, and days.
- **SC-004**: At least 95% of valid requests return a user-visible exact age result within 1 second under normal service conditions.

## Assumptions

- The API uses the ISO-style `YYYY-MM-DD` format in the URL path.
- The current date is determined when the request is processed.
- Age is calculated using calendar periods, not an approximate total-day conversion.
- The endpoint continues to accept one birth date per request and does not support batch calculations.
- The response remains a plain user-facing message rather than a structured response object.
- The earlier year-only input is superseded and is not supported by the enhanced `/age/{birthDate}` contract.
- Month-end dates use calendar normalization rather than fixed-length month or total-day approximations.
