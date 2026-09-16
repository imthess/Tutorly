# Tutorly Design Patterns

Tutorly uses design patterns as part of its application architecture.

## Singleton

Used where a single shared instance is appropriate.

**Purpose:** centralize shared state or access to shared application infrastructure.

## Factory

Used to encapsulate object creation.

**Purpose:** keep creation rules separate from client code and make supported object types easier to extend.

## Observer

Used by the notification workflow.

**Purpose:** allow application events to notify registered observers without tightly coupling the event source to every receiver.

## Strategy

Used for interchangeable payment behavior.

**Purpose:** select payment behavior without changing the code that coordinates the payment workflow.

## Decorator

Used to extend tutor-related behavior.

**Purpose:** attach additional behavior or presentation without directly modifying the original component.

## Facade

Used to simplify access to complex workflows such as the live-class subsystem.

**Purpose:** expose a smaller interface to a subsystem with multiple collaborating components.

## Proxy

Used for controlled access to selected operations.

**Purpose:** place access-control or intermediary behavior between the caller and the underlying implementation.

## Adapter

Used where interfaces need to be made compatible.

**Purpose:** allow existing components with different interfaces to cooperate.

## Template Method

Used to structure reusable workflows.

**Purpose:** keep common algorithm steps in one place while allowing specialized steps to vary.

## Pattern Usage Principle

Patterns are used to solve concrete design problems in the application. They should not be introduced solely for the sake of increasing the number of patterns.

Future refactoring should preserve this principle and favor simple code when a pattern does not provide a clear architectural benefit.
