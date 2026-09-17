# Tasks for Math Table Generator API

This file converts the spec into actionable, dependency-ordered tasks focused on implementing `TableService` and adding robust exception handling across the math-table feature.

---

## 1. Add DTOs (TableRequest, TableRow, TableResponse)
- ID: T1
- Priority: High
- Files to create:
  - `src/main/java/com/example/chatbot/dto/TableRequest.java`
  - `src/main/java/com/example/chatbot/dto/TableRow.java`
  - `src/main/java/com/example/chatbot/dto/TableResponse.java`
- Description: Create simple DTOs for request/response mapping. `TableRequest` holds an `Integer number` annotated with `@NotNull` and an explicit validation message. `TableRow` contains `int multiplier` and `long value`. `TableResponse` contains `int input` and `List<TableRow> rows`.
- Acceptance criteria:
  - Classes compile and include getters/setters (use Lombok `@Data` or explicit methods).
  - `TableRequest.number` is annotated with `@NotNull(message = "number is required")` and appropriate import.
  - No business logic in DTOs.
- Estimate: 1h
- Depends on: none

---

## 2. Create TableService with validation and table generation
- ID: T2
- Priority: High
- Files to create:
  - `src/main/java/com/example/chatbot/service/TableService.java`
- Description: Implement the business logic per the project constitution. `TableService` must expose two public methods:
  - `void validateNumber(Integer number)` — throws `IllegalArgumentException` for invalid input: null, out-of-range (abs(number) > 1_000_000). Use clear messages that map to HTTP statuses (e.g. "number is required" or "number out of allowed range (abs(number) <= 1000000)").
  - `List<TableRow> generateTable(int number)` — returns 10 rows with multipliers 1..10 and `value = (long) number * multiplier`.
- Acceptance criteria:
  - Business logic resides only in service class.
  - Methods are unit-testable and deterministic.
  - Input validation uses service-level rules (not controller-level business logic).
- Estimate: 2h
- Depends on: T1

---

## 3. Add exception handling (global controller advice)
- ID: T3
- Priority: High
- Files to create:
  - `src/main/java/com/example/chatbot/controller/ApiExceptionHandler.java`
- Description: Implement a global `@ControllerAdvice` to translate exceptions into consistent JSON error responses. Map the following:
  - `MethodArgumentNotValidException` -> 400 Bad Request, body `{ "error": "<concise message>" }` (prefer the first field error message).
  - `IllegalArgumentException` -> 400 Bad Request or 422 Unprocessable Entity depending on message (use a simple rule: messages containing "out of allowed range" -> 422; otherwise 400).
  - Generic `Exception` -> 500 Internal Server Error with body `{ "error": "Internal server error. Please try again later." }`.
- Acceptance criteria:
  - All exceptions are converted to JSON error responses and no stack trace or exception internals are returned in responses.
  - Validation errors from `@Valid` on DTOs produce a 400 and the `number is required` message when missing.
- Estimate: 2h
- Depends on: T1, T2

---

## 4. Update controller: add POST /api/table endpoint
- ID: T4
- Priority: Medium
- Files to modify:
  - `src/main/java/com/example/chatbot/controller/ChatBotController.java`
- Description: Add a new controller handler method that accepts `@Valid @RequestBody TableRequest`, delegates to `TableService.validateNumber(...)` and `TableService.generateTable(...)`, and returns `ResponseEntity<TableResponse>` with 200 OK on success.
- Implementation notes:
  - Keep controller thin — no business validation other than `@Valid` for transport-level. All business validation should call service methods.
  - Inject `TableService` via constructor injection (preferred) or `@Autowired` field.
- Acceptance criteria:
  - Endpoint compiles and responds to POST `/api/table`.
  - Happy path returns 200 with correct JSON structure matching spec.
  - Missing body or invalid JSON returns 400 with message from ApiExceptionHandler.
- Estimate: 1.5h
- Depends on: T1, T2, T3

---

## 5. Unit tests for TableService
- ID: T5
- Priority: Medium
- Files to create:
  - `src/test/java/com/example/chatbot/service/TableServiceTest.java`
- Description: Add JUnit tests covering `validateNumber` and `generateTable`:
  - validateNumber: null -> IllegalArgumentException with message "number is required".
  - validateNumber: out-of-range -> IllegalArgumentException with message containing "out of allowed range".
  - generateTable: correctness for inputs 0, 1, -3, 12345.
- Acceptance criteria:
  - Tests pass locally (`mvn -DskipTests=false test`) and cover edge cases.
- Estimate: 2h
- Depends on: T2

---

## 6. Integration tests for controller (optional but recommended)
- ID: T6
- Priority: Low
- Files to create:
  - `src/test/java/com/example/chatbot/controller/TableControllerIntegrationTest.java`
- Description: Use MockMvc or SpringBootTest to exercise the HTTP endpoint for:
  - Happy path with `{ "number": 5 }` -> 200 and expected content.
  - Empty body -> 400 and `{ "error": "number is required" }`.
  - Out-of-range -> 422 and `{ "error": "number out of allowed range (abs(number) <= 1000000)" }`.
- Acceptance criteria:
  - Integration tests run and pass in CI.
- Estimate: 3h
- Depends on: T3, T4

---

## 7. Documentation update and examples
- ID: T7
- Priority: Low
- Files to update:
  - `../spec.md` (link implementation)
  - Optional README snippet or docs: `docs/API.md` or project README
- Description: Keep the feature specification aligned with the endpoint and error behaviors. Reference test coverage and where business logic lives (TableService).
- Acceptance criteria:
  - The feature specification remains aligned with the implemented endpoint and error behaviors.
- Estimate: 0.5h
- Depends on: T4

---

## Implementation order (dependency-resolved)
1. T1 – Add DTOs
2. T2 – Create TableService (depends on DTOs)
3. T3 – Add global exception handler (depends on DTOs and service messages)
4. T4 – Update controller and add endpoint (depends on T1, T2, T3)
5. T5 – Unit tests for TableService (depends on T2)
6. T6 – Integration tests for controller (depends on T3, T4)
7. T7 – Documentation updates (after implementation)

---

## Notes & conventions
- Keep business logic in `service` package only (per constitution). Controllers remain thin.
- Use constructor injection for services in controllers.
- Use Lombok (`@Data`, `@AllArgsConstructor`, `@NoArgsConstructor`) consistently if the project uses Lombok (confirm `pom.xml`). If Lombok is not desired, create explicit getters/setters.
- Error response shape: `{ "error": "..." }` (and optional `details` field in the future).
- Configurable limits: If desired, make `1_000_000` a configurable property in `application.properties` and inject using `@Value` into `TableService`.

---

If you'd like, I can now implement tasks T1–T4 and run `mvn -DskipTests=false test`. Reply with "Proceed" and I will begin editing the codebase (one logical change per file), run the build/tests, and report results. If you prefer only T2 and T3 implemented first, say so and I'll implement those in priority order.