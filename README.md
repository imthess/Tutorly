# Tutorly

Tutorly is a JavaFX-based tutoring marketplace application designed to connect students and tutors through tutor discovery, booking, payments, notifications, and online classes.

## Features

- Student and tutor accounts
- Authentication and role-based workflows
- Tutor profiles and subject management
- Tutor availability management
- Tutor search and selection
- Booking and booking-status management
- Payment workflow
- Student notifications
- Online class scheduling
- Native in-app live classroom
- Accepted students can join an active class
- Tutor-controlled class start and completion
- Admin functionality
- MySQL database integration

## Student Workflow

1. Create an account and log in.
2. Browse tutors and subjects.
3. View tutor information and availability.
4. Book a tutoring session.
5. Track booking status.
6. Receive notifications about booking, payment, and class events.
7. Join an active online class when the tutor starts it.

## Tutor Workflow

1. Create an account and manage a tutor profile.
2. Select teaching subjects.
3. Configure availability.
4. Review incoming bookings.
5. Accept or manage bookings.
6. Start scheduled online classes.
7. Conduct the class through the native live classroom.
8. Complete the class after the session.

## Native Live Classroom

Tutorly includes a native live-class system based on scheduled class occurrences.

- A tutor selects the exact subject, date, and time.
- Accepted bookings for that occurrence share the same class.
- Students cannot join before the tutor starts the class.
- Only students with an accepted booking can join.
- The live server validates the student's identity.
- A completed class cannot be started again.
- Failed actions are reported to the user instead of terminating the application.

## Design Patterns

- **Singleton** — shared application/database-related services
- **Factory** — controlled creation of related objects
- **Observer** — notification delivery
- **Strategy** — interchangeable payment-related behavior
- **Decorator** — extending tutor profile/service behavior
- **Facade** — simplified access to complex workflows such as live classes
- **Proxy** — controlled access to selected services
- **Adapter** — integration between incompatible interfaces
- **Template Method** — reusable workflow structure

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java |
| Java Version | JDK 21 |
| UI | JavaFX 21 |
| Build Tool | Maven |
| Database | MySQL |
| Architecture | Layered / service-repository architecture |
| Live Classes | Native Java networking-based implementation |

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

## Architecture

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

Supporting components include models, authentication, notifications, payments, live classroom services, design patterns, and navigation/session management.

## Database

The database schema is provided in `database/tutorly.sql` and contains the core entities required by the application, including users, students, tutors, subjects, tutor subjects, availability, bookings, online classes, notifications, and payment-related data.

## Requirements

- JDK 21
- Maven
- MySQL
- JavaFX 21 dependencies configured through Maven

## Running the Project

```bash
git clone https://github.com/imthess/Tutorly.git
cd Tutorly
```

Configure MySQL using `database/tutorly.sql`, then run:

```bash
mvn clean compile
mvn javafx:run
```

## Development

Tutorly emphasizes object-oriented design, separation of concerns, design patterns, database-driven application development, maintainable service/repository layers, event-driven notifications, and native client-server communication for live classes.

## Future Improvements

- Improved live-class media capabilities
- More advanced tutor discovery and filtering
- Expanded communication features
- Improved administrative tools
- Additional automated testing
- Further UI/UX improvements

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE) for details.
