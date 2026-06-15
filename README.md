# Job Offer Microservice

A RESTful microservice for managing job offer lifecycles in a staffing platform.

## How to Run Locally

```bash
docker-compose up --build -d
```

The API will be available at `http://localhost:8080`.
Swagger UI: `http://localhost:8080/swagger-ui/index.html`

---

## Key Architectural Decisions

### Project Structure
The project is split into three packages:
- `domain/` — entities, value objects, enums, repositories, and domain exceptions. Business logic lives here.
- `application/` — service layer orchestrating domain logic and persistence.
- `api/` — controllers, DTOs (as records), and global exception handling.

This separation ensures the domain model stays independent of HTTP concerns, and the service layer stays independent of HTTP semantics.

### Domain Modeling
- `JobOffer` is the aggregate root. `SalaryEntry` and `BonusEntry` are value objects owned by a `Compensation` wrapper.
- `Compensation` is stored as a single `JSONB` column on `job_offers`, avoiding joins and N+1 queries on list endpoints.
- `Address` is a value object mapped with `@Embeddable` — stored as flat columns on `job_offers`, trading normalization for simplicity.
- UUIDs are generated in Java (not the DB) to allow ID assignment before persistence.

### State Machine

State transitions are implemented as methods on `JobOffer` (e.g. `submit()`, `approve()`). Each method validates the current state and throws `IllegalStateTransitionException` if the transition is invalid.

`CompanyConfig` flags control which intermediate states are skipped:
- `partialSaveEnabled` → enables `TO_FINALIZE`
- `approvalRequired` → enables `TO_APPROVE`
- `manualPostingRequired` → enables `TO_POST`

If no config exists for a company, all flags default to `false` — offers go straight to `PUBLISHED` on submit.
Config changes affect future transitions only — offers already in-flight keep their current state.

Available Transitions thus are chosen to be state-driven for practicality — the available actions depend only on the current status, since config already determined which states are reachable.



### API Design
- Dedicated `POST /{id}/{action}` endpoints per transition (e.g. `/submit`, `/approve`) rather than a generic `PATCH /status` — makes intent explicit for state machines and easier to secure individually.
- `availableTransitions` is a computed field on `GET /{id}`, returning action names (e.g. `["submit", "approve"]`) that map directly to endpoint URLs.
- Global exception handler maps domain exceptions to consistent HTTP responses: `404` for not found, `409` for illegal transitions, `400` for validation errors.

### Data Layer
- Schema managed with Flyway migrations; `ddl-auto=validate` ensures the schema and entity model stay in sync.
- Compensation (salary and bonus entries) stored as `JSONB` — always read/written with the offer, never queried independently.
- Indexes on `company_id`, `status`, and composite `(company_id, status)` for efficient list queries.

---

## Testing 
- **Unit tests:** State machine logic tested directly on `JobOffer` domain objects. 
- **Integration tests:** Full API lifecycle tested with MockMvc and Testcontainers (basic functionality tests), covering offer creation, validation, and state transitions.

---

## What I'd Improve With More Time
- **Available Transitions:** Modify to reflect only transitions that are executable, taking into account the company workflow configuration and offer completeness.
- **Unit Tests:** Extend to cover more business logic. 
- **Partial update:** Current `PUT` requires all fields; a `PATCH` endpoint would better support `TO_FINALIZE` partial saves.
- **`expire()` vs `close()`:** Both transition to `CLOSED` — would add a `closureReason` field to distinguish them and an expiresAt date field on the offer to enable automatic expiry via a scheduled job.
- **Optimistic locking:** Add `@Version` to `JobOffer` to handle concurrent transitions safely.
- **Transactional boundaries:** Add `@Transactional` on service methods to ensure state transitions and DB saves are atomic.
- **More integration tests:** Extend the basic tests to cover approval workflow, rejection and resubmit, and config flag changes.
  
