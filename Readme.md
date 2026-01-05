# Kotlin + Gradle + JOOQ + Flyway + Gradle + ktlint (for Kotlin) + Spotless (for SQL)

Build, migrate, and query a real PostgreSQL with a clean Kotlin setup.

## Why this project exists
- Primarily to showcase Kotlin + JOOQ setup serving as template for other projects

## Quick start (5 minutes)

1) Create a free PostgreSQL instance at https://app.koyeb.com/

2) Create `src/test/resources/application.properties` with your DB credentials. Example:

```properties
jdbc.url=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/<here comes db name>
jdbc.username=<here comes real username>
jdbc.password=<here comes real password>
```

3) Run the full pipeline:

```bash
./gradlew ciPipeline
```

## Useful commands
- `./gradlew ciPipeline`
