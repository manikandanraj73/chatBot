<!--
Sync Impact Report
- Version change: unratified scaffold -> 1.0.0
- Modified principles: none; all five principles established from the scaffold
- Added sections: Technology and Security Constraints; Development Workflow and Quality Gates
- Removed sections: none
- Follow-up TODOs: RATIFICATION_DATE remains TODO because the original adoption date is unknown
-->

# Chatbot Constitution

## Core Principles

### I. Clear Service Boundaries
Application behavior MUST be organized around explicit Spring components with focused
responsibilities: controllers handle HTTP concerns, services handle application and
integration logic, and DTOs define request or response contracts. New external integrations
MUST be isolated behind a service boundary so that transport details do not leak into
controllers or domain-facing code. This keeps the chatbot understandable and makes each
boundary independently testable.

### II. Contract-First HTTP API
Every endpoint MUST define an intentional HTTP method, route, request shape, response shape,
and error behavior. Input MUST be validated at the API boundary, and responses MUST use
appropriate HTTP status codes rather than encoding failures as successful responses. Changes
to an existing contract MUST document compatibility impact and provide a migration path
when clients could be affected.

### III. Testable Changes
Every behavior change MUST include or update automated tests at the narrowest useful level.
Unit tests MUST cover deterministic service logic and validation; integration or web-layer
tests MUST cover controller contracts and external-service boundaries where those contracts
matter. A change is not complete until the relevant Maven test suite passes locally.
Tests MUST avoid real external model calls by using controlled fixtures, mocks, or test
implementations.

### IV. Secure Configuration and Data Handling
Secrets, API keys, and credentials MUST be supplied through protected runtime configuration
or environment-specific secret management and MUST NOT be committed to source, logs, test
fixtures, or error responses. External requests MUST use authenticated, encrypted
connections and explicit timeouts where supported. User messages and model responses MUST
be treated as untrusted data; logging MUST minimize sensitive content and failures MUST NOT
expose credentials or provider internals.

### V. Observable and Resilient Integrations
External model calls MUST have explicit failure handling, bounded resource use, and
diagnosable outcomes. The application MUST distinguish provider failures, invalid upstream
responses, and client input errors in its observable behavior. Operational logging MUST
provide enough context to troubleshoot requests without recording secrets or unnecessary
user content. Integration changes MUST consider timeouts, transient failures, malformed
responses, and graceful degradation before release.

## Technology and Security Constraints

The project MUST remain compatible with the configured Java 21 and Spring Boot stack unless
an approved amendment changes that baseline. Maven is the canonical build and test entry
point. Dependencies MUST be justified by a concrete requirement, kept current within
compatibility constraints, and reviewed for security and license implications. Runtime
configuration MUST be externalized from source-controlled code, and generated build output
MUST NOT be treated as source.

## Development Workflow and Quality Gates

Changes MUST be small enough to review and MUST preserve the principles in this document.
Before integration, authors MUST run the relevant Maven tests and verify that compilation
and static checks pass when configured. Reviewers MUST check API compatibility, test
coverage, secret handling, external-integration failure behavior, and observability.
Changes that alter public behavior MUST include updated documentation or endpoint examples
when existing documentation would otherwise become inaccurate.

## Governance

This constitution is the highest-level project guidance; implementation details, plans, and
local conventions MUST comply with it. Amendments MUST:

1. Describe the reason and scope of the change.
2. Include a Sync Impact Report at the top of the amended constitution for human review.
3. Update the version and last-amended date.
4. Identify compatibility, migration, and documentation effects.
5. Be reviewed for compliance before implementation work relying on the amendment begins.

Versioning follows semantic versioning for governance: MAJOR increments for incompatible
principle removals or redefinitions, MINOR for new principles or materially expanded
requirements, and PATCH for clarifications or non-semantic wording changes. Every feature
plan, implementation review, and release review MUST verify applicable constitutional
requirements. Any justified exception MUST be recorded with its scope, owner, rationale,
and expiry or reassessment condition.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date is unknown | **Last Amended**: 2026-09-10
