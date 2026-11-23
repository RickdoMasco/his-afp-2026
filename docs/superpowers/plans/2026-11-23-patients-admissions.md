# Patients and Admissions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Expose the `patients` and `admissions` tables through the Spring Boot backend with the same component set and conventions as the existing modules, then regenerate the frontend OpenAPI client.

**Architecture:** Package-by-layer, matching `pathologies`/`triage_colors`/`arrival_modes`. Each module is entity + DTO + mapper + repository + service + controller. Controllers return `ApiResponse<T>`; errors flow through the existing `GlobalExceptionHandler`.

**Tech Stack:** Java 25, Spring Boot 4.1.2, Spring Data JPA, PostgreSQL 16, Lombok, springdoc-openapi 3.1.0, Maven wrapper. Frontend: Angular 22, `@openapitools/openapi-generator-cli` 7.27.0.

**Spec:** [docs/superpowers/specs/2026-11-23-patients-admissions-design.md](../specs/2026-11-23-patients-admissions-design.md)

## Global Constraints

- Java 25. Build with `JAVA_HOME=C:\Program Files\Java\jdk-25.0.5.1` (the configured `jdk-25.0.2` does not exist).
- `spring.jpa.hibernate.ddl-auto: none` — never let Hibernate create or alter tables.
- Every entity qualifies the schema: `@Table(name = "...", schema = "sio")`.
- PostgreSQL enum columns map with `@JdbcTypeCode(SqlTypes.NAMED_ENUM)`.
- Controllers return `com.sio.backend.generics.ApiResponse<T>`; never a raw entity.
- Services throw `IllegalArgumentException` (400) and `EntityNotFoundException` / `NoSuchElementException` (404). No new exception types.
- Do not modify `db/init.sql`, the `users` table, or the existing three modules.
- Do not hand-edit files under `frontend/src/app/core/api/` — they are generated.

## Review Focus

- A patient with an open episode (`stato != DIM`) must be rejected on accept; a dismissed one must be accepted again.
- `codice_colore` is nullable in the DDL (`ON DELETE SET NULL`); an admission with a null color must still serialize and must sort last in the active list.
- Fiscal code lookup is case-insensitive and whitespace-tolerant; the stored value is uppercase.
- `PATCH .../stato` to `DIM` twice must not overwrite the first discharge timestamp.
- `braccialetto` generation must not collide with the seeded `YYYY-0001`..`YYYY-0007` rows.

---

### Task 1: Patient entity, enum and repository

**Files:**
- Create: `backend/src/main/java/com/sio/backend/entity/PatientSex.java`
- Modify: `backend/src/main/java/com/sio/backend/entity/Patients.java` (replace the untracked stub)
- Create: `backend/src/main/java/com/sio/backend/repository/PatientsRepository.java`

**Interfaces:**
- Consumes: nothing.
- Produces: `Patients` with getters/setters for `id, codiceFiscale, nome, cognome, dataNascita, sex, indirizzoVia, indirizzoCivico, comune, provincia, createdAt`; `PatientSex.M`, `PatientSex.F`; `PatientsRepository extends JpaRepository<Patients, Integer>` with `Optional<Patients> findByCodiceFiscaleIgnoreCase(String cf)` and `List<Patients> findAllByOrderByCognomeAscNomeAsc()`.

- [ ] **Step 1: Write `PatientSex`**

```java
package com.sio.backend.entity;

public enum PatientSex {
    M, F
}
```

- [ ] **Step 2: Write `Patients`**

Replace the stub. Fields per the spec's field-mapping table. `@Table(name = "patients", schema = "sio")`, `@Id @GeneratedValue(strategy = IDENTITY) Integer id`, `@Size(max = 16) @NotNull @Column(name = "codice_fiscale", nullable = false, length = 16) String codiceFiscale`, `@NotNull LocalDate dataNascita`, `@JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(name = "sex", columnDefinition = "patient_sex not null") PatientSex sex`, `@CreationTimestamp @ColumnDefault("CURRENT_TIMESTAMP") Instant createdAt`. Keep a `@PrePersist` that uppercases `codiceFiscale` and defaults `sex` to `M`. Add `@Schema` descriptions like the sibling entities.

- [ ] **Step 3: Write `PatientsRepository`**

```java
@Repository
public interface PatientsRepository extends JpaRepository<Patients, Integer> {
    Optional<Patients> findByCodiceFiscaleIgnoreCase(String cf);
    List<Patients> findAllByOrderByCognomeAscNomeAsc();
}
```

- [ ] **Step 4: Compile**

Run: `$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.5.1'; backend\mvnw.cmd -q -f backend\pom.xml compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/sio/backend/entity/Patients.java backend/src/main/java/com/sio/backend/entity/PatientSex.java backend/src/main/java/com/sio/backend/repository/PatientsRepository.java
git commit -m "feat: add Patients entity, PatientSex enum and repository"
```

---

### Task 2: Patient DTO, mapper, service and controller

**Files:**
- Create: `backend/src/main/java/com/sio/backend/dto/PatientsDTO.java`
- Create: `backend/src/main/java/com/sio/backend/mapper/PatientsMapper.java`
- Create: `backend/src/main/java/com/sio/backend/service/PatientsService.java`
- Create: `backend/src/main/java/com/sio/backend/controller/PatientsController.java`

**Interfaces:**
- Consumes: `Patients`, `PatientSex`, `PatientsRepository` from Task 1.
- Produces: `PatientsDTO(Integer id, String codiceFiscale, String nome, String cognome, LocalDate dataNascita, PatientSex sex, String indirizzoVia, String indirizzoCivico, String comune, String provincia)`; `PatientsService` with `getAllPatients()`, `getAllPatientsPaginated(Pageable)`, `getPatientById(Integer)`, `getPatientByFiscalCode(String)`, `createPatient(PatientsDTO)`, `editPatient(Integer, PatientsDTO)`, `deletePatient(Integer)`.

- [ ] **Step 1: Write `PatientsDTO`**

Record with the fields above. Validation: `@NotBlank @Size(min = 16, max = 16)` on `codiceFiscale`; `@NotBlank @Size(max = 100)` on `nome` and `cognome`; `@NotNull @PastOrPresent` on `dataNascita`; `@NotNull` on `sex`; `@Size(max = 255)` on `indirizzoVia`, `@Size(max = 20)` on `indirizzoCivico`, `@Size(max = 100)` on `comune`, `@Size(max = 5)` on `provincia`. Add `@Schema` on the record and each component, matching `TriageColorDTO`.

- [ ] **Step 2: Write `PatientsMapper`**

`@Component` with `toDTO(Patients)` and `toEntity(PatientsDTO)`, null-safe, copying every field. Uppercase and trim `codiceFiscale` in `toEntity`.

- [ ] **Step 3: Write `PatientsService`**

`@Service @Slf4j @Transactional(readOnly = true)`, constructor-injected repository and mapper. `getAllPatients()` maps `findAllByOrderByCognomeAscNomeAsc()`. `getPatientById` throws `NoSuchElementException("Patient not found with id: " + id)`. `getPatientByFiscalCode` trims, throws `NoSuchElementException("Patient not found with fiscal code: " + cf)`. `createPatient` throws `IllegalArgumentException` when the fiscal code already exists. `editPatient` loads, copies fields, saves. `deletePatient` calls `deleteById`. `@Transactional` on the three mutating methods. `log.debug` on reads, `log.warn` on the duplicate-code rejection.

- [ ] **Step 4: Write `PatientsController`**

`@RestController @RequestMapping("/api/patients") @Tag(name = "Patients", ...)`. Endpoints: `GET ""`, `GET "/paginated"`, `GET "/{id}"`, `GET "/search"` with `@RequestParam String cf`, `POST ""`, `PUT "/{id}"`, `DELETE "/{id}"`. Each returns `ApiResponse.success(...)` and carries `@Operation` + `@ApiResponses` like `PathologiesController`.

- [ ] **Step 5: Compile**

Run: `$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.5.1'; backend\mvnw.cmd -q -f backend\pom.xml compile`
Expected: BUILD SUCCESS.

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/sio/backend/dto/PatientsDTO.java backend/src/main/java/com/sio/backend/mapper/PatientsMapper.java backend/src/main/java/com/sio/backend/service/PatientsService.java backend/src/main/java/com/sio/backend/controller/PatientsController.java
git commit -m "feat: add Patients DTO, mapper, service and controller"
```

---

### Task 3: Admission entity, status enum and repository

**Files:**
- Create: `backend/src/main/java/com/sio/backend/entity/AdmissionStatus.java`
- Create: `backend/src/main/java/com/sio/backend/entity/Admission.java`
- Create: `backend/src/main/java/com/sio/backend/repository/AdmissionRepository.java`

**Interfaces:**
- Consumes: `Patients` (Task 1), `Pathologies`, `TriageColor`, `ArrivalMode`.
- Produces: `AdmissionStatus.ATT/VIS/OBI/RIC/DIM`; `Admission` with getters/setters for `id, patient, braccialetto, dataOraIngresso, stato, patologia, codiceColore, modalitaArrivo, noteTriage, updatedAt, dataOraDimissione`; `AdmissionRepository` with `existsByPatientIdAndStatoNot(Integer, AdmissionStatus)`, `findByStatoNotOrderByCodiceColorePriorityAscDataOraIngressoAsc(AdmissionStatus)`, `findWithDetailsById(Integer)`, `findByBraccialettoIgnoreCase(String)`.

- [ ] **Step 1: Write `AdmissionStatus`**

```java
package com.sio.backend.entity;

public enum AdmissionStatus {
    ATT, VIS, OBI, RIC, DIM
}
```

- [ ] **Step 2: Write `Admission`**

`@Table(name = "admissions", schema = "sio")`. `@ManyToOne(fetch = LAZY, optional = false) @JoinColumn(name = "patient_id", nullable = false) Patients patient`. `@NotNull @Size(max = 20) @Column(name = "braccialetto", nullable = false, unique = true, length = 20) String braccialetto`. `@Column(name = "data_ora_ingresso", updatable = false) LocalDateTime dataOraIngresso`. `@Enumerated(EnumType.STRING) @JdbcTypeCode(SqlTypes.NAMED_ENUM) @Column(name = "stato", nullable = false, columnDefinition = "admission_status") AdmissionStatus stato`. `@ManyToOne(LAZY, optional = false) Pathologies patologia` on `patologia_code`; `@ManyToOne(LAZY) TriageColor codiceColore` on `codice_colore` (nullable); `@ManyToOne(LAZY, optional = false) ArrivalMode modalitaArrivo` on `modalita_arrivo_code`. `@Column(name = "note_triage", columnDefinition = "TEXT") String noteTriage`. `LocalDateTime updatedAt`, `LocalDateTime dataOraDimissione`. `@PrePersist` defaults `dataOraIngresso`, `updatedAt`, `stato = ATT`; `@PreUpdate` refreshes `updatedAt`.

- [ ] **Step 3: Write `AdmissionRepository`**

```java
@Repository
public interface AdmissionRepository extends JpaRepository<Admission, Integer> {
    boolean existsByPatientIdAndStatoNot(Integer patientId, AdmissionStatus stato);

    @EntityGraph(attributePaths = {"patient", "patologia", "codiceColore", "modalitaArrivo"})
    List<Admission> findByStatoNotOrderByCodiceColorePriorityAscDataOraIngressoAsc(AdmissionStatus stato);

    @EntityGraph(attributePaths = {"patient", "patologia", "codiceColore", "modalitaArrivo"})
    Optional<Admission> findWithDetailsById(Integer id);

    @EntityGraph(attributePaths = {"patient", "patologia", "codiceColore", "modalitaArrivo"})
    Optional<Admission> findByBraccialettoIgnoreCase(String braccialetto);
}
```

- [ ] **Step 4: Compile**

Run: `$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.5.1'; backend\mvnw.cmd -q -f backend\pom.xml compile`
Expected: BUILD SUCCESS.

- [ ] **Step 5: Commit**

```bash
git add backend/src/main/java/com/sio/backend/entity/Admission.java backend/src/main/java/com/sio/backend/entity/AdmissionStatus.java backend/src/main/java/com/sio/backend/repository/AdmissionRepository.java
git commit -m "feat: add Admission entity, AdmissionStatus enum and repository"
```

---

### Task 4: Admission DTOs, mapper, service and controller

**Files:**
- Create: `backend/src/main/java/com/sio/backend/dto/AdmissionRequestDTO.java`
- Create: `backend/src/main/java/com/sio/backend/dto/AdmissionResponseDTO.java`
- Create: `backend/src/main/java/com/sio/backend/mapper/AdmissionMapper.java`
- Create: `backend/src/main/java/com/sio/backend/service/AdmissionService.java`
- Create: `backend/src/main/java/com/sio/backend/controller/AdmissionController.java`

**Interfaces:**
- Consumes: Task 1–3 types, plus `PatientsDTO`, `PatientsMapper`, `PatientsRepository`, `PathologiesDTO`, `PathologiesMapper`, `PathologiesRepository`, `TriageColorDTO`, `TriageColorMapper`, `TriageColorRepository`, `ArrivalModeDTO`, `ArrivalModeMapper`, `ArrivalModeRepository`.
- Produces: `AdmissionRequestDTO(PatientsDTO patient, String patologiaCode, String codiceColore, String modalitaArrivoCode, String noteTriage)`; `AdmissionResponseDTO(Integer id, String braccialetto, LocalDateTime dataOraIngresso, AdmissionStatus stato, String noteTriage, LocalDateTime updatedAt, LocalDateTime dataOraDimissione, PatientsDTO patient, PathologiesDTO patologia, TriageColorDTO codiceColore, ArrivalModeDTO modalitaArrivo)`; `AdmissionService` with `getActiveAdmissions()`, `getAdmissionById(Integer)`, `getAdmissionByBraccialetto(String)`, `accettaPaziente(AdmissionRequestDTO)`, `updateStato(Integer, AdmissionStatus)`.

- [ ] **Step 1: Write `AdmissionRequestDTO`**

Record as above. `@NotNull @Valid PatientsDTO patient`; `@NotBlank @Size(max = 10) String patologiaCode`; `@NotBlank @Size(max = 20) String codiceColore`; `@NotBlank @Size(max = 10) String modalitaArrivoCode`; plain `String noteTriage`.

- [ ] **Step 2: Write `AdmissionResponseDTO`**

Record as above, no validation annotations.

- [ ] **Step 3: Write `AdmissionMapper`**

`@Component`, constructor-injected `PatientsMapper`, `PathologiesMapper`, `TriageColorMapper`, `ArrivalModeMapper`. `toDTO(Admission)` null-safe, delegating each nested entity to its mapper.

- [ ] **Step 4: Write `AdmissionService`**

`@Service @Transactional(readOnly = true)`. `getActiveAdmissions()` maps `findByStatoNotOrderByCodiceColorePriorityAscDataOraIngressoAsc(DIM)`. `getAdmissionById` / `getAdmissionByBraccialetto` throw `EntityNotFoundException`. `accettaPaziente` is `@Transactional` and: upserts the patient via `findByCodiceFiscaleIgnoreCase` (update in place) or `save`; throws `IllegalArgumentException` when `existsByPatientIdAndStatoNot(id, DIM)`; resolves the three dictionary codes with `findById(code.toUpperCase().trim())`, throwing `EntityNotFoundException` when absent; generates a unique `braccialetto` as `Year.now().getValue() + "-" + ThreadLocalRandom.current().nextInt(1000, 9999)` retrying while `findByBraccialettoIgnoreCase` finds it; saves with `stato = ATT`. `updateStato` is `@Transactional`, loads via `findWithDetailsById`, sets `stato`, and stamps `dataOraDimissione` only when the new state is `DIM` and the field is still null.

- [ ] **Step 5: Write `AdmissionController`**

`@RestController @RequestMapping("/api/admissions") @Tag(name = "Admissions", ...)`. `GET ""`, `GET "/{id}"`, `GET "/braccialetto/{code}"`, `POST ""` with `@Valid @RequestBody AdmissionRequestDTO`, `PATCH "/{id}/stato"` with `@RequestParam AdmissionStatus stato`. All return `ApiResponse.success(...)` with `@Operation` + `@ApiResponses`.

- [ ] **Step 6: Compile**

Run: `$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.5.1'; backend\mvnw.cmd -q -f backend\pom.xml compile`
Expected: BUILD SUCCESS.

- [ ] **Step 7: Commit**

```bash
git add backend/src/main/java/com/sio/backend/dto/AdmissionRequestDTO.java backend/src/main/java/com/sio/backend/dto/AdmissionResponseDTO.java backend/src/main/java/com/sio/backend/mapper/AdmissionMapper.java backend/src/main/java/com/sio/backend/service/AdmissionService.java backend/src/main/java/com/sio/backend/controller/AdmissionController.java
git commit -m "feat: add Admission DTOs, mapper, service and controller"
```

---

### Task 5: End-to-end verification against the seeded database

**Files:**
- Modify: `README.md` (API table)

**Interfaces:**
- Consumes: everything from Tasks 1–4.
- Produces: verified running endpoints.

- [ ] **Step 1: Build and start**

Run: `$env:JAVA_HOME='C:\Program Files\Java\jdk-25.0.5.1'; backend\mvnw.cmd -f backend\pom.xml spring-boot:run` (background)
Expected: `Started BackendApplication` on port 3000.

- [ ] **Step 2: Verify the seeded reads**

Run: `curl.exe -s http://localhost:3000/api/patients` and `curl.exe -s http://localhost:3000/api/admissions`
Expected: 7 patients; 6 admissions (the `DIM` seed row excluded), ordered by triage priority.

- [ ] **Step 3: Verify the accept workflow**

Run: `curl.exe -s -X POST http://localhost:3000/api/admissions -H "Content-Type: application/json" -d '{"patient":{"codiceFiscale":"VRDLGU80A01H501X","nome":"Luca","cognome":"Verdi","dataNascita":"1980-01-01","sex":"M"},"patologiaCode":"C01","codiceColore":"VERDE","modalitaArrivoCode":"AUT","noteTriage":"test"}'`
Expected: `status: SUCCESS`, a generated `braccialetto`, `stato: ATT`.

- [ ] **Step 4: Verify the duplicate-open-episode rejection**

Re-run the same POST.
Expected: HTTP 400 with `status: FAIL` and the "already has an open admission" message.

- [ ] **Step 5: Verify the discharge stamp and re-acceptance**

Run: `curl.exe -s -X PATCH "http://localhost:3000/api/admissions/1/stato?stato=DIM"` twice.
Expected: `dataOraDimissione` set on the first call and unchanged by the second.

Then POST a new admission for the same patient as admission 1 (fiscal code `RSSMRA80E20H501U`).
Expected: `status: SUCCESS` — a dismissed patient may be accepted again.

- [ ] **Step 6: Verify the case-insensitive fiscal code lookup**

Run: `curl.exe -s "http://localhost:3000/api/patients/search?cf=%20rssmra80e20h501u%20"`
Expected: the Mario Rossi seed row, despite the lowercase input and surrounding spaces.

- [ ] **Step 7: Verify a null triage color serializes and sorts last**

Run: `docker exec sio_postgres psql -U sio_user -d sio_db -c "UPDATE sio.admissions SET codice_colore = NULL WHERE braccialetto = (SELECT braccialetto FROM sio.admissions WHERE stato <> 'DIM' ORDER BY braccialetto LIMIT 1);"` then `curl.exe -s http://localhost:3000/api/admissions`
Expected: HTTP 200, the affected admission has `codiceColore: null`, and it appears last in the list.

- [ ] **Step 8: Verify the braccialetto generator does not collide**

Run the Step 3 POST five times with five distinct fiscal codes.
Expected: five distinct `braccialetto` values, none equal to a seeded `YYYY-0001`..`YYYY-0007`.

- [ ] **Step 9: Verify the OpenAPI document**

Run: `curl.exe -s http://localhost:3000/api-docs`
Expected: `/api/patients` and `/api/admissions` paths present.

- [ ] **Step 10: Update the README API table and commit**

Add the patients and admissions rows to the endpoint table in `README.md`.

```bash
git add README.md
git commit -m "docs: document patients and admissions endpoints"
```

---

### Task 6: Regenerate the frontend OpenAPI client

**Files:**
- Delete: `frontend/src/app/core/api/` (entire directory)
- Regenerate: `frontend/src/app/core/api/`
- Modify: `frontend/src/app/core/resources/resources.ts`

**Interfaces:**
- Consumes: the live `/api-docs` from Task 5.
- Produces: a generated client whose service classes cover resources, patients and admissions.

- [ ] **Step 1: Install the pinned generator**

Run: `npm install --no-save @openapitools/openapi-generator-cli@7.27.0` in `frontend/`
Expected: the CLI is available via `npx openapi-generator-cli`.

- [ ] **Step 2: Delete the stale client**

Run: `Remove-Item -Recurse -Force frontend\src\app\core\api`

- [ ] **Step 3: Regenerate**

Run: `npx openapi-generator-cli generate -i http://localhost:3000/api-docs -g typescript-angular -o src/app/core/api --additional-properties=providedInRoot=true,useSingleRequestParameter=false,fileNaming=kebab-case,modelFileSuffix=DTO,serviceFileSuffix=.service,serviceSuffix=Service,apiModulePrefix=Api,configurationPrefix=Configuration,enumPropertyNaming=UPPERCASE,stringEnums=true` in `frontend/`
Expected: `core/api/` recreated with `api/`, `model/`, `index.ts`.

- [ ] **Step 4: Fix the consumer**

Update `frontend/src/app/core/resources/resources.ts` to inject the regenerated resources service class and call its method with the regenerated signature. Remove the now-unused `fetchPathoFromApi` if its call no longer type-checks.

- [ ] **Step 5: Build**

Run: `npm run build` in `frontend/`
Expected: build succeeds.

- [ ] **Step 6: Commit**

```bash
git add frontend/src/app/core/api frontend/src/app/core/resources/resources.ts
git commit -m "chore: regenerate OpenAPI client with patients and admissions"
```
