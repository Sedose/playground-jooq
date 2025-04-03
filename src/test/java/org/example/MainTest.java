package org.example;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

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

      Result<Record> columns = sqlContext.fetch(
          "SELECT column_name, data_type " +
              "FROM information_schema.columns " +
              "WHERE table_name = 'test_table'"
      );

      boolean hasId = columns.stream().anyMatch(r ->
          Objects.equals(r.get("column_name"), "id") &&
              Objects.equals(r.get("data_type"), "integer")
      );
      boolean hasName = columns.stream().anyMatch(r ->
          Objects.equals(r.get("column_name"), "name") &&
              Objects.equals(r.get("data_type"), "character varying")
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
