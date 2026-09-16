# Tutorly

Tutorly is a JavaFX-based tutoring marketplace application that connects students and tutors through tutor discovery, availability, booking, payment, notifications, and online classes.

## Project Overview

Tutorly is designed as a modular Java application with a layered structure separating the user interface, business services, persistence, models, and supporting infrastructure.

The project also demonstrates software engineering design patterns through practical application features such as notifications, payments, tutor profiles, and live classes.

## Core Features

- Student and tutor accounts
- Authentication and role-based workflows
- Tutor profiles
- Subject management
- Tutor availability
- Tutor discovery
- Booking management
- Payment workflow
- Student notifications
- Online class scheduling
- Native in-app live classroom
- Admin functionality
- MySQL database integration

## Student Workflow

1. Register and log in.
2. Browse tutors and subjects.
3. Review tutor information and availability.
4. Submit a booking.
5. Track booking status.
6. Receive relevant notifications.
7. Join an active online class after the tutor starts it.
8. Participate in the live classroom.

## Tutor Workflow

1. Register and log in.
2. Create and manage a tutor profile.
3. Select teaching subjects.
4. Configure availability.
5. Review incoming bookings.
6. Accept or manage bookings.
7. Start a scheduled online class.
8. Complete the class after the session.

## Native Live Classroom

Tutorly contains a native live-class workflow rather than depending entirely on an external meeting application.

The workflow is based on an exact class occurrence:

- The tutor selects the subject, date, and time.
- Accepted bookings for the same occurrence are associated with the class.
- Students cannot join before the tutor starts the class.
- Only students with an accepted booking can join.
- The live server validates the student's identity.
- A completed class cannot be started again.
- Failed operations are reported to the user instead of crashing the application.

## Architecture

```text
                    ┌─────────────────────┐
                    │   JavaFX UI / FXML  │
                    │    Controllers      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │      Services       │
                    │ Business Logic      │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Repositories     │
                    │   JDBC / SQL        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │       MySQL         │
                    └─────────────────────┘

     ┌──────────────┐ ┌──────────────┐ ┌────────────────┐
     │ Design       │ │ Notification │ │ Native Live    │
     │ Patterns     │ │ / Observer   │ │ Class System   │
     └──────────────┘ └──────────────┘ └────────────────┘
```

## Design Patterns

The codebase contains and applies multiple software engineering patterns, including:

- **Singleton**
- **Factory**
- **Observer**
- **Strategy**
- **Decorator**
- **Facade**
- **Proxy**
- **Adapter**
- **Template Method**

Examples include notification propagation through the Observer structure, simplified live-class operations through the Facade structure, and interchangeable payment behavior through Strategy-based components.

## Technology Stack

| Area | Technology |
|---|---|
| Language | Java |
| Java Version | JDK 21 |
| UI | JavaFX 21 |
| Build | Maven |
| Database | MySQL |
| Persistence | JDBC / SQL |
| Live Classes | Native Java networking components |

## Project Structure

```text
Tutorly/
├── assets/
├── database/
│   └── tutorly.sql
├── docs/
├── src/
│   ├── main/
│   │   ├── java/com/tutorly/
│   │   │   ├── controllers/
│   │   │   ├── database/
│   │   │   ├── live/
│   │   │   ├── model/
│   │   │   ├── patterns/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   └── util/
│   │   └── resources/
│   │       ├── css/
│   │       ├── fxml/
│   │       ├── icons/
│   │       └── images/
│   └── test/
├── pom.xml
├── Makefile
├── LICENSE
└── README.md
```

## Database

The database schema is provided in `database/tutorly.sql`.

The schema includes the application's core entities, including users, students, tutors, subjects, tutor-subject relationships, availability, bookings, online classes, notifications, and payment-related data.

## Requirements

- JDK 21
- Maven
- MySQL
- A configured MySQL database using the supplied schema

## Run Locally

Clone the repository:

```bash
git clone https://github.com/imthess/Tutorly.git
cd Tutorly
```

Configure the MySQL database using:

```text
database/tutorly.sql
```

Build the project:

```bash
mvn clean compile
```

Run the application:

```bash
mvn javafx:run
```

## Testing

The project contains a test source structure under:

```text
src/test/java
```

A useful next step for maintainability is expanding automated JUnit 5 tests around services, repositories, pattern implementations, and live-class authorization.

## Documentation

Additional project documentation is maintained under `docs/`, including architecture and development planning material.

## Future Improvements

Planned improvement areas include:

- Broader automated test coverage
- More isolated service-level testing
- Connection pooling for database access
- Stronger repository interfaces
- More detailed architecture/UML documentation
- Further live-class media capabilities
- Expanded tutor discovery and filtering
- Additional administrative functionality
- Continued UI/UX refinement

## License

This project is licensed under the MIT License. See `LICENSE` for details.
