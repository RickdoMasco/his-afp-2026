# Contributing

Guide for anyone working on this repository. It describes how the project is structured and the conventions to follow when adding or changing code.

## Repository overview

```
.
├── backend/            Spring Boot API (Java 25, Maven)
├── frontend/           Angular application
├── db/                 SQL script that initializes the database
├── docker-compose.yml  PostgreSQL 16 for local development
└── .github/            Configuration and automation
```

The backend and the frontend are two separate projects. They talk over HTTP: the frontend calls the backend REST API.

## Backend

### Stack

| Component | Version / choice |
|---|---|
| Build | Maven (wrapper included in `backend/.mvn`) |
| Parent | `spring-boot-starter-parent` 4.1.2 |
| Java | 25 |
| GroupId / artifactId | `com.sio` / `backend` |
| Base package | `com.sio.backend` |
| Database | PostgreSQL 16, schema `sio` |
| API documentation | springdoc-openapi 3.1.0 (Swagger UI) |
| Utilities | Lombok, Spring Boot DevTools, Bean Validation, Actuator |

### Package structure

The backend uses a **layer** structure: each package holds one kind of class. It is not organized by feature.

```
com.sio.backend
├── BackendApplication.java   Entry point (@SpringBootApplication)
├── audit/                    Audit classes
├── config/                   Spring configuration (e.g. OpenAPI)
├── controller/               REST endpoints
├── dto/                      Data transfer objects (records)
├── entity/                   JPA entities mapped to tables
├── exception/                Centralized error handling
├── generics/                 Reusable generic classes
├── mapper/                   Entity <-> DTO conversion
├── repository/               Spring Data JPA interfaces
├── resources/                Non-Java domain resources
└── service/                  Business logic
```

A request always follows the same path:

```
HTTP -> Controller -> Service -> Repository -> Database
                \-> Mapper (entity <-> DTO)
```

The controller holds no business logic. The service knows nothing about HTTP. The repository knows nothing about DTOs.

### Layer conventions

#### Entity (`entity/`)

- Annotated with `@Entity`, `@Getter`, `@Setter` (Lombok).
- `@Table` always declares table name and schema: `@Table(name = "pathologies", schema = "sio")`.
- The primary key can be natural (`String code`) or surrogate (`Integer id`).
- Timestamp fields use `@CreationTimestamp` and `@ColumnDefault("CURRENT_TIMESTAMP")`.
- Jakarta validation constraints (`@NotBlank`, `@Size`) live on the entity, not only on the DTO.
- `@Column` always declares `name`, `nullable` and `length` explicitly.

Example: [Pathologies.java](backend/src/main/java/com/sio/backend/entity/Pathologies.java)

#### DTO (`dto/`)

- Always an immutable `record`, never a class with setters.
- Every field carries a Swagger `@Schema` annotation with `description` and `example`.
- Validation (`@NotBlank`, `@NotNull`) lives on the record.
- The DTO exposes only the fields the client should see: it is not a copy of the entity.

Example: [PathologiesDTO.java](backend/src/main/java/com/sio/backend/dto/PathologiesDTO.java)

#### Mapper (`mapper/`)

- Class annotated with `@Component`.
- **Manual** conversion, no MapStruct or other libraries.
- Two methods: `toDTO(entity)` and `toEntity(dto)`.
- Both methods are null-safe: a `null` argument returns `null`.

Example: [PathologiesMapper.java](backend/src/main/java/com/sio/backend/mapper/PathologiesMapper.java)

#### Repository (`repository/`)

- Interface extending `JpaRepository<Entity, KeyType>`.
- No implementation: Spring Data generates the queries.
- Add derived methods (`findByCode`, `existsByDescription`) only when actually needed.

Example: [PathologiesRepository.java](backend/src/main/java/com/sio/backend/repository/PathologiesRepository.java)

#### Service (`service/`)

- Annotated with `@Service` and `@Slf4j` (Lombok).
- Dependencies injected **through the constructor**, with `private final` fields.
- Class-level `@Transactional(readOnly = true)`; write methods override it with `@Transactional`.
- Debug logging with `log.debug(...)` at the start of read operations.
- When a resource does not exist, throw `NoSuchElementException` with a descriptive message. Translating it to HTTP 404 is the global handler's job.
- The service returns DTOs, never entities.

Example: [PathologiesService.java](backend/src/main/java/com/sio/backend/service/PathologiesService.java)

#### Controller (`controller/`)

- Annotated with `@RestController` and `@RequestMapping("/api/<resource>")`.
- OpenAPI documentation is mandatory: `@Tag` on the class, `@Operation` and `@ApiResponses` on every method.
- The incoming body is always `@Valid @RequestBody`.
- The path identifier is `@PathVariable String code` (or the correct type).
- The controller holds no logic: it delegates to the service and returns the result.
- Every response is wrapped in `ApiResponse<T>`.

Example: [PathologiesController.java](backend/src/main/java/com/sio/backend/controller/PathologiesController.java)

#### Exception (`exception/`)

- A single `@RestControllerAdvice` centralizes error handling.
- Each exception type has its own `@ExceptionHandler`.
- The error response is the generic `ApiResponse<Void>` with status `FAIL` or `ERROR`.
- Current mappings: `NoSuchElementException` -> 404, `EntityNotFoundException` -> 404, `IllegalArgumentException` -> 400, `MethodArgumentNotValidException` -> 400, `HttpMessageNotReadableException` -> 400, `TransactionSystemException` -> 400, `Exception` -> 500.

Example: [GlobalExceptionHandler.java](backend/src/main/java/com/sio/backend/exception/GlobalExceptionHandler.java)

#### Generics (`generics/`)

- `ApiResponse<T>` is the standard wrapper for every REST response.
- `ApiResponseStatus` is the outcome enum: `SUCCESS`, `FAIL`, `ERROR`.
- Factory methods: `ApiResponse.success(data)`, `ApiResponse.fail(message, statusCode)`, `ApiResponse.error(message, statusCode, errorCode)`.

Example: [ApiResponse.java](backend/src/main/java/com/sio/backend/generics/ApiResponse.java)

#### Config (`config/`)

- Spring configuration, annotated with `@Configuration`.
- The global OpenAPI definition (`@OpenAPIDefinition`) lives here.

Example: [OpenApiConfig.java](backend/src/main/java/com/sio/backend/config/OpenApiConfig.java)

### Adding a new resource

To add a resource (for example `patients`), create the files in this order:

1. `entity/Patients.java` — mapping to the existing table.
2. `dto/PatientsDTO.java` — record with `@Schema` annotations.
3. `mapper/PatientsMapper.java` — `toDTO` and `toEntity`.
4. `repository/PatientsRepository.java` — extends `JpaRepository`.
5. `service/PatientsService.java` — business logic.
6. `controller/PatientsController.java` — REST endpoints with OpenAPI documentation.

If the table does not exist yet, add it to `db/init.sql` with `CREATE TABLE IF NOT EXISTS` and idempotent seeding (`ON CONFLICT DO NOTHING`).

### Database

- The schema is defined **only** in `db/init.sql`. Hibernate neither creates nor changes tables: `ddl-auto` is `none`.
- The script is idempotent: it uses `IF NOT EXISTS` and `ON CONFLICT` so it can be re-run.
- The application schema is `sio`. Every table must be created there.
- To apply schema changes locally, recreate the Docker volume:

```powershell
docker compose down -v
docker compose up -d
```

Warning: `-v` deletes existing data.

### Configuration and profiles

Configuration files live in `backend/src/main/resources/`:

| File | Purpose |
|---|---|
| `application.yaml` | Common configuration: datasource, JPA, server port, springdoc |
| `application-dev.yaml` | `dev` profile: verbose logging, SQL in console |
| `application-prod.yaml` | `prod` profile: reduced logging, SQL disabled |
| `application-test.yaml` | `test` profile: minimal logging |
| `logback-spring.xml` | Logging appenders (console and file) |

The server listens on port **3000**. The database is reachable on `localhost:5434`.

## Frontend

### Stack

| Component | Version / choice |
|---|---|
| Framework | Angular 22 (standalone components, signals) |
| Build | Angular CLI (`@angular/build`) |
| Styling | Tailwind CSS 4 |
| UI kit | Optimus UI (`@openng/optimus-ui`) |
| Icons | `@openng/icons` |
| Tests | Vitest |

### Structure

```
frontend/src/app/
├── app.ts / app.html / app.config.ts / app.routes.ts
├── core/
│   ├── generics/ApiResponse.model.ts   ApiResponse<T> and Page<T>
│   └── resources/
│       ├── resources.model.ts          ITriageColor, IPathology, IArrivalMode
│       └── resources.ts                HTTP service, signal-based state
├── layout/main-layout/                 Shell: header + sidebar + router-outlet
└── ui/
    ├── header/                         Logo, ward name, dropdown, theme toggle
    ├── sidebar/                        Navigation items, version tag
    ├── toggle-theme/                   Dark mode switch
    └── resources/pathologies-dropdown/ Pathology select bound to Resources
```

### Conventions

- Standalone components only. Never set `standalone: true` — it is the default.
- Never set `changeDetection: OnPush` explicitly — it is the default.
- Use `input()`, `output()` and `model()` functions instead of decorators.
- Use `computed()` for derived state and `linkedSignal()` for synchronized state.
- Use `inject()` instead of constructor injection.
- Use native control flow (`@if`, `@for`, `@switch`), not `*ngIf` / `*ngFor`.
- Use `class` and `style` bindings, not `ngClass` / `ngStyle`.
- Import only the directives and pipes a template uses, never `CommonModule`.
- Prefer inline templates for small components.
- Components must pass AXE checks and meet WCAG AA.

The full Angular ruleset lives in [frontend/AGENTS.md](frontend/AGENTS.md).

### API base URL

The backend base URL is defined once in [resources.ts](frontend/src/app/core/resources/resources.ts):

```ts
const API_BASE_URL = 'http://localhost:3000';
```

Change it there, not at each call site.

### Response shape

The backend wraps every response in `ApiResponse<T>`:

```ts
export interface ApiResponse<T> {
  status: 'SUCCESS' | 'FAIL' | 'ERROR';
  statusCode: number;
  timestamp: string;
  data?: T;
  message?: string;
  errorCode?: string;
}
```

Paginated endpoints return `data` as a `Page<T>`:

```ts
export interface Page<T> {
  content: T[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
```

So `GET /resources/pathologies` gives `data.content`, while `GET /resources/triage-colors` gives `data` directly as an array.

## Code style

- Do not add comments that restate what the code already says. Comment only where clarification is needed.
- Prefer explicit names over explanatory comments.
- Stay consistent with the existing code: if one resource follows a pattern, the next one follows it too.
- Do not introduce new libraries without need: the manual mapper is a choice, not a gap.

## Commit messages

Use Conventional Commits:

```
feat: add patient management
fix: correct PostgreSQL port in configuration
docs: update contributing guide
```
