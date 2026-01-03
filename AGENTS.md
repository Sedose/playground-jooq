# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java` holds production code, including Flyway Java migrations in `src/main/java/db/migration` (e.g., `V1__CreateInitialSchema.java`).
- `src/main/resources` contains runtime resources like `logback.xml`.
- `src/test/java` contains JUnit tests and test DTOs under `org/example`.
- `src/test/resources` stores test configuration, including `application.properties` with JDBC settings.
- Generated JOOQ sources are written to `build/generated-src/jooq/main`.
- Checkstyle rules live at `config/checkstyle/checkstyle.xml`.

## Build, Test, and Development Commands
- `./gradlew ciPipeline` runs the full pipeline: clean, format, assemble, migrate, generate JOOQ, and tests.
- `./gradlew test` runs the JUnit 5 test suite.
- `./gradlew flywayMigrate` applies DB migrations; `./gradlew flywayClean` resets the DB (destructive).
- `./gradlew generateJooq` regenerates JOOQ sources from the configured database schema.
- `./gradlew spotlessApply` formats Java with Google Java Format and trims whitespace.
- `./gradlew assemble` builds the project artifacts.

## Coding Style & Naming Conventions
- Java formatting is enforced by Spotless with Google Java Format; keep code compatible with it.
- Checkstyle is enabled; review `config/checkstyle/checkstyle.xml` for rules.
- Migration classes follow Flyway naming: `V{number}__{Description}.java`.
- Test classes use `*Test.java` (e.g., `MainTest.java`).

## Testing Guidelines
- Tests use JUnit 5 (`junit-jupiter`).
- You must provide a local `src/test/resources/application.properties` with `jdbc.url`, `jdbc.username`, and `jdbc.password` to run DB-backed tests.
- Add tests for new queries or schema changes and keep them deterministic.

## Commit & Pull Request Guidelines
- Commit subjects are short, imperative, and sentence case; optional conventional prefixes are acceptable (e.g., `refactor:`).
- PRs should include a brief summary, the commands run (e.g., `./gradlew test`), and any DB migration notes.
- Link relevant issues and call out schema or data changes explicitly.

## Configuration & Security Notes
- Do not commit real database credentials. Use local `application.properties` values for testing.
- Treat `flywayClean` as a local-only operation; avoid it against shared databases.
