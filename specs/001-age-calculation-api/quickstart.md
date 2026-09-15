# Quickstart: Age Calculation API

## Prerequisites

- Java 21
- Maven available through the project’s standard Maven wrapper or local Maven installation

## Run the tests

From the repository root:

```powershell
mvn test
```

The focused tests should cover valid calculation, non-four-digit values, future years, and non-integer path values.

## Run and call the endpoint

Start the application using the project’s normal Spring Boot command, then call:

```powershell
curl.exe http://localhost:8080/age/2000
```

For calendar year 2026, the expected body is:

```text
Now your age is: 26
```

An invalid request should produce a client error:

```powershell
curl.exe -i http://localhost:8080/age/abc
```

Expected status:

```text
HTTP/1.1 400
```
