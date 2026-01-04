# Java + Gradle + JOOQ + Flyway + Gradle + Checkstyle

Build, migrate, and query a real PostgreSQL with a clean Java setup.

## Why this project exists
- Real migrations (Flyway), not toy schemas.
- Type-safe SQL with JOOQ.
- Format + lint locked in (Spotless + Checkstyle) to avoid bikeshedding.
- One command pipeline to prove it all works.

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

## What you get
- Flyway migrations in `src/main/java/db/migration`
- Generated JOOQ sources in `build/generated-src/jooq`
- JUnit 5 tests in `src/test/java`
- App config in `src/test/resources`

## Useful commands
- `./gradlew ciPipeline` - clean, format, assemble, migrate, generate JOOQ, test
- `./gradlew test` - run tests only
- `./gradlew flywayMigrate` - apply migrations
- `./gradlew generateJooq` - regenerate JOOQ code
- `./gradlew spotlessApply` - auto-format Java

## Notes
- Do not commit real DB credentials.
- If something feels off in this README, tell me and I will fix it.
