# Feature Specification: Age Calculation API

**Feature Branch**: `001-age-calculation-api`

**Created**: 2026-09-15

**Status**: Draft

**Input**: User description: "Create an age calculation API that accepts only a 4 digit year value as an integer, throws an error message if input is empty or inappropriate, and returns the user's age based on that input, such as 'Now your age is: result'."

## Clarifications

### Session 2026-09-15

- Q: Which HTTP contract should the age calculation API use for its request and response? → A: The initial POST proposal was superseded; the final contract is `GET /age/{birthYear}`, with successful responses returning the age message and invalid input returning `400 Bad Request`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Calculate Age from Birth Year (Priority: P1)

As an API consumer, I want to submit my four-digit birth year and receive my current age so that I can use the result in an application.

**Why this priority**: This is the core value of the feature and the only successful path required for a useful first release.

**Independent Test**: Submit a valid four-digit birth year and verify that the response contains the whole-number age calculated from the current calendar year.

**Acceptance Scenarios**:

1. **Given** the current calendar year is 2026, **When** a consumer sends `GET /age/2000`, **Then** the API returns a successful response stating `Now your age is: 26`.
2. **Given** a valid four-digit birth year, **When** a consumer submits it, **Then** the returned age equals the current calendar year minus the submitted year.

---

### User Story 2 - Explain Invalid Input (Priority: P1)

As an API consumer, I want a clear error when the year is missing or invalid so that I can correct my request.

**Why this priority**: Rejecting invalid data prevents misleading age results and gives callers an actionable contract.

**Independent Test**: Submit empty, non-integer, non-four-digit, and future-year values and verify that each receives an error response with a clear validation message.

**Acceptance Scenarios**:

1. **Given** no birth year is supplied, **When** the request is submitted, **Then** the API returns an error explaining that the birth year is required.
2. **Given** the supplied value is not an integer, **When** the request is submitted, **Then** the API returns an error explaining that the birth year must be an integer.
3. **Given** the supplied integer is not exactly four digits, **When** the request is submitted, **Then** the API returns an error explaining that the birth year must contain four digits.
4. **Given** the supplied year is later than the current calendar year, **When** the request is submitted, **Then** the API returns an error explaining that a future birth year is not allowed.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The API MUST accept one birth-year input for an age calculation request.
- **FR-001a**: The API MUST expose the age calculation through `GET /age/{birthYear}`, where `{birthYear}` is the supplied four-digit integer.
- **FR-002**: The birth-year input MUST be a numeric integer with exactly four digits.
- **FR-003**: The API MUST reject an empty or missing birth-year input with a clear validation error.
- **FR-004**: The API MUST reject non-integer, negative, non-four-digit, and otherwise inappropriate birth-year values with a clear validation error.
- **FR-005**: The API MUST reject a birth year later than the current calendar year.
- **FR-006**: For valid input, the API MUST calculate age as the current calendar year minus the supplied birth year.
- **FR-007**: A successful response MUST include the calculated whole-number age in the format `Now your age is: result`, replacing `result` with the calculated age.
- **FR-008**: Validation failures MUST be returned as `400 Bad Request` responses with a clear validation message and MUST not be represented as successful age calculations.

### Key Entities

- **Age Calculation Request**: The input containing one four-digit integer birth year.
- **Age Calculation Response**: The successful result containing the calculated whole-number age and its user-facing message.
- **Validation Error**: An unsuccessful result describing why the supplied input cannot be used.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of valid four-digit birth-year requests return the age calculated from the current calendar year.
- **SC-002**: 100% of empty, non-integer, non-four-digit, negative, and future-year requests are rejected with a clear validation message.
- **SC-003**: At least 95% of valid requests return a user-visible age result within 1 second under normal service conditions.
- **SC-004**: The successful response uses the exact user-facing prefix `Now your age is:` in 100% of valid requests.

## Assumptions

- Age is represented as completed calendar years; the birth month and day are not collected.
- The current calendar year is determined when the request is processed.
- A four-digit year is treated as a value from `1000` through the current calendar year.
- Version one supports one birth-year value per request and does not calculate ages for a list of people.
- Authentication, persistence, localization, and historical age calculations are outside the scope of this feature.
