# Feature Specification: Chat History API

**Feature Branch**: `003-chat-history`

**Created**: 2026-09-16

**Status**: Draft

**Input**: User description: "save each chatbot message and its response, provide an API to retrieve that history, return history in sending time order, and if there is no history return 'No items here !'."

## Clarifications

### Session 2026-09-16

- Q: How should chat history be stored? → A: In memory during application runtime; persistence across application restarts is out of scope.
- Q: What response format should the history API use when saved items exist? → A: A JSON array of history objects.
- Q: Which endpoint path should retrieve chat history? → A: `GET /chatbot/history`.
- Q: Which timestamp format should history use? → A: UTC ISO-8601, such as `2026-09-16T10:30:00Z`.
- Q: When should the history timestamp be captured? → A: When the request is received, before chatbot processing, so it represents sending time.

## User Scenarios & Testing

### User Story 1 - Save Chat Exchanges (Priority: P1)

As a chatbot user, I want each successful message and response saved together so I can review the conversation later.

**Independent Test**: Submit a successful chatbot request, retrieve history, and verify the message-response pair appears exactly once.

**Acceptance Scenarios**:

1. Given a valid message, when the chatbot returns a response, then one history item is saved containing both texts and the send time.
2. Given a chatbot request fails before producing a response, when history is retrieved, then no incomplete item is present.
3. Given multiple successful requests, when history is retrieved, then each exchange appears as a separate item.

### User Story 2 - Retrieve Ordered History (Priority: P1)

As a chatbot user, I want an API that returns saved exchanges from oldest to newest.

**Independent Test**: Submit two successful messages at different times, retrieve history, and verify their sending order.

**Acceptance Scenarios**:

1. Given saved exchanges, when the history API is called, then all items are returned in ascending send-time order.
2. Given exchanges with the same timestamp, then a deterministic tie-breaker keeps their order stable.
3. Each returned item contains the original message, chatbot response, and send time.

### User Story 3 - Handle Empty History (Priority: P1)

As a first-time user, I want a clear empty state when no exchanges have been saved.

**Independent Test**: Retrieve history before sending any successful message.

**Acceptance Scenarios**:

1. Given no saved exchanges, when history is requested, then the API returns `200 OK` with exactly `No items here !`.
2. Repeated empty-history requests return the same message.

## Edge Cases

- Blank or missing chatbot messages follow existing validation and do not create history.
- Failed chatbot requests do not create partial history.
- Message and response text are preserved exactly.
- History is application-scoped because user identity and authentication were not requested.
- History is cleared when the application restarts because storage is in memory.

## Requirements

### Functional Requirements

- **FR-001**: The system MUST save each successfully completed chatbot message and its response as one history item.
- **FR-002**: The system MUST NOT save incomplete exchanges when chatbot processing fails.
- **FR-003**: The system MUST expose chat history through `GET /chatbot/history`.
- **FR-004**: The JSON history response MUST include each original message, its response, and its message send time.
- **FR-005**: History MUST be returned in ascending sending-time order, oldest first.
- **FR-006**: Same-time items MUST use a deterministic secondary ordering.
- **FR-007**: Empty history MUST return `200 OK` and exactly `No items here !`.
- **FR-008**: Existing chatbot request behavior MUST remain compatible for clients that do not use history.
- **FR-009**: History MUST be held in application memory and need not survive restarts.
- **FR-010**: Each non-empty history item MUST be returned as a JSON object containing `message`, `response`, and `sentAt` fields.
- **FR-011**: Each history timestamp MUST use UTC ISO-8601 formatting.
- **FR-012**: The send timestamp MUST be captured when the request is received, before chatbot processing begins.
- **FR-013**: Existing age-calculation endpoints and their current validation and response behavior MUST remain unchanged.
- **FR-014**: A failed chatbot request MUST propagate its existing failure behavior and MUST NOT alter previously saved history.

### Key Entities

- **Chat History Item**: A completed exchange containing message, response, send time, and ordering value.
- **Chat Message**: Text submitted to the chatbot.
- **Chat Response**: Text returned by the chatbot.

## Success Criteria

- **SC-001**: Every successful exchange appears exactly once in history immediately after completion.
- **SC-002**: All returned items are ordered oldest to newest by send time.
- **SC-003**: Empty history always returns `200 OK` with `No items here !`.
- **SC-004**: Existing chatbot responses and status behavior remain unchanged.
- **SC-005**: Retrieval of 1,000 in-memory exchanges completes within 1 second under normal conditions.

## Assumptions

- The existing chatbot request endpoint remains the message submission endpoint.
- The history endpoint is application-scoped and does not require a user identifier.
- Durable database storage, authentication, deletion, pagination, and search are out of scope for this version.
- Message and response text are returned as JSON string values, with line breaks encoded according to JSON rules.
- Chat history is an additive feature and does not change the existing chatbot request contract or age-calculation API.
