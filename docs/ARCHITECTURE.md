# Tutorly Architecture

## 1. Architectural Overview

Tutorly follows a layered application structure.

```text
JavaFX Controllers
        │
        ▼
     Services
        │
        ▼
   Repositories
        │
        ▼
      MySQL
```

Models represent application data, while the pattern implementations provide reusable mechanisms for selected cross-cutting and domain-specific behaviors.

## 2. Presentation Layer

The presentation layer contains JavaFX controllers and FXML resources.

Responsibilities include:

- Displaying application screens
- Collecting user input
- Triggering service operations
- Updating the interface with operation results
- Navigating between application views

Controllers should avoid containing database access or large business rules directly.

## 3. Service Layer

The service layer contains application-level business operations.

Examples include:

- Authentication
- Student management
- Tutor management
- Tutor subjects
- Availability
- Bookings
- Payments
- Notifications
- Online classes

The service layer coordinates repositories, models, and patterns to implement application workflows.

## 4. Repository Layer

Repositories isolate persistence operations from the rest of the application.

The project uses JDBC/SQL against MySQL.

This separation allows controllers and services to work with application-level operations without embedding SQL throughout the UI.

## 5. Model Layer

The model package represents core application entities such as:

- User
- Student
- Tutor
- Booking
- BookingDetails
- Availability
- OnlineClass
- Notification

Models carry the data required by the corresponding application workflows.

## 6. Pattern Layer

Tutorly includes several design-pattern implementations.

### Singleton

Provides a controlled shared instance for components where a single instance is appropriate.

### Factory

Encapsulates creation logic for supported object types and keeps creation rules separate from clients.

### Observer

Supports notification propagation. A notification event can be delivered to registered observers and persisted through the notification workflow.

### Strategy

Allows payment-related behavior to be selected without hard-coding one payment algorithm into the calling service.

### Decorator

Allows tutor-related behavior or profile information to be extended without modifying the underlying component directly.

### Facade

Provides a simplified interface over a more complex subsystem. The live-class workflow uses this approach to reduce the complexity visible to callers.

### Proxy

Provides controlled access to selected functionality.

### Adapter

Allows components with incompatible interfaces to work together.

### Template Method

Provides a reusable workflow structure where common steps can be defined while variable behavior is delegated to specialized implementations.

## 7. Native Live-Class Architecture

The live-class subsystem contains:

```text
LiveClassClient
LiveClassServer
LiveClassSession
LiveClassRoom
LiveClassRoomController
LiveMediaClient
LiveMediaServer
```

The class occurrence is associated with an online-class record.

The workflow is:

```text
Accepted Booking
       │
       ▼
Online Class
       │
       ▼
Tutor Starts Class
       │
       ▼
Live Class Server
       │
       ├──────────────► Authorized Student
       │
       ▼
Live Classroom
       │
       ▼
Tutor Completes Class
```

Authorization is tied to the accepted booking and the logged-in user rather than allowing unrestricted access to the running class.

## 8. Notification Architecture

Notifications use an Observer-based workflow.

```text
Application Event
       │
       ▼
NotificationService
       │
       ▼
NotificationManager
       │
       ▼
NotificationObserver
       │
       ▼
NotificationRepository
       │
       ▼
MySQL
```

Class notifications can lead the student directly toward the active live class workflow.

## 9. Design Goals

The architecture emphasizes:

- Separation of concerns
- Low coupling between UI and persistence
- Reusable business services
- Explicit database access
- Pattern-based extensibility
- Maintainable feature boundaries
- Controlled access to live classes

## 10. Potential Architectural Improvements

These are improvement opportunities rather than current requirements:

1. Add stronger repository interfaces.
2. Introduce database connection pooling such as HikariCP.
3. Increase automated unit-test coverage.
4. Add integration tests for database workflows.
5. Add UML class and sequence diagrams.
6. Further isolate live-class networking from UI controllers.
