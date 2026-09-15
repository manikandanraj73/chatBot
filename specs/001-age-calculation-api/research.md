# Research: Age Calculation API

## Decision: Reuse the existing `ChatBotController`

**Rationale**: The repository already uses `ChatBotController` as the HTTP boundary under the `controller` package. Adding the age mapping there follows the requested pattern and avoids a second controller for a small endpoint.

**Alternatives considered**: A separate `AgeController` would add unnecessary structure and would not match the request to use the existing controller.

## Decision: Add a dedicated `AgeCalculationService`

**Rationale**: Controllers handle HTTP concerns while services handle application logic. Year validation and calculation are deterministic business logic and belong in a focused service.

**Alternatives considered**: Performing calculation directly in the controller would weaken the service boundary and make unit testing less focused.

## Decision: Return a plain `String` and add no DTO

**Rationale**: The requested response is `Now your age is: result`, and the user explicitly does not want an extra DTO. Spring MVC can return a `String` directly.

**Alternatives considered**: A response DTO or map would add an unnecessary API shape outside the requested contract.

## Decision: Use `GET /age/{birthYear}`

**Rationale**: This is the clarified public contract. A path parameter makes the single required year explicit and lets Spring MVC reject non-integer path values at the HTTP boundary.

**Alternatives considered**: Query parameters and POST JSON were superseded during clarification.

## Decision: Return `400 Bad Request` for invalid input

**Rationale**: Missing, non-integer, malformed, and future years are client input errors. The endpoint must not encode validation failures as successful responses.

**Alternatives considered**: `500 Internal Server Error` misclassifies client input, while a successful string error violates the API contract.
