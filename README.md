# ShareIt — Rental & Booking API (Updated, Multi‑Module)

*This project was developed independently as part of a learning program to practice backend development with Java and Spring Boot.*


A Spring Boot application for item sharing and bookings, organized as **gateway** and **server** modules.
Users publish items, search, request bookings, approve/decline them, and leave comments after completed bookings.

> **Java 21 · Spring Boot 3.x · Spring Data JPA · PostgreSQL · Jakarta Validation · Lombok**


---

## Highlights
- **Gateway/Server split:** gateway handles request validation & routing; server hosts domain logic and persistence.
- **Items:** CRUD, partial updates, search by text, comments after completed bookings.
- **Bookings:** create, approve/decline, fetch by booker or owner with state filters.
- **Users:** basic CRUD.
- **Validation & Errors:** Jakarta Validation, cohesive error responses.
- **DB schema:** normalized PostgreSQL schema with FKs; JPA repositories (derived queries, JPQL).

---

## Tech Stack
- **Language:** Java 21
- **Frameworks:** Spring Boot (Web, Validation), Spring Data JPA
- **Database:** PostgreSQL with Hibernate/JPA. Schema defined in `server/src/main/resources/schema.sql` (`users`, `items`, `bookings`, `comments`, `requests`; FK constraints).
- **Build/Tools:** Maven (multi‑module), Lombok, Checkstyle, JUnit

---

## Full API Endpoints

The REST API is exposed via both modules.

Header **X-Sharer-User-Id**: <userId> is required for Items, Bookings, and Item Requests; not required for Users.

- **Users (`/users`)**
  - `GET /users/{id}` — Get user by ID
  - `POST /users/` — Create user
  - `DELETE /users/{id}` — Delete user
  - `PATCH /users/{id}` — Partially update user
- **Items (`/items`)** (userId in header `X-Sharer-User-Id`)
  - `GET /items/{itemId}` — Get item by ID
  - `GET /items/` — List items owned by the current user
  - `GET /items/search` — Search available items by text
  - `POST /items/` — Create item
  - `POST /items/{itemId}/comment` — Add comment after a completed booking
  - `PATCH /items/{itemId}` — Partially update item (owner only)
- **Bookings (`/bookings`)** (userId in header `X-Sharer-User-Id`)
  - `GET /bookings/{bookingId}` — Get booking by ID (for booker or owner)
  - `GET /bookings/` — List bookings of the current booker (filter by `state`)
  - `GET /bookings/owner` — List bookings of the current owner (filter by `state`)
  - `POST /bookings/` — Create booking request
  - `PATCH /bookings/{bookingId}` — Approve/decline booking (`approved=true|false`, owner only)
- **Item Requests (`/requests`)** (userId in header `X-Sharer-User-Id`)
  - `GET /requests/` — List requester’s own item requests
  - `GET /requests/all` — List other users’ requests
  - `GET /requests/{requestId}` — Get a specific request by ID with answers
  - `POST /requests/` — Create a new item request


---

## Database Schema (PostgreSQL)

- `users(id, name, email)`
- `items(id, owner_id, name, description, available, request_id)`
- `bookings(id, start_date, end_date, item_id, booker_id, status)`
- `comments(id, text, item_id, author_id, created)`
- `requests(id, description, requestor_id, create_date)`

> Foreign keys ensure integrity; schema is initialized from `server/src/main/resources/schema.sql`.

---

## Project Structure
```
java-shareit/
├── gateway/                              # validation & routing layer (API gateway)
│   ├── pom.xml
│   └── src/
│       └── main/
│          ├── java/practicum/
│          │   ├── booking/              # booking requests validation and forwarding
│          │   ├── item/                 # item validation and forwarding
│          │   ├── request/              # item request validation and forwarding
│          │   ├── user/                 # user validation and forwarding
│          │   ├── client/               # BaseClient
│          │   └── ShareItGatewayApp.java # Spring Boot main entry point (gateway)
│          └── resources/
│              └── application.properties # gateway configuration
│
├── server/                               # business logic & persistence (PostgreSQL + JPA)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/ru/practicum/shareit/server/
│       │   │   ├── booking/              # booking domain logic (controller, service, repository, model, dto)
│       │   │   ├── item/                 # item domain logic (controller, service, repository, model, dto)
│       │   │   ├── request/              # item request domain logic (controller, service, repository, model, dto)
│       │   │   ├── user/                 # user domain logic (controller, service, repository, model, dto)
│       │   │   ├── exception/            # centralized error handling and custom exceptions
│       │   │   └── ShareItServerApp.java # Spring Boot main entry point (server)
│       │   └── resources/
│       │       ├── application.properties # configuration
│       │       └── schema.sql            # database DDL (users, items, bookings, comments, requests)
│       └── test/
│           ├── java/ru/practicum/shareit/server/
│           │   ├── booking/              # booking-related tests (controller, service, integration)
│           │   ├── item/                 # item-related tests (controller, service, integration)
│           │   ├── request/              # request-related tests (controller, service, integration)
│           │   ├── user/                 # user-related tests (controller, service, integration)
│           │   └── ShareItServerAppTests.java # smoke/context load test
│           └── resources/
│               └── application-test.properties # test DB & JPA configuration
│
└── pom.xml                                # parent Maven configuration (multi-module)
```


## Testing

- **Unit tests** — cover core business logic in services (Mockito + JUnit 5).
- **Controller tests** — validate REST endpoints using `MockMvc`.
- **Integration tests** — run full Spring Boot context to test repository & DB integration.
- **Smoke tests** — ensure application context loads correctly.

> Coverage includes service-layer logic, validation rules, REST endpoint behavior, and DB persistence.

---

## Skills Practiced
- Designing and documenting **REST APIs** with clear module boundaries (gateway ↔ server).
- Applying **Spring Data JPA**: entity mapping, relationships, derived queries, pagination.
- Structuring **PostgreSQL** schemas and initializing with SQL scripts.
- Implementing **validation** and centralized **error handling**.
- Writing maintainable, testable code: unit, controller, and integration tests with JUnit & Mockito.
- Managing **multi-module Maven projects** and applying clean architecture principles.

---

## How to run

The project is a Spring Boot application and can be run in two ways.

### Option 1: Run locally without Docker (default)
```bash
mvn spring-boot:run
or
docker-compose up
```