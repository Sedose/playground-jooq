# Java + Gradle + JOOQ + Flyway
## Getting started
- Create a free PostgreSQL instance. \
  👉 https://app.koyeb.com/
- Create a file at: `src/test/resources/application.properties`. \
  Example content:

```properties
jdbc.url=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/koyebdb
jdbc.username=your_username
jdbc.password=your_password
```

- Execute the test suite to verify DB connectivity and see JOOQ in action.
- Review the test cases to understand how JOOQ interacts with the DB.
- Feel free to modify queries, add assertions, and experiment

## Useful Gradle Commands

### ✅ Run all tests
```
./gradlew-env.sh test
```

### 🗄 Apply Flyway DB migrations
```
./gradlew-env.sh flywayMigrate
```

### 🧬 Generate jOOQ classes from current DB schema
```
./gradlew-env.sh generateJooq  
```

### 🔍 See Flyway migration status
```
./gradlew-env.sh flywayInfo
```

### 🧹 Clean generated sources and build
```
./gradlew clean
```

### 📜 Show all available Gradle tasks
```
./gradlew tasks
```

#### 💡 For commands starting from `./gradlew-env.sh` to work, create a `.env` file.
#### Example:
```
export DB_URL=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/koyebdb
export DB_USER=your_username
export DB_PASSWORD=your_password
```

