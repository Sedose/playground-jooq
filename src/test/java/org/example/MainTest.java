package org.example;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    final var config = TestDatabaseConfig.load();
    try (final Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      final DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      final Record record = sqlContext.select(DSL.val(1).as("result")).fetchOne();
      assertEquals(1, record.get("result"));
    }
  }

  @Test
  public void testCurrentTimestampQuery() throws Exception {
    final var config = TestDatabaseConfig.load();
    try (final Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      final DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      final Record record = sqlContext.select(DSL.currentTimestamp().as("current_time")).fetchOne();
      System.out.println("Current time: " + record.get("current_time"));
    }
  }

  @Test
  void testTestTableStructure() throws Exception {
    final var config = TestDatabaseConfig.load();
    try (final Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      final DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);

      final var columns =
          sqlContext
              .select(DSL.field("column_name", String.class), DSL.field("data_type", String.class))
              .from(DSL.table("information_schema.columns"))
              .where(DSL.field("table_name").eq("test_table"))
              .fetch();

      final boolean hasId =
          columns.stream()
              .anyMatch(
                  r ->
                      Objects.equals(r.get("column_name"), "id")
                          && Objects.equals(r.get("data_type"), "integer"));
      final boolean hasName =
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
}
