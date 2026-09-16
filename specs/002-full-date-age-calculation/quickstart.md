# Quickstart: Full Date Age Calculation API

## Prerequisites

- Java 21
- Maven wrapper included in the repository

## Run the tests

From the repository root:

```powershell
.\mvnw.cmd test
```

The tests should cover exact date calculation, leap-year and month-end behavior, malformed/impossible/future dates, missing input, and rejection of year-only input.

## Run and call the endpoint

Start the application using the project’s normal Spring Boot command, then call:

```powershell
curl.exe http://localhost:8080/age/2000-06-15
```

When the current date is `2026-09-16`, the expected body is:

```text
Now your age is: 26 years, 3 months, 1 day
```

Invalid date example:

```powershell
curl.exe -i http://localhost:8080/age/2024-02-30
```

Expected status:

```text
HTTP/1.1 400
```

The old year-only request is intentionally rejected:

```powershell
curl.exe -i http://localhost:8080/age/2000
```
