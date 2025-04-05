# Java + Gradle + JOOQ + Flyway + Spotless + Google style formatting + Checkstyle
## Getting started
- Create a free PostgreSQL instance. \
  👉 https://app.koyeb.com/
- Create a file at: `src/test/resources/application.properties`. \
  Example content:

```properties
jdbc.url=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/db_name
jdbc.username=db_username
jdbc.password=db_password
```

- Execute the test suite to verify DB connectivity and see JOOQ in action.
- Review the test cases to understand how JOOQ interacts with the DB.
- Feel free to modify queries, add assertions, and experiment

## Useful Commands

### ✅ Automation script
```
python3 build_and_sync.py 
```

#### 💡 Create a `.env` file with the correct DB credentials.
#### Example:
```
DB_URL=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/db_name
DB_USER=db_username
DB_PASSWORD=db_password
```
