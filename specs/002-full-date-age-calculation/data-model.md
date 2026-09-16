# Data Model: Full Date Age Calculation API

## Birth Date Input

The endpoint receives one `birthDate` path value.

| Field | Type | Rules |
|---|---|---|
| `birthDate` | date string | Required, strict `YYYY-MM-DD`, real calendar date, not later than the current date |

No request DTO is introduced; the controller passes the path value to the service as a string.

## Age Result

The service calculates an elapsed calendar period from `birthDate` to the current date:

`completed years + remaining months + remaining days`

The controller returns a plain string:

`Now your age is: {years} year(s), {months} month(s), {days} day(s)`

Zero values remain visible, and singular values use singular labels.

## Month-End Normalization

When an intermediate date would fall on a day unavailable in the target month, use the target month’s last valid day before continuing the calendar-period calculation. This covers dates such as January 31 and February 29.

## Validation Outcomes

- Missing or empty path value: `400 Bad Request`, birth-date-required message.
- Malformed path value: `400 Bad Request`, date-format message.
- Impossible calendar date: `400 Bad Request`, invalid-date message.
- Future date: `400 Bad Request`, future-date message.
- Four-digit year without month/day: `400 Bad Request`, date-format message.
- Valid date: `200 OK` with the plain-string exact age result.
