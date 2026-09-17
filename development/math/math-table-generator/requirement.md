# Requirements: Math Table Generator API

## Objective

Provide a deterministic HTTP API that generates a multiplication table for a supplied integer.

## Functional Requirements

- Expose `POST /api/table` with a JSON request body containing one integer `number`.
- Return `200 OK` with the input and exactly 10 rows for multipliers 1 through 10.
- Calculate each row as `number * multiplier`.
- Reject a missing or null number with `400 Bad Request` and a clear JSON error.
- Reject non-integer input with `400 Bad Request` and a clear JSON error.
- Reject numbers whose absolute value exceeds `1,000,000` with `422 Unprocessable Entity`.
- Return clear JSON error objects and a generic `500` response for unexpected server errors without exposing internals.

## Technical Requirements

- Keep validation and table-generation business logic in `TableService`.
- Keep DTOs free of business logic.
- Limit output to 10 rows to avoid unbounded resource use.
- Do not log user-provided request bodies in production logs.
