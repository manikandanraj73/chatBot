# Implementation Plan: Chat History API

**Branch**: `003-chat-history` | **Date**: 2026-09-16 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/development/chatbot/chat-history/spec.md`

## Summary

Add runtime in-memory chat history without changing existing chatbot or age-calculation behavior. The existing `POST /chatbot/request` flow will capture the request timestamp before chatbot processing, save a completed message-response pair only after a successful response, and expose saved entries through `GET /chatbot/history`. A `HistoryDTO` will contain the in-memory list of history entries; a list is preferred over a map because history is an ordered sequence, duplicate messages are valid, and ordering is based on timestamp plus a receipt sequence. Populated history will be exposed as a JSON array, while empty history returns the exact `No items here !` message.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.0, Spring MVC, Jackson, Lombok, JUnit 5

**Storage**: Thread-safe in-memory list owned by `HistoryDTO`; no database

**Testing**: Maven test lifecycle, JUnit 5, Spring MVC standalone tests, Mockito

**Target Platform**: Spring Boot HTTP service

**Project Type**: Java Spring Boot web service

**Performance Goals**: Retrieve 1,000 in-memory history entries within 1 second under normal conditions

**Constraints**: `GET /chatbot/history`; successful chatbot responses only; UTC ISO-8601 timestamps; oldest-to-newest ordering; deterministic equal-timestamp ordering; no persistence across restart; no new dependency expected

**Scale/Scope**: Application-wide runtime history, no user identity, deletion, pagination, search, or database persistence

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Clear Service Boundaries**: PASS — controller handles HTTP, a history service owns storage/order, DTOs define history data, and the chatbot service remains responsible for external model calls.
- **Contract-First HTTP API**: PASS — the new route, JSON array shape, empty response, timestamp format, and failure behavior are defined.
- **Testable Changes**: PASS — service and MVC tests can use mocked chatbot responses, fixed clocks, and isolated history instances.
- **Secure Configuration and Data Handling**: PASS — no credentials or external integrations are added.
- **Observable and Resilient Integrations**: PASS — history is written only after successful chatbot completion and failures do not mutate stored history.
- **Technology and Quality Gates**: PASS — Java 21, Spring Boot, Maven, and existing package structure remain unchanged.

## Phase 0: Research

The feature specification and requirement document cover in-memory concurrency, `HistoryDTO` list ownership, timestamp capture, deterministic ordering, JSON serialization, empty response handling, and preservation of existing routes.

## Phase 1: Design

The history data, timestamps, ordering, state transitions, route contract, response shape, and empty-history behavior are defined in the feature specification and requirement document.

## Implementation Approach

1. Add `HistoryDTO` under the existing DTO package. It owns a list of completed history entries. A list is used instead of a map because entries must retain chronological ordering, duplicate messages must not overwrite one another, and the internal sequence provides deterministic ordering for equal timestamps. Each entry contains `message`, `response`, `sentAt`, and internal sequence metadata.
2. Add a focused history service with thread-safe append and snapshot operations. Capture `sentAt` in UTC at request receipt and assign a monotonic sequence for equal timestamps.
3. Update `ChatBotController` to capture the timestamp before calling `ChatBotService`, save only after the call returns successfully, and retain the original chatbot response/status behavior.
4. Add `GET /chatbot/history`. For non-empty history, return the stored entries as a JSON array with `message`, `response`, and `sentAt`. For empty history, return `200 OK` with `No items here !`.
5. Update controller/service tests to cover successful recording, failed-request isolation, ordering, equal timestamps, JSON escaping, empty history, and existing age/chat behavior.
6. Run Maven verification and live endpoint checks without introducing database configuration or external dependencies.

## Compatibility Impact

This is an additive change. Existing `POST /chatbot/request`, all age-calculation endpoints, their request/response formats, validation, and failure behavior must remain unchanged. Runtime history is application-scoped and is intentionally cleared on restart.

## Project Structure

### Documentation

```text
development/chatbot/chat-history/
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
│   ├── dto/
│   │   ├── ChatRequest.java
│   │   └── HistoryDTO.java
│   └── service/
│       ├── ChatBotService.java
│       └── ChatHistoryService.java
└── test/java/com/example/chatbot/
    ├── controller/
    │   └── ChatBotControllerTest.java
    └── service/
        └── ChatHistoryServiceTest.java
```

**Structure Decision**: Extend the existing Spring Boot service in place. Reuse `ChatBotController` and `ChatBotService`, add `HistoryDTO` under the existing DTO package, add a focused `ChatHistoryService`, and update existing controller tests plus dedicated history-service tests. No new controller, database, or dependency is planned.

## Complexity Tracking

No constitution violations identified.

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| None | N/A | N/A |
