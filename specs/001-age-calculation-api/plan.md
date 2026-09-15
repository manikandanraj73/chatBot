# Implementation Plan: Age Calculation API

**Branch**: `001-age-calculation-api` | **Date**: 2026-09-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-age-calculation-api/spec.md`

## Summary

Add a `GET /age/{birthYear}` endpoint to the existing `ChatBotController`. The endpoint delegates age validation and calculation to a new `AgeCalculationService` and returns the user-facing result directly as a `String`; no additional DTO is needed. Invalid years are rejected at the API boundary with `400 Bad Request`.

## Technical Context

- **Language/Version**: Java 21
- **Primary Dependencies**: Spring Boot 4.1.0, `spring-boot-starter-webmvc`, JUnit 5/Spring web MVC test support
- **Storage**: N/A
- **Testing**: Maven test lifecycle with JUnit 5 and Spring MVC/controller tests
- **Target Platform**: Spring Boot HTTP service on Java 21
- **Project Type**: Web service
- **Performance Goals**: At least 95% of valid requests return a result within 1 second under normal service conditions
- **Constraints**: Accept exactly one four-digit integer birth year in the URL path; return plain text; do not add a DTO or persistence layer
- **Scale/Scope**: One stateless endpoint and one calculation service; no batch requests, authentication, or persistence

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Clear Service Boundaries**: PASS — `ChatBotController` owns HTTP mapping and delegates to `AgeCalculationService`.
- **Contract-First HTTP API**: PASS — route, path parameter, plain-string success body, and `400 Bad Request` behavior are documented.
- **Testable Changes**: PASS — deterministic service logic and controller conversion/error behavior will have focused tests.
- **Secure Configuration and Data Handling**: PASS — no secrets, persistence, or external calls are introduced.
- **Observable and Resilient Integrations**: PASS — the feature has no external integration; invalid client input remains distinguishable from success.
- **Technology and Quality Gates**: PASS — remains on Java 21/Spring Boot/Maven and requires relevant Maven tests.

## Phase 0: Research

Research decisions are recorded in [research.md](./research.md). No unresolved technical unknowns remain: the existing controller pattern and Maven/Spring test conventions are sufficient.

## Phase 1: Design

- [Data model](./data-model.md): defines the path input, calculated age result, and validation outcomes without DTO classes.
- [API contract](./contracts/age-api.md): defines `GET /age/{birthYear}`, plain-string success output, and invalid-input behavior.
- [Validation guide](./quickstart.md): provides runnable Maven and HTTP checks.

## Implementation Approach

1. Add `AgeCalculationService` under the existing `service` package.
2. Keep year validation and current-year calculation in the service.
3. Extend `ChatBotController` with `@GetMapping("/age/{birthYear}")` and return the service’s `String` result directly.
4. Ensure invalid years produce `400 Bad Request`, including missing/empty path values, non-integer conversion failures, non-four-digit values, and future years.
5. Add focused service tests and MVC/controller tests for the public route and response behavior.

## Post-Design Constitution Check

- **Clear Service Boundaries**: PASS — controller and service responsibilities remain separate.
- **Contract-First HTTP API**: PASS — the contract artifact matches the implementation approach and explicitly covers errors.
- **Testable Changes**: PASS — service and web-layer tests are planned without external calls.
- **Secure Configuration and Data Handling**: PASS — no sensitive data is introduced.
- **Observable and Resilient Integrations**: PASS — no provider integration is changed.

## Project Structure

### Documentation (this feature)

```text
specs/001-age-calculation-api/
├── plan.md
├── research.md
├── data-model.md
├── contracts/
│   └── age-api.md
├── quickstart.md
└── tasks.md
```

### Source Code

```text
src/
├── main/java/com/example/chatbot/
│   ├── controller/
│   │   └── ChatBotController.java
│   └── service/
│       └── AgeCalculationService.java
└── test/java/com/example/chatbot/
    ├── controller/
    │   └── ChatBotControllerTest.java
    └── service/
        └── AgeCalculationServiceTest.java
```

**Structure Decision**: Extend the existing single Spring Boot application. Reuse the current controller, add one service in the existing service package, and do not add a request or response DTO because the endpoint accepts a path integer and returns a plain `String`.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|---|---|---|
| None | N/A | N/A |
