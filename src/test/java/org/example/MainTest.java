package org.example;

import static org.jooq.generated.Tables.CATEGORIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.jooq.Record;
import org.jooq.generated.tables.records.CategoriesRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  void testSimpleSelectQuery() throws Exception {
    TestDatabaseConfig.withDslContext(
        sqlContext -> {
          final Record record = sqlContext.select(DSL.val(1).as("result")).fetchOne();
          assertEquals(1, record.get("result"));
        });
  }

  @Test
  void testCurrentTimestampQuery() throws Exception {
    TestDatabaseConfig.withDslContext(
        sqlContext -> {
          final Record record =
              sqlContext.select(DSL.currentTimestamp().as("current_time")).fetchOne();
          System.out.println("Current time: " + record.get("current_time"));
        });
  }

  @Test
  void testFetchAllCategories() throws Exception {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var categories = dsl.selectFrom(CATEGORIES).fetch();
          assertTrue(categories.size() >= 4);
          final var categoryNames = categories.stream().map(CategoriesRecord::getName).toList();
          assertTrue(
              categoryNames.containsAll(List.of("Books", "Electronics", "Computers", "Games")));
        });
  }
}
