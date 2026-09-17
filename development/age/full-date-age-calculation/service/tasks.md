---

description: "Executable task list for the Full Date Age Calculation API"
---

# Tasks: Full Date Age Calculation API

**Input**: Design documents from `/development/age/full-date-age-calculation/`

**Prerequisites**: [plan.md](./plan.md), [spec.md](../spec.md), [requirement.md](../requirement.md)

**Tests**: Included because the project constitution requires automated tests for behavior changes and the feature specifies independently testable user stories.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the existing application structure and replacement contract before modifying the implemented year-only feature.

- [X] T001 Confirm the existing controller, service, exception, and test packages under `src/main/java/com/example/chatbot/` and `src/test/java/com/example/chatbot/` before replacing the year-only age contract
- [X] T002 [P] Confirm the replacement route, strict date format, response examples, and compatibility impact in `../spec.md` and `../requirement.md`
- [X] T003 [P] Confirm the input and calendar-normalization rules in `../spec.md` and `../requirement.md` before writing calculation tests

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish deterministic date testing and the shared exception path required by both user stories.

- [X] T004 Define a fixed-clock test setup in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java` so current date is deterministic for year, month, day, leap-year, and month-end cases
- [X] T005 Define the MVC test setup in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` with the existing `ChatBotController`, mocked `AgeCalculationService`, and `AgeCalculationExceptionHandler`
- [X] T006 Decide and document the date-validation exception flow in `src/main/java/com/example/chatbot/exception/BirthDateValidationException.java` and `src/main/java/com/example/chatbot/exception/AgeCalculationExceptionHandler.java`, mapping all invalid date inputs to `400 Bad Request`

**Checkpoint**: Deterministic date fixtures and dedicated exception handling are ready; user-story implementation can begin.

---

## Phase 3: User Story 1 - Calculate Exact Age from Birth Date (Priority: P1) 🎯 MVP

**Goal**: A valid `YYYY-MM-DD` birth date returns the exact calendar age with years, months, and days in the existing message style.

**Independent Test**: With the current date fixed at `2026-09-16`, call `GET /age/2000-06-15` and verify `200 OK` with body `Now your age is: 26 years, 3 months, 1 day`.

### Tests for User Story 1

- [X] T007 [P] [US1] Add service tests for strict parsing of `2000-06-15`, the calculation result `26 years, 3 months, 1 day`, and zero-value visibility in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T008 [P] [US1] Add service tests for singular/plural output such as `1 year, 0 months, 0 days` in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T009 [P] [US1] Add an MVC contract test for `GET /age/2000-06-15` returning `200 OK` and the exact plain-string response in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`

### Implementation for User Story 1

- [X] T010 [US1] Update `AgeCalculationService` in `src/main/java/com/example/chatbot/service/AgeCalculationService.java` to accept a birth-date string, parse strict `YYYY-MM-DD`, calculate completed years/months/days from an injectable `Clock`, and return `Now your age is: {years} year(s), {months} month(s), {days} day(s)`
- [X] T011 [US1] Update `ChatBotController` in `src/main/java/com/example/chatbot/controller/ChatBotController.java` so `GET /age/{birthDate}` binds the path value as a string and delegates to the updated service without adding a DTO or new controller
- [X] T012 [US1] Preserve the existing `/chatbot/request` behavior while replacing only the age route contract in `src/main/java/com/example/chatbot/controller/ChatBotController.java`

**Checkpoint**: User Story 1 is independently functional for valid full birth dates and produces the required message.

---

## Phase 4: User Story 2 - Reject Invalid Birth Dates (Priority: P1)

**Goal**: Missing, malformed, impossible, future, and year-only values receive clear `400 Bad Request` responses.

**Independent Test**: Call the endpoint with `2000`, `2000-6-15`, `2024-02-30`, a future date, and a missing path value; verify each request is unsuccessful with a clear validation message.

### Tests for User Story 2

- [X] T013 [P] [US2] Add service tests for malformed formats, missing values, year-only input, and impossible dates such as `2024-02-30` in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T014 [P] [US2] Add service tests for future dates and month-end/leap-year normalization, including January 31 and February 29 cases, in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T015 [P] [US2] Add MVC tests for malformed, impossible, future, and year-only paths returning `400 Bad Request` with clear error content in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`
- [X] T016 [P] [US2] Add an MVC test for `/age` or `/age/` returning `400 Bad Request` with a birth-date-required message in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`

### Implementation for User Story 2

- [X] T017 [US2] Add `BirthDateValidationException` in `src/main/java/com/example/chatbot/exception/BirthDateValidationException.java` for malformed, impossible, missing, future, and year-only birth-date input
- [X] T018 [US2] Extend `AgeCalculationExceptionHandler` in `src/main/java/com/example/chatbot/exception/AgeCalculationExceptionHandler.java` to map `BirthDateValidationException` and path conversion errors to `400 Bad Request` messages specific to birth dates
- [X] T019 [US2] Update the missing-input mapping in `src/main/java/com/example/chatbot/controller/ChatBotController.java` to return a clear birth-date-required error while keeping exception handling outside the controller
- [X] T020 [US2] Ensure `src/main/java/com/example/chatbot/service/AgeCalculationService.java` rejects a four-digit year alone and all future dates, and applies the documented month-end normalization rule

**Checkpoint**: Both P1 stories are independently functional: valid dates calculate correctly and every specified invalid input is rejected consistently.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Align documentation, verify compatibility impact, and run all quality gates.

- [X] T021 [P] Review `../spec.md` and `../requirement.md` to ensure final validation wording and normalization behavior remain accurate
- [X] T022 [P] Review `src/test/java/com/example/chatbot/ChatbotApplicationTests.java` and its test-only configuration to ensure the application context remains loadable without exposing secrets
- [X] T023 Run focused service and controller tests, then run `.\mvnw.cmd test` from the repository root and resolve failures without changing unrelated chatbot behavior
- [X] T024 Run focused service and controller tests and verify the valid full-date response, invalid-date `400` responses, and rejection of the old year-only request
- [X] T025 Review the final diff for accidental DTO/controller additions and confirm the compatibility-breaking change is documented in `../spec.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001-T003 can run in parallel.
- **Foundational (Phase 2)**: Depends on Phase 1; T004 and T005 can run in parallel, while T006 establishes the shared exception contract.
- **User Story 1 (Phase 3)**: Depends on Phase 2; T007-T009 can be written in parallel, then T010 before T011-T012.
- **User Story 2 (Phase 4)**: Depends on the service/controller shape from User Story 1; T013-T016 can be written in parallel, then T017-T020 implement the validation behavior.
- **Polish (Phase 5)**: Depends on both P1 stories; T021-T025 can run in parallel after implementation is stable, except T024 requires the application to be runnable.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Phase 2 and is the MVP; it establishes the date-based service and route.
- **User Story 2 (P1)**: Builds on User Story 1’s date input and route but remains independently testable through invalid-input requests.

### Within Each User Story

- Write tests before implementation and verify they fail for the missing behavior.
- Keep date arithmetic in the service and HTTP mapping in the controller/exception package.
- Preserve the plain-string response and avoid introducing DTOs.
- Complete the story’s independent test criteria before moving to the next phase.

---

## Parallel Opportunities

### Setup

```text
T001: Confirm existing source/test structure
T002: Confirm API contract
T003: Confirm data-model rules
```

### User Story 1

```text
T007: Exact calculation service tests
T008: Singular/plural service tests
T009: MVC success contract test
```

### User Story 2

```text
T013: Format/impossible-date service tests
T014: Future/month-end service tests
T015: MVC invalid-date tests
T016: MVC missing-date test
```

### Polish

```text
T021: Documentation alignment
T022: Application-context test review
T023: Maven verification
T025: Final diff review
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup and Foundational phases.
2. Update the service and existing controller for strict full-date input.
3. Add exact-age and response-format tests.
4. Validate `GET /age/2000-06-15` independently.

### Incremental Delivery

1. Add date-specific exceptions and all invalid-input validation.
2. Add leap-year and month-end normalization tests.
3. Run focused tests, the full Maven suite, and live HTTP checks.
4. Confirm the old year-only route is rejected and the breaking contract is documented.

## Notes

- `[P]` tasks can run in parallel when they touch different files or independent concerns.
- Every task includes a concrete repository path.
- No request or response DTO is planned.
- The existing `ChatBotController`, `AgeCalculationService`, and `exception` package are reused.
- The old year-only `/age/{birthYear}` behavior is intentionally replaced.
