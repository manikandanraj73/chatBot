# Research: Chat History API

## Decision: Keep history in an application-scoped in-memory DTO

**Rationale**: The clarified requirement explicitly selects runtime-only storage. A `HistoryDTO` owning a list keeps the aggregate simple and avoids database configuration or persistence behavior that was not requested.

**Alternatives considered**:

- Database persistence: rejected because restart persistence is out of scope.
- File storage: rejected because it adds durability and filesystem concerns without a requirement.
- User-scoped history: rejected because no identity or authentication scope exists in the current application.

## Decision: Use a list rather than a map inside `HistoryDTO`

**Rationale**: A list directly represents conversation history as an ordered sequence. It allows duplicate messages, preserves receipt order through the internal sequence value, and avoids inventing a map key that could collide or overwrite an exchange.

**Alternatives considered**:

- Map keyed by message: rejected because the same message can be sent more than once.
- Map keyed by timestamp: rejected because multiple requests can share a timestamp.
- Map keyed by generated identifier: rejected because it adds an unnecessary lookup structure when retrieval always returns the complete ordered history.

## Decision: Use a dedicated history service for storage and ordering

**Rationale**: The controller should preserve HTTP responsibilities while the service owns append, snapshot, concurrency, and ordering rules. This follows the project constitution's service-boundary principle.

**Alternatives considered**:

- Store history directly in `ChatBotController`: rejected because it couples transport and state management.
- Reuse `ChatBotService` for storage: rejected because that service owns external chatbot interaction.

## Decision: Record only after successful chatbot completion

**Rationale**: The request timestamp is captured before chatbot processing, but the history item is appended only after `ChatBotService.chat(...)` returns successfully. Exceptions therefore leave history unchanged.

**Alternatives considered**:

- Append a pending item before the external call: rejected because failed requests must not affect history.
- Record after the response is committed: rejected because it adds unnecessary lifecycle coupling.

## Decision: Capture UTC request time and use a monotonic tie-breaker

**Rationale**: `sentAt` represents message sending time and must be UTC ISO-8601. A monotonic sequence assigned at receipt provides deterministic ordering when timestamps are equal.

**Alternatives considered**:

- Response completion time: rejected because slow responses would reorder messages by completion.
- Client-provided timestamp: rejected because clients cannot provide a trusted ordering clock.
- Timestamp-only sorting: rejected because equal timestamps do not guarantee deterministic order.

## Decision: Expose JSON arrays for populated history and plain text for empty history

**Rationale**: The clarified contract requires a JSON array of objects for saved items and the exact empty message `No items here !`. Each populated object contains `message`, `response`, and `sentAt`.

**Alternatives considered**:

- Always return an empty JSON array: rejected because the specified empty message must be returned.
- JSON wrapper object: rejected because the public contract specifies an array.
- Plain-text populated history: rejected by the later JSON clarification.

## Decision: Preserve existing chatbot and age behavior

**Rationale**: The feature is additive. Existing request routing, response behavior, exception behavior, age endpoints, and age validation must remain unchanged.

**Alternatives considered**:

- Refactor existing chatbot or age APIs while adding history: rejected because it expands scope and creates compatibility risk.
