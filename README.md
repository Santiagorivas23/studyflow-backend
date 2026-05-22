# StudyFlow — Backend

Spring Boot 3 · Java 17 · PostgreSQL · Gemini API

## Despliegue en Railway (recomendado)

### 1. Crear proyecto en Railway
1. Ir a [railway.app](https://railway.app) → New Project → Deploy from GitHub Repo
2. Seleccionar este repositorio (`studyflow-backend`)
3. Railway detectará el `Dockerfile` automáticamente

### 2. Agregar PostgreSQL
- En el proyecto Railway: New → Database → PostgreSQL
- Railway conectará las variables automáticamente

### 3. Variables de entorno en Railway
Agrega estas variables en Settings → Variables:

| Variable | Descripción |
|---|---|
| `DATABASE_URL` | URL JDBC completa (ej: `jdbc:postgresql://...`) |
| `DATABASE_USERNAME` | Usuario PostgreSQL |
| `DATABASE_PASSWORD` | Contraseña PostgreSQL |
| `GEMINI_API_KEY` | API Key de Google Gemini |
| `SPRING_PROFILES_ACTIVE` | `prod` |

> **Truco Railway + PostgreSQL:** Si usas el PostgreSQL de Railway, las variables `${{Postgres.DATABASE_URL}}` etc. se conectan automáticamente con el plugin.

### 4. Deploy
Railway hace build y deploy automáticamente en cada push a `main`.

La URL del servicio será algo como `https://studyflow-backend-xxx.up.railway.app`.
El API estará en `https://studyflow-backend-xxx.up.railway.app/api`.

---

## Desarrollo local

Consulta la guía de despliegue en:

- backend/docs/README_DEPLOY.md

Para probar localmente:

1. Construir el JAR:

```
mvn -B -DskipTests clean package
```

2. Ejecutar el JAR:

```
java -jar target/*.jar
```

O usar Docker (recomendado):

```
docker build -t studyflow-backend:latest .
docker run -p 8080:8080 -e SERVER_PORT=8080 -e DATABASE_URL="jdbc:postgresql://..." -e GEMINI_API_KEY="..." studyflow-backend:latest
```

Production
----------

To run in production, set the `spring.profiles.active=prod` environment variable (or pass `-Dspring.profiles.active=prod`). The production profile uses Flyway migrations and validates the schema with Hibernate.

Required environment variables:

- `DATABASE_URL` or `SUPABASE_DATABASE_URL`: Full JDBC URL for the Postgres database (e.g. `jdbc:postgresql://host:5432/dbname`).
- `DATABASE_USERNAME` / `SUPABASE_DB_USER`: Database user.
- `DATABASE_PASSWORD` / `SUPABASE_DB_PASSWORD`: Database password.
- `GEMINI_API_KEY`: API key for Gemini (used by the Gemini client).
- `SPRING_PROFILES_ACTIVE`: set to `prod` to use production profile (alternative to passing `--spring.profiles.active=prod`).
- `SERVER_PORT` (optional): Port to expose the application.

Flyway
------

This project integrates Flyway for database migrations. The initial migration is located at `src/main/resources/db/migration/V1__initial_schema.sql` and will be executed automatically when the application starts with the `prod` profile (or when `spring.flyway.enabled=true`).

Deployment notes
----------------

- Ensure the database is reachable and credentials are correct before starting the app.
- Use `spring.jpa.hibernate.ddl-auto=validate` in production; do not use `update` in production environments.
- Store `GEMINI_API_KEY` securely in the environment or a secrets manager.

