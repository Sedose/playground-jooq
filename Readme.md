# Java + Gradle + JOOQ + Flyway
- Create a free PostgreSQL instance. \
  👉 https://app.koyeb.com/
- Configure the database connection for tests. \
  Create a file at: `src/test/resources/application.properties`. \
  Example content:

```properties
jdbc.url=jdbc:postgresql://ep-sparkling-frog-a22n3nhs.eu-central-1.pg.koyeb.app/koyebdb
jdbc.username=your_username
jdbc.password=your_password
```

- Run the tests. \
  Execute the test suite to verify DB connectivity and see JOOQ in action.
- Explore and manipulate the database via JOOQ. \
  Review the test cases to understand how JOOQ interacts with the DB.
  Feel free to modify queries, add assertions, and experiment
