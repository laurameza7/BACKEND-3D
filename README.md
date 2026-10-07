# UCC Pasto 3D — Backend (Java)

API REST en **Java 21 + Spring Boot 3** para el mapa 3D del campus Pasto de la Universidad
Cooperativa de Colombia y su asistente con inteligencia artificial (**Coopi**).

- Producción: **Render** (Docker) — `https://<tu-servicio>.onrender.com/api/salud`
- Base de datos: **PostgreSQL en Neon** (repositorio `ucc-pasto3d-database`)
- IA: **Google Gemini** con respaldo local (si Gemini falla o no hay API key, responde desde la BD)

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/salud` | Hello World / estado del servicio |
| GET | `/api/edificios` | Bloques del campus con su posición en el mapa 3D |
| GET | `/api/edificios/{id}/lugares` | Oficinas, laboratorios y servicios de un bloque |
| GET | `/api/lugares` | Todos los lugares |
| GET | `/api/programas` | Programas académicos |
| GET | `/api/preguntas-frecuentes` | Preguntas frecuentes |
| POST | `/api/asistente/preguntar` | `{"pregunta": "..."}` → `{"respuesta","proveedor","edificioId"}` |

## Patrones de software implementados

| # | Patrón | Dónde |
|---|---|---|
| 1 | **Strategy** | `ia/ProveedorIA` con dos estrategias: `GeminiProveedorIA` y `LocalProveedorIA` |
| 2 | **Factory** | `ia/ProveedorIAFactory` decide qué estrategia(s) usar según configuración |
| 3 | **Facade** | `servicio/AsistenteFacade.preguntar()` oculta contexto, IA, respaldo y registro |
| 4 | **Adapter** | `ia/GeminiProveedorIA` adapta la API REST de Gemini a la interfaz `ProveedorIA` |
| 5 | **Builder** | `ia/ContextoPromptBuilder` arma por partes el prompt con el conocimiento del campus |
| + | Repository | `repositorio/*` (Spring Data JPA) |
| + | DTO | `dto/Dtos` (records que viajan al frontend) |
| + | Singleton / Inyección de dependencias | Todos los `@Service`, `@Component` son instancias únicas manejadas por Spring |
| + | Chain of Responsibility (respaldo) | `AsistenteFacade` recorre la cadena Gemini → Local hasta que uno responde |
| + | MVC | `controlador/*` |

## Variables de entorno

| Variable | Ejemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require` |
| `DB_USER` | `neondb_owner` |
| `DB_PASSWORD` | `********` |
| `GEMINI_API_KEY` | clave de https://aistudio.google.com/apikey |
| `GEMINI_MODELO` | `gemini-2.5-flash` (opcional) |
| `IA_PROVEEDOR` | `gemini` o `local` |
| `CORS_ORIGENES` | `https://ucc-pasto3d.vercel.app` |

## Correr en el computador (sin PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
# abrir http://localhost:8080/api/salud
```

El perfil `local` usa H2 en memoria con una copia de las migraciones (`src/main/resources/db`).
La fuente oficial del esquema es el repositorio de base de datos.
