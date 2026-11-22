# Contributing

Guida per chi lavora su questo repository. Descrive come è strutturato il backend e le convenzioni da seguire quando si aggiunge o si modifica codice.

## Panoramica del repository

```
.
├── backend/            API Spring Boot (Java 25, Maven)
├── frontend/           Applicazione Angular
├── db/                 Script SQL di inizializzazione del database
├── docker-compose.yml  PostgreSQL 16 per lo sviluppo locale
└── .github/            Configurazione e automazioni
```

Il backend e il frontend sono due progetti separati. Questa guida riguarda il backend.

## Stack del backend

| Componente | Versione / scelta |
|---|---|
| Build | Maven (wrapper incluso in `backend/.mvn`) |
| Parent | `spring-boot-starter-parent` 4.1.2 |
| Java | 25 |
| GroupId / artifactId | `com.sio` / `backend` |
| Package base | `com.sio.backend` |
| Database | PostgreSQL 16, schema `sio` |
| Documentazione API | springdoc-openapi 3.1.0 (Swagger UI) |
| Utility | Lombok, Spring Boot DevTools, Bean Validation |

## Struttura dei package

Il backend usa una struttura **a layer**: ogni package contiene un solo tipo di classe. Non si organizza per feature.

```
com.sio.backend
├── BackendApplication.java   Punto di ingresso (@SpringBootApplication)
├── audit/                    Classi di audit
├── config/                   Configurazione Spring (es. OpenAPI)
├── controller/               Endpoint REST
├── dto/                      Oggetti di trasferimento dati (record)
├── entity/                   Entità JPA mappate sulle tabelle
├── exception/                Gestione centralizzata degli errori
├── generics/                 Classi riutilizzabili generiche
├── mapper/                   Conversione entity <-> DTO
├── repository/               Interfacce Spring Data JPA
├── resources/                Risorse non-Java legate al dominio
└── service/                  Logica di business
```

Il flusso di una richiesta è sempre lo stesso:

```
HTTP -> Controller -> Service -> Repository -> Database
                \-> Mapper (entity <-> DTO)
```

Il controller non contiene logica di business. Il service non conosce HTTP. Il repository non conosce i DTO.

## Convenzioni per layer

### Entity (`entity/`)

- Annotata con `@Entity`, `@Getter`, `@Setter` (Lombok).
- `@Table` dichiara sempre nome tabella e schema: `@Table(name = "pathologies", schema = "sio")`.
- La chiave primaria può essere una chiave naturale (`String code`) o surrogata (`Integer id`).
- I campi di timestamp usano `@CreationTimestamp` e `@ColumnDefault("CURRENT_TIMESTAMP")`.
- I vincoli di validazione Jakarta (`@NotNull`, `@Size`) stanno sull'entity, non solo sul DTO.
- `@Column` dichiara sempre `name`, `nullable` e `length` espliciti.

Esempio: [Pathologies.java](backend/src/main/java/com/sio/backend/entity/Pathologies.java)

### DTO (`dto/`)

- Sempre un `record` immutabile, mai una classe con setter.
- Ogni campo ha un'annotazione `@Schema` di Swagger con `description` ed `example`.
- La validazione (`@NotBlank`, `@NotNull`) sta sul record.
- Il DTO espone solo i campi che il client deve vedere: non è una copia dell'entity.

Esempio: [PathologiesDTO.java](backend/src/main/java/com/sio/backend/dto/PathologiesDTO.java)

### Mapper (`mapper/`)

- Classe annotata con `@Component`.
- Conversione **manuale**, senza MapStruct né altre librerie.
- Due metodi: `toDTO(entity)` e `toEntity(dto)`.
- Entrambi i metodi sono null-safe: se l'argomento è `null`, ritornano `null`.

Esempio: [PathologiesMapper.java](backend/src/main/java/com/sio/backend/mapper/PathologiesMapper.java)

### Repository (`repository/`)

- Interfaccia che estende `JpaRepository<Entity, TipoChiave>`.
- Nessuna implementazione: Spring Data genera le query.
- Aggiungere metodi derivati (`findByCode`, `existsByDescription`) solo quando servono davvero.

Esempio: [PathologiesRepository.java](backend/src/main/java/com/sio/backend/repository/PathologiesRepository.java)

### Service (`service/`)

- Annotato con `@Service` e `@Slf4j` (Lombok).
- Iniezione delle dipendenze **via costruttore**, con campi `private final`.
- Log di debug con `log.debug(...)` all'inizio delle operazioni di lettura.
- Quando una risorsa non esiste, lancia `NoSuchElementException` con un messaggio descrittivo. La traduzione in HTTP 404 è compito del gestore globale.
- Il service ritorna DTO, mai entity.

Esempio: [PathologiesService.java](backend/src/main/java/com/sio/backend/service/PathologiesService.java)

### Controller (`controller/`)

- Annotato con `@RestController` e `@RequestMapping("/api/<risorsa>")`.
- Documentazione OpenAPI obbligatoria: `@Tag` sulla classe, `@Operation` e `@ApiResponse` su ogni metodo.
- Il body in ingresso è sempre `@Valid @RequestBody`.
- L'identificativo nel path è `@PathVariable String id` (o il tipo corretto).
- Il controller non contiene logica: delega al service e ritorna il risultato.

Esempio: [PathologiesController.java](backend/src/main/java/com/sio/backend/controller/PathologiesController.java)

### Exception (`exception/`)

- Un unico `@RestControllerAdvice` centralizza la gestione degli errori.
- Ogni tipo di eccezione ha il suo `@ExceptionHandler`.
- La risposta di errore è il record `ApiError(timestamp, status, error, message, path)`.
- Mappature attuali: `NoSuchElementException` -> 404, `MethodArgumentNotValidException` -> 400, `HttpMessageNotReadableException` -> 400.

Esempio: [GlobalExceptionHandler.java](backend/src/main/java/com/sio/backend/exception/GlobalExceptionHandler.java)

### Config (`config/`)

- Configurazione Spring, annotata con `@Configuration`.
- La definizione OpenAPI globale (`@OpenAPIDefinition`) sta qui.

Esempio: [OpenApiConfig.java](backend/src/main/java/com/sio/backend/config/OpenApiConfig.java)

## Aggiungere una nuova risorsa

Per aggiungere una risorsa (per esempio `patients`), creare i file in quest'ordine:

1. `entity/Patients.java` — mappatura sulla tabella esistente.
2. `dto/PatientsDTO.java` — record con annotazioni `@Schema`.
3. `mapper/PatientsMapper.java` — `toDTO` e `toEntity`.
4. `repository/PatientsRepository.java` — estende `JpaRepository`.
5. `service/PatientsService.java` — logica di business.
6. `controller/PatientsController.java` — endpoint REST con documentazione OpenAPI.

Se la tabella non esiste ancora, aggiungerla a `db/init.sql` con `CREATE TABLE IF NOT EXISTS` e seeding idempotente (`ON CONFLICT DO NOTHING`).

## Database

- Lo schema è definito **solo** in `db/init.sql`. Hibernate non genera né modifica tabelle: `ddl-auto` è `none`.
- Lo script è idempotente: usa `IF NOT EXISTS` e `ON CONFLICT` per poter essere rieseguito.
- Lo schema applicativo è `sio`. Ogni tabella va creata lì.
- Per applicare modifiche allo schema in locale, ricreare il volume Docker:

```powershell
docker compose down -v
docker compose up -d
```

Attenzione: `-v` elimina i dati esistenti.

## Configurazione e profili

I file di configurazione stanno in `backend/src/main/resources/`:

| File | Scopo |
|---|---|
| `application.yaml` | Configurazione comune: datasource, JPA, porta server |
| `application-dev.yaml` | Profilo `dev`: log verboso, SQL in console |
| `application-prod.yaml` | Profilo `prod`: log ridotti, SQL disattivato |
| `application-test.yaml` | Profilo `test`: log minimi |
| `logback-spring.xml` | Appender di logging (console e file) |

Il server ascolta sulla porta **3000**. Il database è raggiungibile su `localhost:5434`.

## Avvio in locale

Avviare il database:

```powershell
docker compose up -d
```

Avviare il backend:

```powershell
cd backend
./mvnw spring-boot:run
```

Swagger UI è disponibile su `http://localhost:3000/swagger-ui.html`.

## Build e test

```powershell
cd backend
./mvnw clean verify
```

I test stanno in `backend/src/test/java`. Il test di contesto (`BackendApplicationTests`) verifica che l'applicazione si avvii correttamente.

## Stile del codice

- Non aggiungere commenti che ripetono ciò che il codice già dice. Commentare solo dove serve chiarimento.
- Preferire nomi espliciti a commenti esplicativi.
- Mantenere la coerenza con il codice esistente: se una risorsa segue un pattern, la successiva lo segue uguale.
- Non introdurre nuove librerie senza necessità: il mapper manuale è una scelta, non una mancanza.

## Commit

Usare messaggi in stile Conventional Commits:

```
feat: aggiungi gestione pazienti
fix: correggi porta PostgreSQL in configurazione
docs: aggiorna guida al contributo
```
