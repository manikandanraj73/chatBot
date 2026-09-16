# Data Model: Chat History API

## HistoryDTO

Application-scoped runtime aggregate that owns the list of completed chat history entries.

The implementation uses a list rather than a map. Chat history is an ordered sequence, identical messages may occur multiple times, and no natural unique key was requested. A map keyed by message or timestamp could overwrite entries or make chronological ordering indirect.

| Field | Type | Required | Description |
|---|---|---:|---|
| `items` | list of history entries | Yes | In-memory completed exchanges |

The DTO starts with an empty list and is cleared naturally when the application restarts. The public populated response is serialized from the entries list as a JSON array.

## History Entry

One successfully completed chatbot exchange.

| Field | Type | Required | Description |
|---|---|---:|---|
| `message` | string | Yes | Original user message |
| `response` | string | Yes | Chatbot response |
| `sentAt` | UTC instant | Yes | Request-received timestamp serialized as UTC ISO-8601 |
| `sequence` | positive integer | Internal | Monotonic receipt order used only to break equal timestamp ties |

`sequence` is internal ordering metadata and is not exposed in JSON.

## State Transitions

1. Request received: capture UTC timestamp and receipt sequence.
2. Chatbot processing succeeds: create one history entry and append it to `HistoryDTO.items`.
3. Chatbot processing fails: do not append an entry; preserve existing failure behavior.
4. History requested with items: return a snapshot sorted by `sentAt`, then `sequence`.
5. History requested without items: return `200 OK` with `No items here !`.
6. Application restart: in-memory items are discarded.

## Validation and Invariants

- Only completed message-response pairs may be stored.
- `message`, `response`, and `sentAt` are present for every stored entry.
- `sentAt` is captured before chatbot processing.
- Returned entries are oldest-to-newest by `sentAt`.
- Equal `sentAt` values are ordered by `sequence`.
- Snapshot retrieval does not expose a mutable internal list.
- JSON serialization preserves message and response text, including newline and special-character escaping.
