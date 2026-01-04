# Repository Guidelines

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

## **Programming style, code characteristics, philosophy**
* **Pipeline programming**
* **Data-oriented programming**
* **Functional-like programming**
* **Declarative-like programming**
* **Side-effect isolation** (I/O only in `main`)
* **Expression-oriented programming**
* **Structured, composable functions**
* **No mutable global state**
* **Prefer not to have even local mutable state unless absolutely needed to gain smth much more valuable**
* **Prefer not to have explicit `source code`-level loops unless absolutely needed to gain smth much more valuable**
* **Pure functions**
* **Minimal branching, maximal transformation**
* **Clean, predictable control flow**
* **Lean functional patterns**
* **Less code means better. Keep code dense. Code is a liability, not an asset**
* **The code is not opimized to gain maximum raw speed**
* **The code is kept vertical, so it stays within a regular monitor size, no need for endless zooming, scroling nonsense**
* **The code is opimized for correctness, simplicity and clarity**
* **The code is straightforward like do 1, do 2, do 3, get result**
