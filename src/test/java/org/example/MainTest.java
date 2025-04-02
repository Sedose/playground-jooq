package org.example;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

  @BeforeAll
  static void setupDatabase() {
    try {
      Class.forName("org.postgresql.Driver");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("PostgreSQL driver missing", e);
    }
    var config = TestDatabaseConfig.load();
    Flyway.configure()
        .dataSource(config.url(), config.username(), config.password())
        .driver("org.postgresql.Driver")
        .load()
        .migrate();
  }

  @Test
  public void testSimpleSelectQuery() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      Record record = sqlContext.fetchOne("SELECT 1 AS result");
      assertEquals(1, record.get("result"));
    }
  }

  @Test
  public void testCurrentTimestampQuery() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      Record record = sqlContext.fetchOne("SELECT NOW() AS current_time");
      if (record != null) {
        System.out.println("Current time: " + record.get("current_time"));
      }
    }
  }

  @Test
  void testTestTableStructure() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);

      // Check if test_table exists and has correct columns
      Result<Record> columns = sqlContext.fetch(
          "SELECT column_name, data_type " +
              "FROM information_schema.columns " +
              "WHERE table_name = 'test_table'"
      );

      // Verify 'id' (integer) and 'name' (varchar) columns
      boolean hasId = columns.stream().anyMatch(r ->
          r.get("column_name").equals("id") &&
              r.get("data_type").equals("integer")
      );
      boolean hasName = columns.stream().anyMatch(r ->
          r.get("column_name").equals("name") &&
              r.get("data_type").equals("character varying")
      );

      assertAll(
          () -> assertTrue(hasId, "test_table should have an 'id' column"),
          () -> assertTrue(hasName, "test_table should have a 'name' column")
      );
    }
  }

  @Test
  void testFlywaySchemaHistory() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection = DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);

      // Check if Flyway migration V1 is recorded
      Record migrationRecord = sqlContext.fetchOne(
          "SELECT version, success " +
              "FROM flyway_schema_history " +
              "WHERE version = '1'"
      );

      assertNotNull(migrationRecord, "V1 migration should exist in Flyway history");
      assertEquals("1", migrationRecord.get("version"));
      assertTrue((boolean) migrationRecord.get("success"), "V1 migration should be marked as successful");
    }
  }
}
