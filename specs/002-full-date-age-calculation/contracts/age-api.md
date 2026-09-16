# Full Date Age Calculation API Contract

## Calculate Exact Age

```http
GET /age/{birthDate}
```

### Path Parameter

| Name | Type | Required | Description |
|---|---|---:|---|
| `birthDate` | string | Yes | A strict `YYYY-MM-DD` date no later than the current date |

### Successful Response

- **Status**: `200 OK`
- **Body**: plain string

Example when the current date is `2026-09-16`:

```http
GET /age/2000-06-15
```

```text
Now your age is: 26 years, 3 months, 1 day
```

### Invalid Request Response

- **Status**: `400 Bad Request`
- **Body**: a clear validation message

Invalid examples include:

- `GET /age/2000` — year-only input is no longer accepted
- `GET /age/2000-6-15` — missing zero padding
- `GET /age/2024-02-30` — impossible calendar date
- `GET /age/2026-09-17` when the current date is `2026-09-16` — future date
- `GET /age/` — missing birth date

## Compatibility

This contract intentionally replaces the earlier year-only `GET /age/{birthYear}` behavior. Clients must send a full birth date.
