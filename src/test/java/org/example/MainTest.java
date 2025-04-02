package org.example;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
