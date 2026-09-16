# Tutorly Testing Plan

## Purpose

Testing should verify both individual components and complete application workflows.

## Unit Testing

JUnit 5 can be used for isolated tests of:

- Authentication services
- Booking services
- Notification services
- Payment strategies
- Factory behavior
- Decorator behavior
- Facade behavior
- Proxy behavior

Unit tests should avoid depending on a real database whenever the behavior can be tested independently.

## Repository Testing

Repository integration tests can verify:

- CRUD operations
- Booking persistence
- Online-class persistence
- Notification persistence
- Payment persistence
- Transaction/state transitions

These tests should use a controlled test database.

## Live-Class Testing

Important scenarios include:

1. Tutor starts an accepted scheduled class.
2. Student joins after the tutor starts.
3. Student attempts to join before the tutor starts.
4. Student without an accepted booking attempts to join.
5. Tutor completes a class.
6. A completed class is prevented from being started again.
7. Invalid or disconnected clients are handled without crashing the application.

## Recommended Test Layers

```text
Unit Tests
    │
    ▼
Service Tests
    │
    ▼
Repository / Integration Tests
    │
    ▼
End-to-End Workflow Tests
```

## Future Testing Improvements

- Increase JUnit 5 coverage.
- Add Mockito where isolation from dependencies is useful.
- Add automated regression tests for important booking and notification workflows.
- Add CI execution for the test suite.
