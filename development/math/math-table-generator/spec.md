# Math Table Generator API

Short name: math-table-generator

Status: Draft

## Summary

Provide a small HTTP API that accepts a single number from the caller and returns a mathematical table for that number. The table is defined as the multiplication table for the provided number with rows from 1 up to a fixed limit of 10. The API must validate input, return clear, user-friendly error messages for invalid or missing input, and never produce more than 10 rows.

## Problem statement

Clients need a simple, deterministic service that generates a multiplication table for a supplied integer. The service should be safe (avoid unbounded resource use), produce machine-friendly JSON, and provide helpful error responses when callers send bad input.

## User scenarios

1. Happy path
   - User posts a valid integer (e.g., 5) to the API and receives a JSON response containing the 10-row multiplication table for 5.

2. Missing input
   - User sends an empty request body or omits the number field. The API responds with HTTP 400 and a descriptive error message indicating that a number is required.

3. Invalid input type
   - User sends non-numeric input (e.g., "five" or a JSON object without the number). The API responds with HTTP 400 and a message indicating that the number must be an integer.

4. Out-of-range input
   - User sends a numeric value outside reasonable bounds (e.g., magnitude greater than 1,000,000). The API responds with HTTP 422 and a message that the number is out of allowed range to prevent resource exhaustion.

5. Server error
   - On unexpected failures (serialization error, runtime exception), the API responds with HTTP 500 and a generic message: "Internal server error. Please try again later." without revealing internals or secrets.

## Functional requirements (testable)

1. Endpoint
   - POST /api/table
   - Content-Type: application/json
   - Request body: { "number": <integer> }

2. Input validation
   - If the request body is missing or null, return 400 Bad Request with JSON error { "error": "number is required" }.
   - If the number field is missing, return 400 Bad Request with JSON error { "error": "number is required" }.
   - If the number is not an integer, return 400 Bad Request with JSON error { "error": "number must be an integer" }.
   - If the absolute value of number > 1_000_000 (assumption to limit abuse), return 422 Unprocessable Entity with JSON error { "error": "number out of allowed range (abs(number) <= 1000000)" }.

3. Table generation
   - The API MUST return exactly 10 rows for any valid input.
   - Each row is an object with fields: { "multiplier": <int>, "value": <int> } where multiplier ranges from 1 to 10 and value = number * multiplier.

4. Response format
   - 200 OK with Content-Type: application/json
   - Response body:
     {
       "input": <number>,
       "rows": [ { "multiplier": 1, "value": <number> }, ..., { "multiplier": 10, "value": <number*10> } ]
     }

5. Error responses
   - Use JSON error objects with an "error" string and optional "details" field for additional context.
   - Avoid exposing stack traces or internal exception messages.

## Non-functional requirements

- Performance: For typical inputs (single integer), 95% of requests must complete within 500ms in normal operating conditions.
- Security: Do not log request bodies containing user-provided values in production logs. Validate and sanitize inputs.
- Maintainability: Business logic (validation, table generation) MUST be implemented in the Service layer; controller only handles transport mapping and validation annotations.

## API examples

Request (happy path):

POST /api/table
Content-Type: application/json

{ "number": 5 }

Response 200 OK

{
  "input": 5,
  "rows": [
    { "multiplier": 1, "value": 5 },
    { "multiplier": 2, "value": 10 },
    ...
    { "multiplier": 10, "value": 50 }
  ]
}

Error examples:

Request with empty body -> 400 Bad Request
{ "error": "number is required" }

Request with non-integer -> 400 Bad Request
{ "error": "number must be an integer" }

Request with out-of-range number -> 422 Unprocessable Entity
{ "error": "number out of allowed range (abs(number) <= 1000000)" }

## Key entities

- number (integer): the input provided by the caller
- rows (array): list of row objects with multiplier and value

## Assumptions

- Input is provided as JSON with the field name "number". If you prefer plaintext or form-encoded input, this can be changed; current choice is JSON for clarity and consistency with existing APIs.
- The maximum absolute allowed number is 1,000,000 to prevent accidental or malicious extremely large multiplications. This is configurable and can be relaxed if justified.
- The table is a simple multiplication table (number * 1..10). If an alternate table type (addition, powers) is intended, this must be clarified.

## Success criteria

- Given a valid integer input, the API returns the correct multiplication table containing 10 rows as defined above.
- For invalid or missing input, the API returns an appropriate 4xx error with a clear, actionable message.
- Service-layer unit tests cover validation and table generation logic with at least 90% code coverage for the service class responsible for generating the table.
- Integration tests exercise the controller endpoints for success and for each error condition.

## Acceptance tests (high-level)

1. Unit: TableService.generate(number) returns list of 10 rows with correct values for inputs 0, 1, -3, 12345.
2. Unit: TableService.validate(number) rejects null, non-integers, and out-of-range values.
3. Integration: POST /api/table with {"number":5} returns 200 and expected body.
4. Integration: POST /api/table with empty body returns 400 and error message.
5. Integration: POST /api/table with {"number":"five"} returns 400.
6. Integration: POST /api/table with {"number":1000001} returns 422.

## Implementation notes (non-normative)

- Implement a DTO `TableRequest` with a single Integer `number` field and appropriate validation annotations (e.g., `@NotNull`). Keep DTOs free of business logic.
- Implement `TableService` with a `generateTable(int number)` method returning a list of row DTOs. This is the authoritative business logic location per the project constitution.
- Controller `TableController` should be thin: map request -> call service -> return response. Use `@RestController` and `@RequestBody` with `@Valid`.
- Use a global `@ControllerAdvice` to map validation exceptions and other exceptions to the JSON error shape.


## SPEC_FILE

development/math/math-table-generator/spec.md
