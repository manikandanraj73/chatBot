# Age Calculation API Contract

## Calculate Age

```http
GET /age/{birthYear}
```

### Path Parameter

| Name | Type | Required | Description |
|---|---|---:|---|
| `birthYear` | integer | Yes | A four-digit year no later than the current calendar year |

### Successful Response

- **Status**: `200 OK`
- **Body**: plain string

Example for `GET /age/2000` in calendar year 2026:

```text
Now your age is: 26
```

### Invalid Request Response

- **Status**: `400 Bad Request`
- **Body**: a clear validation error message consistent with Spring MVC error handling

Invalid examples include:

- `GET /age/abc` — not an integer
- `GET /age/999` — not four digits
- `GET /age/2027` when the current year is 2026 — future year
- `GET /age/` — missing path value
