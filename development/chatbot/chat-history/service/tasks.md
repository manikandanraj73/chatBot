---

description: "Executable task list for the Chat History API"
---

# Tasks: Chat History API

**Input**: Design documents from `/development/chatbot/chat-history/`

**Prerequisites**: [plan.md](./plan.md), [spec.md](../spec.md), [requirement.md](../requirement.md)

**Tests**: Included because the project constitution requires automated tests for behavior changes.

## Phase 1: Setup

**Purpose**: Confirm the existing application boundaries and test seams before adding runtime history.

- [X] T001 Confirm the existing `ChatBotController`, `ChatBotService`, `ChatRequest`, age endpoints, and test packages in `src/main/java/com/example/chatbot/` and `src/test/java/com/example/chatbot/`
- [X] T002 [P] Confirm the `GET /chatbot/history` JSON contract and empty-history response in `../spec.md` and `../requirement.md`
- [X] T003 [P] Confirm `HistoryDTO` list ownership, UTC timestamp, sequence ordering, and failure state transitions in `../spec.md` and `../requirement.md`

---

## Phase 2: Foundational

**Purpose**: Establish shared runtime storage and deterministic test seams before endpoint integration.

- [X] T004 Define `HistoryDTO` with a list of history entries in `src/main/java/com/example/chatbot/dto/HistoryDTO.java`, keeping `message`, `response`, and `sentAt` externally serializable while keeping sequence metadata internal
- [X] T005 Define a thread-safe `ChatHistoryService` boundary in `src/main/java/com/example/chatbot/service/ChatHistoryService.java` for recording completed exchanges and returning immutable or copied snapshots
- [X] T006 Add injectable UTC clock and monotonic receipt-sequence seams to `src/main/java/com/example/chatbot/service/ChatHistoryService.java` so request-time and equal-timestamp behavior are deterministic in tests

**Checkpoint**: Runtime list storage, timestamp capture support, and deterministic ordering foundations are ready.

---

## Phase 3: User Story 1 - Save Successful Chat Exchanges (Priority: P1) 🎯 MVP

**Goal**: Record exactly one message-response pair only after a chatbot request succeeds.

**Independent Test**: Mock a successful `ChatBotService` response, submit `POST /chatbot/request`, and verify the original response is returned and exactly one history entry is stored.

### Tests for User Story 1

- [X] T007 [P] [US1] Add `ChatHistoryServiceTest` coverage in `src/test/java/com/example/chatbot/service/ChatHistoryServiceTest.java` for recording one completed exchange with message, response, and UTC `sentAt`
- [X] T008 [P] [US1] Add `ChatBotControllerTest` coverage in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` for successful `POST /chatbot/request` preserving the existing response while recording history
- [X] T009 [P] [US1] Add failure-isolation coverage in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` proving a failed `ChatBotService` call does not add a history item

### Implementation for User Story 1

- [X] T010 [US1] Implement `HistoryDTO` list initialization and history-entry fields in `src/main/java/com/example/chatbot/dto/HistoryDTO.java` without introducing a map or duplicate-message overwrite behavior
- [X] T011 [US1] Implement successful-exchange recording and snapshot ordering in `src/main/java/com/example/chatbot/service/ChatHistoryService.java`, appending only complete message-response pairs
- [X] T012 [US1] Update `ChatBotController` in `src/main/java/com/example/chatbot/controller/ChatBotController.java` to capture request time before `ChatBotService.chat(...)`, record only after success, and leave the existing chatbot response unchanged

**Checkpoint**: Successful chatbot requests preserve their existing behavior and are recorded exactly once; failures do not create entries.

---

## Phase 4: User Story 2 - Retrieve Ordered Chat History (Priority: P1)

**Goal**: Expose completed exchanges through `GET /chatbot/history` as a JSON array ordered oldest-to-newest.

**Independent Test**: Seed multiple history entries with controlled timestamps, call `GET /chatbot/history`, and verify JSON fields and ascending order.

### Tests for User Story 2

- [X] T013 [P] [US2] Add service tests in `src/test/java/com/example/chatbot/service/ChatHistoryServiceTest.java` for oldest-to-newest `sentAt` ordering when chatbot completion order differs from request order
- [X] T014 [P] [US2] Add service tests in `src/test/java/com/example/chatbot/service/ChatHistoryServiceTest.java` for deterministic sequence ordering when entries share the same `sentAt`
- [X] T015 [P] [US2] Add controller contract tests in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` for `GET /chatbot/history` returning a JSON array with `message`, `response`, and `sentAt`, excluding internal sequence metadata
- [X] T016 [P] [US2] Add JSON escaping tests in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` for multiline and special-character message/response content

### Implementation for User Story 2

- [X] T017 [US2] Implement sorted history snapshots in `src/main/java/com/example/chatbot/service/ChatHistoryService.java` using `sentAt` ascending followed by monotonic receipt sequence
- [X] T018 [US2] Add `GET /chatbot/history` to `src/main/java/com/example/chatbot/controller/ChatBotController.java` and return populated history as a JSON array through the existing Spring MVC serialization
- [X] T019 [US2] Ensure `src/main/java/com/example/chatbot/dto/HistoryDTO.java` exposes only `message`, `response`, and UTC ISO-8601 `sentAt` in the public JSON representation

**Checkpoint**: Populated history is available through the specified endpoint, correctly shaped as JSON, and deterministically ordered.

---

## Phase 5: User Story 3 - Handle Empty History and Runtime Reset (Priority: P1)

**Goal**: Provide the exact empty-history response and confirm history is runtime-only.

**Independent Test**: Call `GET /chatbot/history` with no successful exchanges and verify `200 OK` plus `No items here !`; restart/recreate the runtime store and verify history is empty.

### Tests for User Story 3

- [X] T020 [P] [US3] Add controller tests in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` for empty `GET /chatbot/history` returning `200 OK` and exactly `No items here !`
- [X] T021 [P] [US3] Add service tests in `src/test/java/com/example/chatbot/service/ChatHistoryServiceTest.java` proving a new application-scoped history service starts empty and does not retain prior entries
- [X] T022 [P] [US3] Add controller regression tests in `src/test/java/com/example/chatbot/controller/ChatBotControllerTest.java` confirming the existing age endpoints remain unchanged

### Implementation for User Story 3

- [X] T023 [US3] Update `ChatBotController` in `src/main/java/com/example/chatbot/controller/ChatBotController.java` to return `200 OK` with `No items here !` when the history snapshot is empty without changing existing chatbot or age mappings
- [X] T024 [US3] Verify application-scoped wiring in `src/main/java/com/example/chatbot/service/ChatHistoryService.java` creates one runtime history store and clears it naturally on application restart

**Checkpoint**: Empty history, runtime reset, existing chatbot behavior, and existing age-calculation behavior are all verified.

---

## Phase 6: Polish and Cross-Cutting Concerns

**Purpose**: Validate the complete feature and preserve documentation accuracy.

- [X] T025 [P] Review `../spec.md` and `../requirement.md` to ensure implementation details remain consistent with the documented contract
- [X] T026 [P] Review `src/test/java/com/example/chatbot/ChatbotApplicationTests.java` to ensure the Spring context still loads with the new DTO/service and no real external chatbot call is introduced
- [X] T027 Run focused history/controller tests, then run `.\mvnw.cmd test` from the repository root and resolve only feature-related failures
- [X] T028 Run focused history and controller tests covering successful recording, failed-request isolation, JSON retrieval, empty history, and restart clearing
- [X] T029 Review the final diff to confirm no database dependency, map-based history, authentication scope, pagination, deletion, or changes to existing age/chat contracts were introduced

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001-T003 can run in parallel.
- **Foundational (Phase 2)**: Depends on Setup; T004-T006 establish shared DTO, storage, and deterministic timing.
- **User Story 1 (Phase 3)**: Depends on Phase 2; creates the MVP recording path.
- **User Story 2 (Phase 4)**: Depends on User Story 1 because retrieval requires recorded entries.
- **User Story 3 (Phase 5)**: Depends on User Story 2 because it completes endpoint empty-state and compatibility behavior.
- **Polish (Phase 6)**: Depends on all user-story phases.

### Parallel Opportunities

- T001-T003 can run in parallel.
- T007-T009 can run in parallel before T010-T012.
- T013-T016 can run in parallel before T017-T019.
- T020-T022 can run in parallel before T023-T024.
- T025-T026 can run in parallel.

### Implementation Strategy

1. Deliver the MVP with `HistoryDTO`, thread-safe recording, and successful-request integration (US1).
2. Add the populated JSON retrieval contract and deterministic ordering (US2).
3. Add empty-history behavior, runtime reset verification, and compatibility regression coverage (US3).
4. Run full validation and manual checks.
