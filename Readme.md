# Java + Gradle + JOOQ + Flyway + Spotless + Google style auto formatting + Checkstyle + Python automation script
## Getting started
- Create a free PostgreSQL instance. \
  👉 https://app.koyeb.com/ (this site will give you credentials to access your PostgreSQL instance)
- Create a file at: `src/test/resources/application.properties`. - this file is the only source of truth \
  Example content (replace db_name, db_username, db_password with the actual credentials from your https://app.koyeb.com/ instance):

```properties
jdbc.url=jdbc:postgresql://ep-abcd-mavpa-a22n3nhs.eu-central-1.pg.koyeb.app/db_name
jdbc.username=db_username
jdbc.password=db_password
```

- Run python3 build_and_sync.py in your terminal.
- Execute the unit tests suite to verify DB connectivity and see JOOQ in action.
- Review and debug the unit tests to understand how JOOQ interacts with the DB.
- Feel free to modify queries, add assertions, and experiment
- If you find this Readme.md as inaccurate or messy or whatever, please let me know
