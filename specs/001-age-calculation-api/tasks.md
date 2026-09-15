---

description: "Executable task list for the Age Calculation API"
---

# Tasks: Age Calculation API

**Input**: Design documents from `/specs/001-age-calculation-api/`

**Prerequisites**: [plan.md](./plan.md), [spec.md](./spec.md), [research.md](./research.md), [data-model.md](./data-model.md), [contracts/age-api.md](./contracts/age-api.md)

**Tests**: Included because the project constitution requires automated tests for behavior changes.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the existing Spring Boot structure and test entry points before implementation.

- [X] T001 Confirm the existing controller, service, and test package layout in `src/main/java/com/example/chatbot/` and `src/test/java/com/example/chatbot/` and preserve the current Maven/Spring Boot setup
- [X] T002 [P] Confirm the feature contract and validation rules in `specs/001-age-calculation-api/contracts/age-api.md` and `specs/001-age-calculation-api/data-model.md` before writing implementation code

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish the shared test and error-handling approach required by both user stories.

- [X] T003 Define the controller test setup for `GET /age/{birthYear}` in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`, including the existing `ChatBotController` and a mocked `AgeCalculationService`
- [X] T004 Define the service test setup for deterministic current-year calculations in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`, using a fixed current year or injectable time source so tests do not depend on the wall clock
- [X] T005 Decide and document the Spring MVC `400 Bad Request` handling path for non-integer path conversion and service validation failures in `src/main/java/com/example/chatbot/controller/ChatBotController.java` or a narrowly scoped exception handler in the existing application package

**Checkpoint**: Shared test fixtures and the invalid-input response strategy are ready; user-story implementation can begin.

---

## Phase 3: User Story 1 - Calculate Age from Birth Year (Priority: P1) 🎯 MVP

**Goal**: A valid four-digit birth year returns the current calendar year minus that year as the exact plain-string response.

**Independent Test**: With the current year fixed at 2026, call `GET /age/2000` and verify `200 OK` with body `Now your age is: 26`.

### Tests for User Story 1

- [X] T006 [P] [US1] Add service tests for valid four-digit years and the calculation rule `current calendar year - birthYear` in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T007 [P] [US1] Add an MVC contract test for `GET /age/2000` returning `200 OK` and plain-text body `Now your age is: 26` in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`

### Implementation for User Story 1

- [X] T008 [US1] Implement `AgeCalculationService` in `src/main/java/com/example/chatbot/service/AgeCalculationService.java` with a focused method that calculates and returns `Now your age is: {age}` for a valid birth year
- [X] T009 [US1] Extend `ChatBotController` in `src/main/java/com/example/chatbot/controller/ChatBotController.java` with `@GetMapping("/age/{birthYear}")`, inject `AgeCalculationService`, and return the service result directly as a `String` without adding a DTO

**Checkpoint**: User Story 1 is independently functional and the valid request path is testable.

---

## Phase 4: User Story 2 - Explain Invalid Input (Priority: P1)

**Goal**: Empty, non-integer, non-four-digit, negative, and future birth years receive clear `400 Bad Request` validation responses.

**Independent Test**: Call the endpoint with `abc`, `999`, a future year, and a missing path value; verify each request is unsuccessful and contains a clear validation message.

### Tests for User Story 2

- [X] T010 [P] [US2] Add service tests for rejecting values outside the four-digit range and for rejecting a birth year greater than the current calendar year in `src/test/java/com/example/chatbot/service/AgeCalculationServiceTest.java`
- [X] T011 [P] [US2] Add MVC tests for `GET /age/abc`, `GET /age/999`, and a future-year request returning `400 Bad Request` with clear error content in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`
- [X] T012 [P] [US2] Add an MVC test for the missing-year route `/age` or `/age/` in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java`, verifying the explicitly supported missing-input mapping returns `400 Bad Request` with a required-year message

### Implementation for User Story 2

- [X] T013 [US2] Add explicit validation in `src/main/java/com/example/chatbot/service/AgeCalculationService.java` for the data-model rule “Required, exactly four digits, not greater than the current calendar year” and return or raise a clear client-validation error
- [X] T014 [US2] Complete `400 Bad Request` mapping in `src/main/java/com/example/chatbot/controller/ChatBotController.java` or the selected narrowly scoped application error handler for missing input, service validation failures, and Spring integer-conversion failures
- [X] T015 [US2] Ensure `src/main/java/com/example/chatbot/controller/ChatBotController.java` preserves the existing `/chatbot/request` behavior while exposing the new age route and returning no DTO

**Checkpoint**: Both P1 stories are independently functional: valid ages calculate correctly and invalid input is rejected consistently.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Verify the completed feature against the documented contract and project quality gates.

- [X] T016 [P] Update endpoint examples or feature documentation in `specs/001-age-calculation-api/contracts/age-api.md` and `specs/001-age-calculation-api/quickstart.md` if implementation error wording differs from the documented behavior
- [X] T017 Run the focused service and controller tests, then run `mvn test` from the repository root and resolve any failures without changing unrelated chatbot behavior
- [X] T018 Run the commands in `specs/001-age-calculation-api/quickstart.md` against the running application and verify the `GET /age/2000` success response and representative `400 Bad Request` responses

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001 and T002 can run in parallel.
- **Foundational (Phase 2)**: Depends on Phase 1; T003 and T004 can run in parallel, while T005 establishes the shared error strategy.
- **User Story 1 (Phase 3)**: Depends on Phase 2; T006 and T007 can be written in parallel, then T008 before T009.
- **User Story 2 (Phase 4)**: Depends on the shared foundation and the endpoint/service from User Story 1; T010-T012 can be written in parallel, then T013-T015.
- **Polish (Phase 5)**: Depends on both P1 stories; T016-T018 can run in parallel after implementation is stable.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Phase 2 and is the MVP; it has no dependency on User Story 2.
- **User Story 2 (P1)**: Extends the service and endpoint created for User Story 1 to add complete validation and error behavior.

### Within Each User Story

- Tests are written first and should fail before implementation.
- Service logic is implemented before controller integration.
- Controller contract tests verify the public route and response behavior.
- A story is complete only when its independent test criteria pass.

---

## Parallel Opportunities

### Setup

```text
T001: Confirm existing source/test layout
T002: Confirm contract and data-model rules
```

### User Story 1

```text
T006: Service calculation tests
T007: MVC success contract test
```

### User Story 2

```text
T010: Service validation tests
T011: MVC invalid-value tests
T012: MVC missing-path test
```

### Polish

```text
T016: Documentation alignment
T017: Maven verification
T018: Quickstart HTTP verification
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Setup and Foundational phases.
2. Implement User Story 1 with `AgeCalculationService` and the existing `ChatBotController`.
3. Run the service and controller success tests.
4. Stop and validate `GET /age/2000` independently.

### Incremental Delivery

1. Add User Story 2 validation and `400 Bad Request` handling.
2. Run both story test sets independently.
3. Run the full Maven test suite and quickstart HTTP checks.
4. Update the contract documentation if final error wording requires clarification.

## Notes

- `[P]` tasks use different files or independent concerns and can run in parallel.
- Every task includes a concrete repository path.
- No request or response DTO is planned; the endpoint binds the path integer and returns a `String`.
- Existing chatbot behavior must remain unchanged.

## Phase 6: Convergence

- [X] T019 Add service and MVC regression tests for negative birth years, verifying they are rejected with a clear validation message and `400 Bad Request` per SC-002 and FR-004 (partial)
