# AeroMaint

Equipment maintenance management web app for an airport authority.
Final-year engineering student project: the owner must be able to explain every part in an interview,
so keep code simple and readable, and explain each phase briefly when it is finished.

## Goal
A web app where airport staff report equipment faults, supervisors assign them, technicians fix them,
and managers see statistics. It also manages preventive maintenance schedules.

## Stack
- Backend: Java 17, Spring Boot 3, Spring Security with JWT, Spring Data JPA, MySQL, Flyway migrations, OpenAPI/Swagger
- Frontend: Angular (latest stable), Angular Material, Chart.js
- DevOps: Docker, Docker Compose, GitHub Actions, Prometheus, Grafana, Spring Boot Actuator + Micrometer

Local dev environment: JDK 21 compiling with `java.version=17`; database is MariaDB from XAMPP
(MySQL-compatible). Write portable SQL that runs on both MariaDB and MySQL 8 (Docker uses MySQL 8).

## Roles
- TECHNICIAN: sees assigned tickets, updates status, adds comments
- SUPERVISOR: creates and assigns tickets, manages maintenance plans, sees team workload
- ADMIN: manages users, zones and equipment

## Data model
users, zones, equipment (name, type, zone, status, installed_at),
tickets (equipment, reporter, assignee, title, description, priority, status, created_at, resolved_at),
ticket_comments, maintenance_plans (equipment, interval_days, next_due, description), audit_log.

Ticket status flow: OPEN -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CLOSED. Reject invalid transitions.

## Features
1. Login with JWT and role-based access on every endpoint and Angular route
2. Ticket CRUD, assignment, status changes, comments, filters (status, priority, equipment, assignee)
3. Equipment register with full fault and maintenance history
4. Preventive maintenance: a @Scheduled job creates tickets when a plan is due
5. Dashboard: open tickets, average resolution time, most failure-prone equipment, workload per technician
6. Audit log of important actions

## Phases (STOP after each phase and wait for "next")
Order agreed: build the whole app first (Phases 1-5), then do Docker + cloud (Phases 6-9) together.
1. Repo structure (backend/, frontend/, docker/, docs/), Spring Boot project, MySQL connection, Flyway schema, seed data (fake data only)
2. Authentication (JWT), user roles, security config
3. Equipment, zones and tickets APIs with validation, status-transition rules, global error handling, Swagger
4. Angular app: login, route guards per role, HTTP interceptor, ticket list/detail/create, equipment pages
5. Maintenance plans with the scheduler, audit log, dashboard endpoints and Angular dashboard
6. Dockerfiles (multi-stage), docker-compose with MySQL, environment variables for secrets
7. GitHub Actions: build backend and frontend, build Docker images, push to a registry, deploy step
8. Actuator metrics, Prometheus config, Grafana dashboard (requests, errors, latency, memory), one alert rule on error rate
9. Full README with architecture diagram, setup steps, screenshots section, API overview, short pitch demo script

## Rules
- Do not write any tests, test classes or test dependencies.
- Use fake or anonymized data only. Never commit secrets, passwords or JWT keys; use environment variables.
- Do not add features that were not asked for.
- The project must build and start after every phase; fix errors before finishing the phase.
- After each phase, commit with a conventional commit message (e.g. "feat: phase 2 authentication") and push.
- At the end of each phase, give: what was built, how to run it, and 3 questions an interviewer could ask about it.
- Installs and simple manual tasks: tell the owner what to do; they do them. Never install anything system-wide without asking.
