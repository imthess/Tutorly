# Tutorly Roadmap

## Current Foundation

- JavaFX desktop application
- Maven build system
- MySQL database
- Layered controller/service/repository structure
- Student and tutor workflows
- Booking management
- Payment workflow
- Notification system
- Native live-class subsystem
- Multiple design-pattern implementations

## Short-Term Improvements

### Testing

- Expand JUnit 5 unit tests.
- Test service-layer behavior independently of the UI.
- Add repository integration tests.
- Test booking state transitions.
- Test notification behavior.
- Test live-class authorization rules.

### Documentation

- Add UML class diagram.
- Add booking sequence diagram.
- Add live-class sequence diagram.
- Document database relationships.
- Document the role of each implemented design pattern.

### Persistence

- Introduce a connection-pooling solution.
- Define clearer repository interfaces.
- Improve error handling around database failures.

## Medium-Term Improvements

### Live Classroom

- Improve live media capabilities.
- Improve class-session lifecycle handling.
- Expand connection/error recovery.
- Improve classroom UI feedback.

### Search and Discovery

- Improve tutor filtering.
- Add richer subject-based discovery.
- Improve availability presentation.

### Administration

- Expand administrative monitoring.
- Improve management workflows.
- Add additional reporting where required.

## Long-Term Direction

Potential architectural directions include:

- More comprehensive automated testing
- Improved modularity
- API integration if a web/mobile client is introduced
- Further separation of infrastructure concerns
- More advanced communication functionality

This roadmap describes potential development directions and does not imply that unimplemented features are currently available.
