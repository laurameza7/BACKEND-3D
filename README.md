# UCC Pasto 3D — Backend (Java)

REST API in **Java 21 + Spring Boot 3** for the 3D map of the Pasto campus of Universidad
Cooperativa de Colombia and its artificial intelligence assistant (**Coopi**).

- Production: **Render** (Docker) — `https://ucc-pasto3d-backend.onrender.com/api/health`
- Database: **PostgreSQL on Neon** (repository `BASE-DATOS-3D-`)
- AI: **Google Gemini** with a local fallback (if Gemini fails or there is no API key, it answers from the database)

The code (packages, classes, methods, comments and tests) is written in English. Everything the user
sees — Coopi's answers and the error messages — stays in Spanish, because the users are the students
and visitors of the Pasto campus.

## Endpoints

| Method | Route | Description |
|---|---|---|
| GET | `/api/health` | Hello World / service status and Coopi metrics |
| GET | `/api/buildings` | Campus blocks with their position on the 3D map |
| GET | `/api/buildings/{id}/places` | Offices, laboratories and services of a block |
| GET | `/api/places` | All the places |
| GET | `/api/programs` | Academic programs |
| GET | `/api/programs/{id}` | One program |
| GET | `/api/faqs` | Frequently asked questions |
| POST | `/api/auth/register` | `{"name","email","fingerprint"}` → session with token |
| POST | `/api/auth/login` | `{"email","fingerprint"}` → session with token |
| POST | `/api/auth/guest` | Guest session (no registration) |
| GET | `/api/auth/me` | 🔒 Who owns the token |
| GET | `/api/assistant/greeting` | 🔒 "¡Hola! ¿Cómo estás? ¿En qué te puedo ayudar hoy?" |
| POST | `/api/assistant/ask` | 🔒 `{"question","programId"?}` → `{"answer","provider","buildingId","action"}` |
| GET | `/api/assistant/history` | 🔒 Last 20 questions of the registered user |

🔒 = requires the header `Authorization: Bearer <token>` (registered user or guest).

Every error has the same shape, with the message in Spanish (built by `config/ErrorHandler`):

```json
{ "error": "Correo o contraseña incorrectos." }
```

| Code | When |
|---|---|
| 400 | Empty or too long field, malformed JSON, password not hashed, id that is not a number |
| 401 | No token, expired or tampered token, or wrong email/password |
| 404 | Program or route that does not exist |
| 409 | An account with that email already exists |

### Passwords
- The browser computes the **SHA-256** of the password (max. 30 characters) and only sends that 64-character *fingerprint*.
- The backend hashes it again with **BCrypt** (random salt) and stores only that result in `app_user.password_hash`.
- The original password never travels over the network, is never stored and cannot be recovered.
- The session is a token signed with **HMAC-SHA256** (`TokenService`) that expires after 12 hours.

### Coopi actions
Before calling the AI, a **chain of intents** checks the message:

| Intent | Example | `action.type` |
|---|---|---|
| Greeting | "Hola", "Ey Coopi" | `GREETING` |
| List of programs | "¿Qué programas hay?" | `SHOW_PROGRAMS` |
| Go to a program | "Llévame al programa de Ingeniería de Software" | `GO_TO_PROGRAM` |
| Go to a place | "Quiero ver el restaurante" → Cafetería | `GO_TO_PLACE` |
| Go to a block | "Llévame al Bloque A" | `GO_TO_BUILDING` |

If none applies, the AI answers (Gemini → local fallback) with the information from the database.

## Software patterns

### Course patterns (creational and structural)

| # | Pattern | Type | Where | What it is used for |
|---|---|---|---|---|
| 1 | **Singleton** | Creational | `ai/AssistantMetrics` | One single instance with Coopi's metrics (answers per provider, times, cache, quota). Shown in `GET /api/health` |
| 2 | **Prototype** | Creational | `ai/PromptTemplate` (+ `GeminiAIProvider.prototype()`) | The prompt with all the campus knowledge is built once and **cloned** for every question |
| 3 | **Builder** | Creational | `ai/PromptContextBuilder` | Builds the prompt step by step: instructions, buildings, places, programs, FAQs |
| 4 | **Abstract Factory** | Creational | `ai/AIFactory` → `GeminiFactory`, `LocalFactory` | Each AI family creates its provider and its formatter, which always go together |
| 5 | **Factory Method** | Creational | `security/SessionCreator` → `UserSessionCreator`, `GuestSessionCreator` | Starting a session is the same for everybody; each subclass decides which session it creates |
| 6 | **Adapter** | Structural | `ai/GeminiAIProvider` | Adapts the Google Gemini REST API to the `AIProvider` interface of the system |
| 7 | **Bridge** | Structural | `ai/AssistantProfile` → `VisitorProfile`, `StudentProfile` (+ `AIProvider`) | Separates the profile of the person asking from the AI engine: any profile with any provider |
| 8 | **Decorator** | Structural | `ai/AIProviderDecorator` → `CachedAIProvider`, `TimedAIProvider` | Adds an answer cache and time measurement without touching the providers |
| 9 | **Facade** | Structural | `service/AssistantFacade.ask()` | A single entry point that hides intents, AI, fallback and saving |
| 10 | **Proxy** | Structural | `ai/AIProviderProxy` | Protection proxy in front of Gemini: limits the questions per minute; when the quota runs out the local AI answers |

How they work together when someone asks Coopi:

```
AssistantFacade (Facade)
 └─ chain of intents (greeting, programs, places)               ← Chain of Responsibility
 └─ AIProviderFactory → families GeminiFactory / LocalFactory    ← Abstract Factory
      └─ TimedAIProvider( CachedAIProvider( AIProviderProxy( GeminiAIProvider ) ) )
           Decorator         Decorator         Proxy            Adapter
      └─ MarkdownFormatter / PlainTextFormatter                  ← products of the same family
 └─ VisitorProfile | StudentProfile  ×  provider                 ← Bridge
      └─ PromptTemplate.copy() (Prototype) built with PromptContextBuilder (Builder)
 └─ AssistantMetrics.instance()                                  ← Singleton
Login: UserSessionCreator / GuestSessionCreator                  ← Factory Method
```

Each pattern has its test in `src/test/java/.../PatternsTests.java`.

### Other patterns and practices

| Pattern | Where |
|---|---|
| Strategy | `ai/AIProvider`: Gemini and Local are interchangeable strategies |
| Chain of Responsibility | `intent/*` (Greeting → Programs → Places → AI) and the Gemini → Local fallback |
| Command | `ActionDTO`: the backend sends the action and the frontend runs it |
| Interceptor | `security/AuthenticationInterceptor` validates the token before the controllers |
| Repository | `repository/*` (Spring Data JPA) |
| DTO | `dto/Dtos` (records sent to the frontend) |
| MVC | `controller/*` |

## Environment variables

| Variable | Example |
|---|---|
| `DB_URL` | `jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require` |
| `DB_USER` | `neondb_owner` |
| `DB_PASSWORD` | `********` |
| `GEMINI_API_KEY` | key from https://aistudio.google.com/apikey |
| `GEMINI_MODEL` | `gemini-3.5-flash` (optional) |
| `AI_PROVIDER` | `gemini` or `local` |
| `CORS_ORIGINS` | `https://ucc-pasto3d.vercel.app` |
| `APP_SECRET` | long random text used to sign the session tokens |
| `GEMINI_LIMIT_PER_MINUTE` | `30` (questions per minute the Proxy lets through to Gemini) |

The first names of these variables (`IA_PROVEEDOR`, `GEMINI_MODELO`, `CORS_ORIGENES`, `APP_SECRETO`,
`GEMINI_LIMITE_MINUTO`) are still accepted, so an existing Render configuration keeps working.

## Running locally (without PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
# open http://localhost:8080/api/health
```

The `local` profile uses an in-memory H2 database with a copy of the migrations (`src/main/resources/db`).
The official source of the schema is the database repository.

## Tests

```bash
mvn verify        # compiles, runs the tests and writes the coverage to target/site/jacoco/index.html
```

GitHub Actions runs them on every push (**Actions** tab) and keeps the report as an artifact.

| Test class | What it checks |
|---|---|
| `PatternsTests` | One case for each course pattern (Singleton, Prototype, Builder, Abstract Factory, Factory Method, Adapter, Bridge, Decorator, Facade, Proxy) |
| `AuthenticationAndCoopiTests` | Full flow: register → login → greeting with the name → Coopi takes you to programs and places |
| `controller/AuthApiTests` | The password is never stored (only BCrypt), validations, `/me`, history separated per user |
| `controller/CampusApiTests` | Buildings, places per floor, programs with their faculty, 404/400 as JSON, CORS for Vercel |
| `intent/IntentsTests` | More than 30 real sentences («llévame al café», «¿dónde está el cajero?»…) and where they lead |
| `ai/LocalAIProviderTests` | The fallback AI with a hand-made test campus |
| `ai/FormattersTests`, `ai/TextUtilTests` | Cleaning Gemini's Markdown and normalizing texts |
| `security/TokenServiceTests` | Expired, tampered, signed with another secret or malformed tokens |
| `Pasto3dApplicationTests` | The application starts with the campus data |
