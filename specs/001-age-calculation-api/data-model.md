# Data Model: Age Calculation API

## Age Calculation Input

The endpoint receives one `birthYear` path value.

| Field | Type | Rules |
|---|---|---|
| `birthYear` | integer | Required, exactly four digits, not greater than the current calendar year |

No request entity or DTO is introduced; the controller binds the path value directly to an integer.

## Age Calculation Result

The service computes:

`current calendar year - birthYear`

The controller returns the service result as a plain string:

`Now your age is: {calculatedAge}`

No result entity or response DTO is introduced.

## Validation Outcomes

- Missing or empty path value: unsuccessful request with `400 Bad Request`.
- Non-integer path value: Spring MVC conversion failure with `400 Bad Request`.
- Integer outside the four-digit range: unsuccessful request with `400 Bad Request`.
- Future year: unsuccessful request with `400 Bad Request`.
- Valid year: successful plain-string response.
