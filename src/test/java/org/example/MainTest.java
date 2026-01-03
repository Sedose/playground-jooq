package org.example;

import static org.jooq.generated.Tables.ADDRESS;
import static org.jooq.generated.Tables.CATEGORY;
import static org.jooq.generated.Tables.CUSTOMER;
import static org.jooq.generated.Tables.CUSTOMER_ORDER;
import static org.jooq.generated.Tables.EMPLOYEE;
import static org.jooq.generated.Tables.FLYWAY_SCHEMA_HISTORY;
import static org.jooq.generated.Tables.ORDER_ITEM;
import static org.jooq.generated.Tables.PERSON;
import static org.jooq.generated.Tables.PRODUCT;
import static org.jooq.generated.Tables.PRODUCT_CATEGORY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.jooq.Record;
import org.jooq.generated.tables.records.CategoryRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  void testSimpleSelectQuery() {
    TestDatabaseConfig.withDslContext(
        dslContext -> {
          final Record record = dslContext.select(DSL.val(1).as("result")).fetchOne();
          assertEquals(1, record.get("result"));
        });
  }

  @Test
  void testCurrentTimestampQuery() {
    TestDatabaseConfig.withDslContext(
        dslContext -> {
          final Record record =
              dslContext.select(DSL.currentTimestamp().as("current_time")).fetchOne();
          System.out.println("Current time: " + record.get("current_time"));
        });
  }

  @Test
  void testFetchAllCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var categories = dsl.selectFrom(CATEGORY).fetch();
          assertTrue(categories.size() >= 4);
          final var categoryNames = categories.stream().map(CategoryRecord::getName).toList();
          assertTrue(
              categoryNames.containsAll(List.of("Books", "Electronics", "Computers", "Games")));
        });
  }

  @Test
  void testFetchAllCustomers() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(CUSTOMER).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllFlywaySchemaHistory() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(FLYWAY_SCHEMA_HISTORY).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllOrderItems() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(ORDER_ITEM).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllOrders() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(CUSTOMER_ORDER).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllProductCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(PRODUCT_CATEGORY).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllProducts() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(PRODUCT).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchOrdersWithCustomerDetails() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(
                      CUSTOMER_ORDER.CUSTOMER_ORDER_ID,
                      CUSTOMER.FULL_NAME,
                      CUSTOMER_ORDER.ORDER_DATE,
                      CUSTOMER_ORDER.TOTAL_AMOUNT)
                  .from(CUSTOMER_ORDER)
                  .join(CUSTOMER)
                  .on(CUSTOMER_ORDER.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMER.FULL_NAME));
                assertNotNull(record.get(CUSTOMER_ORDER.ORDER_DATE));
              });
        });
  }

  @Test
  void testFetchProductsWithCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(PRODUCT.PRODUCT_ID, PRODUCT.NAME, CATEGORY.NAME.as("category_name"))
                  .from(PRODUCT)
                  .join(PRODUCT_CATEGORY)
                  .on(PRODUCT.PRODUCT_ID.eq(PRODUCT_CATEGORY.PRODUCT_ID))
                  .join(CATEGORY)
                  .on(PRODUCT_CATEGORY.CATEGORY_ID.eq(CATEGORY.CATEGORY_ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(PRODUCT.NAME));
                assertNotNull(record.get("category_name"));
              });
        });
  }

  @Test
  void testFetchOrderItemsWithProductDetails() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(
                      ORDER_ITEM.CUSTOMER_ORDER_ID,
                      PRODUCT.NAME.as("product_name"),
                      ORDER_ITEM.QUANTITY,
                      ORDER_ITEM.UNIT_PRICE)
                  .from(ORDER_ITEM)
                  .join(PRODUCT)
                  .on(ORDER_ITEM.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get("product_name"));
                assertNotNull(record.get(ORDER_ITEM.QUANTITY));
              });
        });
  }

  @Test
  void testTotalOrderAmountPerCustomer() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CUSTOMER.FULL_NAME, DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT).as("total_spent"))
                  .from(CUSTOMER)
                  .join(CUSTOMER_ORDER)
                  .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
                  .groupBy(CUSTOMER.FULL_NAME)
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMER.FULL_NAME));
                assertNotNull(record.get("total_spent"));
              });
        });
  }

  @Test
  void testProductCountPerCategory() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CATEGORY.NAME, DSL.count(PRODUCT.PRODUCT_ID).as("product_count"))
                  .from(CATEGORY)
                  .join(PRODUCT_CATEGORY)
                  .on(CATEGORY.CATEGORY_ID.eq(PRODUCT_CATEGORY.CATEGORY_ID))
                  .join(PRODUCT)
                  .on(PRODUCT_CATEGORY.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
                  .groupBy(CATEGORY.NAME)
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CATEGORY.NAME));
                assertTrue(((Integer) record.get("product_count")) > 0);
              });
        });
  }

  @Test
  void testTopCustomersBySpend() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(
                      CUSTOMER.CUSTOMER_ID,
                      CUSTOMER.FULL_NAME,
                      DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT).as("total_spent"))
                  .from(CUSTOMER_ORDER)
                  .join(CUSTOMER)
                  .on(CUSTOMER_ORDER.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID))
                  .groupBy(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME)
                  .orderBy(DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT).desc())
                  .limit(2)
                  .fetch();

          assertEquals(2, results.size());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMER.FULL_NAME));
                assertNotNull(record.get("total_spent"));
              });
        });
  }

  @Test
  void testCustomersLeftJoinOrders() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(
                      CUSTOMER.FULL_NAME,
                      CUSTOMER_ORDER.CUSTOMER_ORDER_ID.as("order_id"),
                      CUSTOMER_ORDER.ORDER_DATE,
                      CUSTOMER_ORDER.TOTAL_AMOUNT)
                  .from(CUSTOMER)
                  .leftJoin(CUSTOMER_ORDER)
                  .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
                  .fetch();

          assertFalse(results.isEmpty());

          results.forEach(record -> assertNotNull(record.get(CUSTOMER.FULL_NAME)));
        });
  }

  @Test
  void testCustomersWithoutOrdersWithId() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var result =
              dsl.select(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME)
                  .from(CUSTOMER)
                  .leftJoin(CUSTOMER_ORDER)
                  .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
                  .where(CUSTOMER_ORDER.CUSTOMER_ORDER_ID.isNull())
                  .fetch();

          assertFalse(result.isEmpty(), "Expected customers without orders");

          result.forEach(
              record -> {
                assertNotNull(record.get("customer_id", Long.class));
                assertNotNull(record.get("full_name", String.class));
              });
        });
  }

  @Test
  void testLeetcodePersonAddressJoin() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(PERSON.FIRST_NAME, PERSON.LAST_NAME, ADDRESS.CITY, ADDRESS.STATE)
                  .from(PERSON)
                  .leftJoin(ADDRESS)
                  .using(PERSON.PERSON_ID)
                  .fetch();

          assertFalse(results.isEmpty());

          results.forEach(
              record -> {
                assertNotNull(record.get(PERSON.FIRST_NAME));
                assertNotNull(record.get(PERSON.LAST_NAME));
              });
        });
  }

  @Test
  void testSubordinatesWhoEarnMoreThanTheirManagers() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var subordinate = EMPLOYEE.as("subordinate");
          final var manager = EMPLOYEE.as("manager");

          final var results =
              dsl.select(subordinate.NAME.as("Subordinate"))
                  .from(subordinate)
                  .join(manager)
                  .on(subordinate.MANAGER_ID.eq(manager.EMPLOYEE_ID))
                  .where(subordinate.SALARY.gt(manager.SALARY))
                  .fetch();

          final List<String> employeeNames = results.getValues("Subordinate", String.class);

          assertEquals(1, employeeNames.size(), "Expected exactly one subordinate");
          assertTrue(
              employeeNames.contains("Joe"),
              "Expected 'Joe' to be the subordinate earning more than their manager");
        });
  }
}
