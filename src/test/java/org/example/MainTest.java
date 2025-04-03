package org.example;

import static org.jooq.generated.tables.FlywaySchemaHistory.FLYWAY_SCHEMA_HISTORY;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  public void testSimpleSelectQuery() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      Record record = sqlContext.select(DSL.val(1).as("result")).fetchOne();
      assertEquals(1, record.get("result"));
    }
  }

  @Test
  public void testCurrentTimestampQuery() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      Record record = sqlContext.select(DSL.currentTimestamp().as("current_time")).fetchOne();
      System.out.println("Current time: " + record.get("current_time"));
    }
  }

  @Test
  void testTestTableStructure() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);

      var columns =
          sqlContext
              .select(DSL.field("column_name", String.class), DSL.field("data_type", String.class))
              .from(DSL.table("information_schema.columns"))
              .where(DSL.field("table_name").eq("test_table"))
              .fetch();

      boolean hasId =
          columns.stream()
              .anyMatch(
                  r ->
                      Objects.equals(r.get("column_name"), "id")
                          && Objects.equals(r.get("data_type"), "integer"));
      boolean hasName =
          columns.stream()
              .anyMatch(
                  r ->
                      Objects.equals(r.get("column_name"), "name")
                          && Objects.equals(r.get("data_type"), "character varying"));

      assertAll(
          () -> assertTrue(hasId, "test_table should have an 'id' column"),
          () -> assertTrue(hasName, "test_table should have a 'name' column"));
    }
  }

  @Test
  void testFlywaySchemaHistory() throws Exception {
    var config = TestDatabaseConfig.load();
    try (Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);

      // Check if Flyway migration V1 is recorded
      var migrationRecord =
          sqlContext
              .select(FLYWAY_SCHEMA_HISTORY.VERSION, FLYWAY_SCHEMA_HISTORY.SUCCESS)
              .from(FLYWAY_SCHEMA_HISTORY)
              .where(FLYWAY_SCHEMA_HISTORY.VERSION.eq("1"))
              .fetchOne();

      assertNotNull(migrationRecord, "V1 migration should exist in Flyway history");
      assertEquals("1", migrationRecord.get("version"));
      assertTrue(
          (boolean) migrationRecord.get("success"), "V1 migration should be marked as successful");
    }
  }
}
