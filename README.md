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
| GET | `/api/programas/{id}` | Un programa |
| GET | `/api/preguntas-frecuentes` | Preguntas frecuentes |
| POST | `/api/auth/registro` | `{"nombre","correo","huella"}` → sesión con token |
| POST | `/api/auth/ingresar` | `{"correo","huella"}` → sesión con token |
| POST | `/api/auth/invitado` | Sesión de invitado (sin registro) |
| GET | `/api/auth/yo` | 🔒 Quién es el dueño del token |
| GET | `/api/asistente/saludo` | 🔒 "¡Hola! ¿Cómo estás? ¿En qué te puedo ayudar hoy?" |
| POST | `/api/asistente/preguntar` | 🔒 `{"pregunta","programaId"?}` → `{"respuesta","proveedor","edificioId","accion"}` |
| GET | `/api/asistente/historial` | 🔒 Últimas 20 preguntas del usuario registrado |

🔒 = requiere la cabecera `Authorization: Bearer <token>` (usuario registrado o invitado).

Todos los errores responden con el mismo formato, en español (lo arma `config/ManejadorErrores`):

```json
{ "error": "Correo o contraseña incorrectos." }
```

| Código | Cuándo |
|---|---|
| 400 | Campo vacío o muy largo, JSON mal formado, contraseña sin cifrar, id que no es número |
| 401 | Sin token, token vencido o alterado, o correo/contraseña incorrectos |
| 404 | Programa o ruta que no existe |
| 409 | Ya existe una cuenta con ese correo |

### Contraseñas
- El navegador calcula **SHA-256** de la contraseña (máx. 30 caracteres) y solo envía esa *huella* de 64 caracteres.
- El backend la vuelve a cifrar con **BCrypt** (sal aleatoria) y guarda únicamente ese resultado en `usuario.hash_clave`.
- La contraseña original nunca viaja por la red, nunca se guarda y no se puede recuperar.
- La sesión es un token firmado con **HMAC-SHA256** (`TokenServicio`) que vence a las 12 horas.

### Acciones de Coopi
Antes de llamar a la IA, una **cadena de intenciones** revisa el mensaje:

| Intención | Ejemplo | `accion.tipo` |
|---|---|---|
| Saludo | "Hola", "Ey Coopi" | `SALUDO` |
| Lista de programas | "¿Qué programas hay?" | `VER_PROGRAMAS` |
| Ir a un programa | "Llévame al programa de Ingeniería de Software" | `IR_PROGRAMA` |
| Ir a un lugar | "Quiero ver el restaurante" → Cafetería | `IR_LUGAR` |
| Ir a un bloque | "Llévame al Bloque A" | `IR_EDIFICIO` |

Si ninguna aplica, responde la IA (Gemini → respaldo local) con la información de la base de datos.

## Patrones de software implementados

### Patrones del curso (creacionales y estructurales)

| # | Patrón | Tipo | Dónde | Para qué se usa |
|---|---|---|---|---|
| 1 | **Singleton** | Creacional | `ia/MetricasAsistente` | Una sola instancia con las métricas de Coopi (respuestas por proveedor, tiempos, caché, cuota). Se ve en `GET /api/salud` |
| 2 | **Prototype** | Creacional | `ia/PlantillaPrompt` (+ `GeminiProveedorIA.prototipo()`) | El prompt con todo el conocimiento del campus se arma una vez y se **clona** en cada pregunta |
| 3 | **Builder** | Creacional | `ia/ContextoPromptBuilder` | Arma por partes el prompt: instrucciones, edificios, lugares, programas, preguntas frecuentes |
| 4 | **Abstract Factory** | Creacional | `ia/FabricaIA` → `FabricaGemini`, `FabricaLocal` | Cada familia de IA crea su proveedor y su formateador, que siempre van juntos |
| 5 | **Factory Method** | Creacional | `seguridad/CreadorSesion` → `CreadorSesionUsuario`, `CreadorSesionInvitado` | El inicio de sesión es igual para todos; cada subclase decide qué sesión crea |
| 6 | **Adapter** | Estructural | `ia/GeminiProveedorIA` | Adapta la API REST de Google Gemini a la interfaz `ProveedorIA` del sistema |
| 7 | **Bridge** | Estructural | `ia/PerfilAsistente` → `PerfilVisitante`, `PerfilEstudiante` (+ `ProveedorIA`) | Separa el perfil de quien pregunta del motor de IA: cualquier perfil con cualquier proveedor |
| 8 | **Decorator** | Estructural | `ia/ProveedorIADecorador` → `ProveedorIAConCache`, `ProveedorIAMedido` | Agrega caché de respuestas y medición de tiempos sin tocar los proveedores |
| 9 | **Facade** | Estructural | `servicio/AsistenteFacade.preguntar()` | Un único punto de entrada que oculta intenciones, IA, respaldo y guardado |
| 10 | **Proxy** | Estructural | `ia/ProveedorIAProxy` | Proxy de protección delante de Gemini: limita las preguntas por minuto; si se agota la cuota responde la IA local |

Cómo se combinan cuando alguien le pregunta a Coopi:

```
AsistenteFacade (Facade)
 └─ cadena de intenciones (saludo, programas, lugares)        ← Chain of Responsibility
 └─ ProveedorIAFactory → familias FabricaGemini / FabricaLocal  ← Abstract Factory
      └─ ProveedorIAMedido( ProveedorIAConCache( ProveedorIAProxy( GeminiProveedorIA ) ) )
           Decorator          Decorator            Proxy             Adapter
      └─ FormateadorMarkdown / FormateadorTextoPlano            ← productos de la misma familia
 └─ PerfilVisitante | PerfilEstudiante  ×  proveedor             ← Bridge
      └─ PlantillaPrompt.copiar() (Prototype) armada con ContextoPromptBuilder (Builder)
 └─ MetricasAsistente.instancia()                               ← Singleton
Inicio de sesión: CreadorSesionUsuario / CreadorSesionInvitado  ← Factory Method
```

Cada patrón tiene su prueba en `src/test/java/.../PatronesTests.java`.

### Otros patrones y prácticas usados

| Patrón | Dónde |
|---|---|
| Strategy | `ia/ProveedorIA`: Gemini y Local son estrategias intercambiables |
| Chain of Responsibility | `intencion/*` (Saludo → Programas → Lugares → IA) y el respaldo Gemini → Local |
| Command | `AccionDTO`: el backend envía la acción y el frontend la ejecuta |
| Interceptor | `seguridad/AutenticacionInterceptor` valida el token antes de los controladores |
| Repository | `repositorio/*` (Spring Data JPA) |
| DTO | `dto/Dtos` (records que viajan al frontend) |
| MVC | `controlador/*` |

## Variables de entorno

| Variable | Ejemplo |
|---|---|
| `DB_URL` | `jdbc:postgresql://ep-xxxx.us-east-2.aws.neon.tech/neondb?sslmode=require` |
| `DB_USER` | `neondb_owner` |
| `DB_PASSWORD` | `********` |
| `GEMINI_API_KEY` | clave de https://aistudio.google.com/apikey |
| `GEMINI_MODELO` | `gemini-3.5-flash` (opcional) |
| `IA_PROVEEDOR` | `gemini` o `local` |
| `CORS_ORIGENES` | `https://ucc-pasto3d.vercel.app` |
| `APP_SECRETO` | texto largo y aleatorio para firmar los tokens de sesión |
| `GEMINI_LIMITE_MINUTO` | `30` (preguntas por minuto que deja pasar el Proxy hacia Gemini) |

## Correr en el computador (sin PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
# abrir http://localhost:8080/api/salud
```

El perfil `local` usa H2 en memoria con una copia de las migraciones (`src/main/resources/db`).
La fuente oficial del esquema es el repositorio de base de datos.

## Pruebas

```bash
mvn verify        # compila, corre las pruebas y genera la cobertura en target/site/jacoco/index.html
```

GitHub Actions las ejecuta en cada push (pestaña **Actions**) y deja el reporte como artefacto.

| Clase de prueba | Qué revisa |
|---|---|
| `PatronesTests` | Un caso por cada patrón del curso (Singleton, Prototype, Builder, Abstract Factory, Factory Method, Adapter, Bridge, Decorator, Facade, Proxy) |
| `AutenticacionYCoopiTests` | Flujo completo: registro → ingreso → saludo con el nombre → Coopi lleva a programas y lugares |
| `controlador/AuthApiTests` | La contraseña no se guarda (solo BCrypt), validaciones, `/yo`, historial separado por usuario |
| `controlador/CampusApiTests` | Edificios, lugares por piso, programas con su facultad, 404/400 en JSON, CORS para Vercel |
| `intencion/IntencionesTests` | Más de 30 frases reales («llévame al café», «¿dónde está el cajero?»…) y a dónde llevan |
| `ia/LocalProveedorIATests` | La IA de respaldo con un campus de prueba armado a mano |
| `ia/FormateadoresTests`, `ia/TextoUtilTests` | Limpieza del Markdown de Gemini y normalización de textos |
| `seguridad/TokenServicioTests` | Tokens vencidos, alterados, firmados con otro secreto o mal formados |
| `Pasto3dApplicationTests` | Arranque de la aplicación con los datos del campus |
