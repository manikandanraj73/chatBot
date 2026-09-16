# Research: Full Date Age Calculation API

## Decision: Parse the path value as a strict `YYYY-MM-DD` date

**Rationale**: The clarified contract requires a four-digit year, two-digit month, and two-digit day. Strict parsing rejects malformed separators, missing zero padding, and impossible dates before calculation.

**Alternatives considered**: Parsing an integer year or accepting flexible date formats would preserve old behavior or create ambiguous inputs, both of which conflict with the clarified replacement contract.

## Decision: Use an injectable current-date source

**Rationale**: Age depends on the current date, while tests need stable results around year, month, day, and leap-year boundaries. An injectable clock keeps production behavior current and tests deterministic without changing the public response.

**Alternatives considered**: Calling the system date directly in every calculation would make boundary tests dependent on execution time.

## Decision: Calculate calendar periods, not fixed-length day approximations

**Rationale**: The result is explicitly expressed as completed years, remaining months, and remaining days. Calendar arithmetic preserves month and leap-year semantics.

**Alternatives considered**: Dividing total days by fixed year/month lengths would produce incorrect human calendar ages.

## Decision: Normalize month-end dates

**Rationale**: Birthdays such as January 31 and February 29 must remain meaningful when an intermediate target month has fewer days. The calculation will clamp intermediate dates to the target month’s last valid day before deriving the remaining period.

**Alternatives considered**: Requiring an identical day number in every intermediate month or using total-day approximations would produce surprising or inaccurate calendar results.

## Decision: Keep validation handling in the existing `exception` package

**Rationale**: The current code already separates `BirthYearValidationException` and `AgeCalculationExceptionHandler`. Replacing this with date-specific validation preserves the established error boundary and keeps the controller focused on routing.

**Alternatives considered**: Controller-local handlers would duplicate the prior design and mix HTTP error policy into the controller.

## Decision: Replace, rather than version, the year-only route

**Rationale**: The clarification explicitly selected replacement. The same `/age/{value}` path now has one unambiguous date contract, and a four-digit year alone is rejected.

**Alternatives considered**: Supporting both formats or adding a second date route would preserve compatibility but contradict the clarified scope.
