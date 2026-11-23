# SIO — Hospital Information System

Full-stack application for managing an emergency department (Pronto Soccorso). It tracks patients, admissions, triage colors, arrival modes and pathologies.

- **Backend** — Spring Boot 4.1.2 REST API, Java 25, PostgreSQL 16
- **Frontend** — Angular 22 with signals and Optimus UI
- **Database** — PostgreSQL 16 in Docker, schema `sio`

## Requirements

| Tool | Version | Notes |
|---|---|---|
| JDK | 25 | `JAVA_HOME` must point to it |
| Docker Desktop | any recent | for the database |
| Node.js | 22 LTS or 24 LTS | odd versions (23, 25) work but are not LTS |
| npm | 11+ | ships with Node |

Check what you have:

```powershell
java -version
docker --version
node --version
npm --version
```

## Quick start

Three terminals. Start them in this order.

### 1. Database

From the repository root:

```powershell
docker compose up -d
```

This starts PostgreSQL 16 on port **5434** and runs `db/init.sql` on first boot. The script creates the `sio` schema, the tables and the seed data.

Verify it is up:

```powershell
docker ps
docker exec sio_postgres psql -U sio_user -d sio_db -c "\dt sio.*"
```

You should see six tables: `admissions`, `arrival_modes`, `pathologies`, `patients`, `triage_colors`, `users`.

### 2. Backend

```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.5.1"   # adjust to your JDK path
.\mvnw.cmd spring-boot:run
```

On Linux or macOS use `./mvnw spring-boot:run`.

The API starts on **http://localhost:3000** with the `dev` profile active.

- Swagger UI: http://localhost:3000/swagger-ui.html
- OpenAPI JSON: http://localhost:3000/api-docs
- Health check: http://localhost:3000/actuator/health

### 3. Frontend

```powershell
cd frontend
npm install
npm start
```

The app starts on **http://localhost:4200**.

## Verifying everything works

With all three running:

```powershell
# database
docker exec sio_postgres psql -U sio_user -d sio_db -c "SELECT code, description FROM sio.pathologies;"

# backend
curl.exe http://localhost:3000/actuator/health
curl.exe http://localhost:3000/resources/triage-colors

# frontend
curl.exe -o NUL -w "%{http_code}`n" http://localhost:4200/
```

Expected: `{"status":"UP"}`, a JSON list of triage colors, and `200`.

## API overview

Every response is wrapped in `ApiResponse<T>`:

```json
{
  "status": "SUCCESS",
  "statusCode": 200,
  "timestamp": "2026-11-22T11:22:01.170Z",
  "data": { }
}
```

### Lookup resources — `/resources`

| Method | Path | Description |
|---|---|---|
| GET | `/resources/triage-colors` | All triage colors |
| GET | `/resources/triage-colors/paginated` | Paginated triage colors |
| GET | `/resources/triage-colors/{code}` | One triage color |
| POST | `/resources/triage-colors` | Create |
| PUT | `/resources/triage-colors/{code}` | Update |
| DELETE | `/resources/triage-colors/{code}` | Delete |
| GET | `/resources/arrival-modes` | All arrival modes |
| GET | `/resources/arrival-modes/paginated` | Paginated arrival modes |
| GET | `/resources/arrival-modes/{code}` | One arrival mode |
| POST | `/resources/arrival-modes` | Create |
| PUT | `/resources/arrival-modes/{code}` | Update |
| DELETE | `/resources/arrival-modes/{code}` | Delete |
| GET | `/resources/pathologies` | Paginated pathologies |
| GET | `/resources/pathologies/{code}` | One pathology |
| POST | `/resources/pathologies` | Create |
| PUT | `/resources/pathologies` | Update (code in body) |
| DELETE | `/resources/pathologies?code=C02` | Delete (code as query param) |
| GET | `/resources/sim-error` | Always throws, for testing error handling |

### Pathologies — `/api/pathologies`

Same CRUD, alternative paths:

| Method | Path |
|---|---|
| GET | `/api/pathologies` |
| GET | `/api/pathologies/paginated` |
| GET | `/api/pathologies/{code}` |
| POST | `/api/pathologies` |
| PUT | `/api/pathologies/{code}` |
| DELETE | `/api/pathologies/{code}` |

### Patients — `/api/patients`

| Method | Path | Description |
|---|---|---|
| GET | `/api/patients` | All patients, ordered by surname and name |
| GET | `/api/patients/paginated` | Paginated patients |
| GET | `/api/patients/{id}` | One patient |
| GET | `/api/patients/search?cf=RSSMRA80E20H501U` | One patient by fiscal code, case-insensitive |
| POST | `/api/patients` | Create |
| PUT | `/api/patients/{id}` | Update |
| DELETE | `/api/patients/{id}` | Delete |

### Admissions — `/api/admissions`

| Method | Path | Description |
|---|---|---|
| GET | `/api/admissions` | Open admissions (state other than `DIM`), ordered by triage priority then arrival time |
| GET | `/api/admissions/{id}` | One admission, with patient and dictionaries resolved |
| GET | `/api/admissions/braccialetto/{code}` | One admission by wristband code |
| POST | `/api/admissions` | Accept a patient: upserts the patient record, then opens an admission with state `ATT` |
| PATCH | `/api/admissions/{id}/stato?stato=VIS` | Change the clinical state; `DIM` stamps the discharge timestamp |

The clinical state machine is `ATT -> VIS -> OBI | RIC -> DIM`. Accepting a patient that already
has an open admission returns HTTP 400.

### Pagination

Paginated endpoints accept Spring's standard parameters:

```
?page=0&size=10&sort=code,asc
```

They return `data` as a page object:

```json
{
  "content": [ ],
  "number": 0,
  "size": 10,
  "totalElements": 6,
  "totalPages": 1,
  "first": true,
  "last": true
}
```

### Errors

| Status | When |
|---|---|
| 400 | Invalid body, validation failure, duplicate code |
| 404 | Resource not found |
| 500 | Unexpected server error (`errorCode: "THX403"`) |

Example:

```json
{
  "status": "FAIL",
  "statusCode": 404,
  "timestamp": "2026-11-22T11:22:17.400Z",
  "message": "Arrival mode not found with code: ZZZ"
}
```

## Seed data

`db/init.sql` inserts:

- **5 triage colors** — ROSSO, ARANCIONE, AZZURRO, VERDE, BIANCO
- **3 arrival modes** — AUT (Autonomo), AMB (Ambulanza 118), ELI (Elisoccorso)
- **6 pathologies** — C01 to C05, C19
- **3 users** — `medico`, `infermiere`, `amministrativo` (password `1234`, bcrypt-hashed)
- **7 patients** and **7 admissions**

## Running tests

Backend (requires the database to be running):

```powershell
cd backend
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.5.1"
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npx ng test --watch=false
```

## Building for production

Backend:

```powershell
cd backend
.\mvnw.cmd clean package
```

Produces `backend/target/backend-0.0.1-SNAPSHOT.jar`. Run it with:

```powershell
java -jar target/backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

Frontend:

```powershell
cd frontend
npx ng build
```

Output goes to `frontend/dist/frontend`.

## Project layout

```
.
├── backend/            Spring Boot API
│   ├── docs/           Architecture notes and diagrams
│   └── src/
├── frontend/           Angular application
│   └── src/app/
├── db/init.sql         Database schema and seed data
├── docker-compose.yml  PostgreSQL service
└── CONTRIBUTING.md     Structure and conventions
```

## Configuration

| Setting | Value | Where |
|---|---|---|
| Backend port | 3000 | `backend/src/main/resources/application.yaml` |
| Database port | 5434 | `docker-compose.yml` and `application.yaml` |
| Frontend port | 4200 | Angular CLI default |
| API base URL (frontend) | `http://localhost:3000` | `frontend/src/app/core/resources/resources.ts` |
| Active profile | `dev` | `application.yaml` |

## Troubleshooting

**`The JAVA_HOME environment variable is not defined correctly`**
Set `JAVA_HOME` to a JDK 25 installation before running Maven.

**`Port 3000 was already in use`**
A previous backend run is still alive. Find and stop it:

```powershell
Get-NetTCPConnection -LocalPort 3000 -State Listen | Select-Object OwningProcess
Stop-Process -Id <PID> -Force
```

**`Connection to localhost:5434 refused`**
The database container is not running. Run `docker compose up -d` and wait a few seconds.

**Frontend shows no data in the dropdown**
The backend must be running on port 3000. Check the browser console for CORS or connection errors.

**Schema changes do not appear**
`db/init.sql` only runs on an empty volume. Recreate it:

```powershell
docker compose down -v
docker compose up -d
```

Warning: this deletes all data.

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md) for the backend layer structure, frontend conventions and commit message format.
