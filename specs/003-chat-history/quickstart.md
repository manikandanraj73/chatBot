# Quickstart: Chat History API

## Prerequisites

- Java 21
- Maven wrapper included in the repository
- Runtime configuration for the existing chatbot service, including `openrouter.api.key`

## Automated validation

Run from the repository root:

```powershell
.\mvnw.cmd test
```

Tests should verify successful recording, failed-request isolation, JSON response shape, UTC timestamps, ordering, empty history, restart clearing, and unchanged chatbot/age behavior.

## Manual verification

1. Start the application using the project's normal Spring Boot command.
2. Send a successful request to `POST /chatbot/request`.
3. Call:

```powershell
curl.exe -i http://localhost:8080/chatbot/history
```

4. Verify the response is a JSON array containing the submitted message, chatbot response, and a UTC `sentAt` value such as `2026-09-16T10:30:00Z`.
5. Cause a chatbot request failure, call the history endpoint again, and verify no failed exchange was added.
6. Restart the application and verify the history endpoint returns:

```text
No items here !
```

## Compatibility checks

- Verify `POST /chatbot/request` retains its existing request and response behavior.
- Verify the full-date age endpoint and invalid-date responses remain unchanged.
