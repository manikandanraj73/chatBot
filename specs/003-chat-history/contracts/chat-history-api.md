# Chat History API Contract

## Retrieve Chat History

```http
GET /chatbot/history
```

### Populated response

- **Status**: `200 OK`
- **Content-Type**: JSON
- **Body**: JSON array ordered oldest-to-newest by `sentAt`

Example:

```json
[
  {
    "message": "Hello",
    "response": "Hi there",
    "sentAt": "2026-09-16T10:30:00Z"
  },
  {
    "message": "How are you?",
    "response": "I am fine.",
    "sentAt": "2026-09-16T10:31:00Z"
  }
]
```

Every object contains `message`, `response`, and `sentAt`. The internal equal-timestamp sequence is not exposed.

### Empty response

- **Status**: `200 OK`
- **Body**:

```text
No items here !
```

## Existing chatbot endpoint

```http
POST /chatbot/request
```

This endpoint remains unchanged. A history item is appended only after it returns a successful chatbot response. Failures do not mutate history.

## Existing age endpoints

The existing `/age` endpoints and their current response and validation behavior remain unchanged.
