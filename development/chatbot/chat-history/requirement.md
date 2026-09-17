# Requirements: Chat History API

## Objective

Store successfully completed chatbot exchanges in application memory and expose them in sending-time order.

## Functional Requirements

- Save each successfully completed chatbot message and response as one history item.
- Do not save incomplete exchanges when chatbot processing fails.
- Expose history through `GET /chatbot/history`.
- Return populated history as a JSON array.
- Include the original `message`, `response`, and UTC ISO-8601 `sentAt` values.
- Return entries from oldest to newest.
- Use deterministic secondary ordering for entries with equal timestamps.
- Return `200 OK` with exactly `No items here !` when history is empty.
- Preserve existing chatbot request behavior.

## Technical Requirements

- Store history in application memory only; it does not need to survive restarts.
- Use a thread-safe history service with deterministic timestamp and ordering seams for tests.
- Do not add persistence, authentication, deletion, pagination, search, or user identity behavior.
- Preserve the existing Java 21, Spring Boot, Maven, controller, service, DTO, and test conventions.
