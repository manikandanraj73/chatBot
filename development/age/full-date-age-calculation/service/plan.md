# Implementation Plan: Full Date Age Calculation API

**Branch**: `002-full-date-age-calculation` | **Date**: 2026-09-16 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/development/age/full-date-age-calculation/spec.md`

## Summary

Replace the existing year-only `GET /age/{birthYear}` contract with a full-date `GET /age/{birthDate}` contract. Reuse `ChatBotController`, update `AgeCalculationService` to parse and validate `YYYY-MM-DD`, calculate calendar age in years/months/days using the current date from an injectable clock, and keep plain-string responses. Continue using the dedicated `exception` package for all `400 Bad Request` mappings; no DTO is introduced.

## Technical Context

- **Language/Version**: Java 21
- **Primary Dependencies**: Spring Boot 4.1.0, `spring-boot-starter-webmvc`, JUnit 5/Spring web MVC test support
- **Storage**: N/A
- **Testing**: Maven test lifecycle with JUnit 5, service unit tests, and Spring MVC controller tests
- **Target Platform**: Spring Boot HTTP service on Java 21
- **Project Type**: Web service
- **Performance Goals**: At least 95% of valid requests return a result within 1 second under normal service conditions
- **Constraints**: One `YYYY-MM-DD` path value, strict calendar validation, plain-string response, no DTO, no persistence, and existing chatbot behavior preserved
- **Scale/Scope**: One updated stateless endpoint and one updated calculation service; no batch requests or external integrations

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Clear Service Boundaries**: PASS — `ChatBotController` handles routing and delegates parsing/calculation to `AgeCalculationService`; exceptions remain in `exception`.
- **Contract-First HTTP API**: PASS — method, route, date format, response text, replacement impact, and `400` behavior are explicit.
- **Testable Changes**: PASS — a fixed `Clock` makes calendar calculations deterministic; service and MVC tests cover the contract.
- **Secure Configuration and Data Handling**: PASS — no secrets, persistence, or external calls are introduced.
- **Observable and Resilient Integrations**: PASS — no external integration changes; invalid client input remains distinct from successful output.
- **Technology and Quality Gates**: PASS — Java 21, Spring Boot, Maven, and the existing package structure remain in use.

## Phase 0: Research

The feature specification and requirement document define strict parsing, calendar-period arithmetic, month-end normalization, exception placement, and the intentional replacement of the year-only route.

## Phase 1: Design

The date input, age result, validation rules, normalization behavior, replacement route, and error responses are defined in the feature specification and requirement document.

## Implementation Approach

1. Update `AgeCalculationService` to accept a date string, parse strict `YYYY-MM-DD`, reject impossible/future dates, and calculate a calendar-period age using an injectable `Clock`.
2. Define a focused date validation exception in the existing `exception` package and extend the existing exception handler to map it to `400 Bad Request`.
3. Update `ChatBotController` to bind the path as a string and delegate to the date-based service; update the missing-input mapping to refer to birth dates.
4. Remove the old integer year-only behavior from the age route. A four-digit path value must be rejected as an invalid date rather than calculated.
5. Update service and controller tests for exact date examples, leap years, month ends, malformed/impossible/future input, and the unchanged chatbot route.
6. Update feature documentation to describe the intentional replacement of the earlier year-only contract.

## Compatibility Impact

This is an intentional breaking change for clients of `GET /age/{birthYear}`. Existing four-digit year requests are no longer valid. The endpoint path remains `/age/{value}`, but the value contract changes from an integer year to a strict full date.

## Post-Design Constitution Check

- **Clear Service Boundaries**: PASS — controller, service, and exception responsibilities remain separated.
- **Contract-First HTTP API**: PASS — the replacement contract and migration impact are documented.
- **Testable Changes**: PASS — deterministic service and web-layer test coverage is defined.
- **Secure Configuration and Data Handling**: PASS — no sensitive data is introduced.
- **Observable and Resilient Integrations**: PASS — no provider integration is changed.

## Project Structure

### Documentation (this feature)

```text
development/age/full-date-age-calculation/
├── requirement.md
├── spec.md
└── service/
    ├── plan.md
    └── tasks.md
```

### Source Code

```text
src/
├── main/java/com/example/chatbot/
│   ├── controller/
│   │   └── ChatBotController.java
│   ├── exception/
│   │   ├── AgeCalculationExceptionHandler.java
│   │   └── BirthDateValidationException.java
│   └── service/
│       └── AgeCalculationService.java
└── test/java/com/example/chatbot/
    ├── controller/
    │   └── ChatBotControllerTest.java
    └── service/
        └── AgeCalculationServiceTest.java
```

**Structure Decision**: Extend the existing Spring Boot application in place. Reuse `ChatBotController`, update the existing age service, add only the date-specific exception type, keep exception handling in the existing `exception` package, and continue returning a plain `String` without a DTO.

## Complexity Tracking

| Violation | Why Needed | Simpler Alternative Rejected Because |
|---|---|---|
| None | N/A | N/A |
