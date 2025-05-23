package org.example;

import static org.jooq.generated.Tables.ADDRESS;
import static org.jooq.generated.Tables.CATEGORIES;
import static org.jooq.generated.Tables.CUSTOMERS;
import static org.jooq.generated.Tables.EMPLOYEE;
import static org.jooq.generated.Tables.FLYWAY_SCHEMA_HISTORY;
import static org.jooq.generated.Tables.ORDERS;
import static org.jooq.generated.Tables.ORDER_ITEMS;
import static org.jooq.generated.Tables.PERSON;
import static org.jooq.generated.Tables.PRODUCTS;
import static org.jooq.generated.Tables.PRODUCT_CATEGORIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.jooq.Record;
import org.jooq.generated.tables.records.CategoriesRecord;
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
          final var categories = dsl.selectFrom(CATEGORIES).fetch();
          assertTrue(categories.size() >= 4);
          final var categoryNames = categories.stream().map(CategoriesRecord::getName).toList();
          assertTrue(
              categoryNames.containsAll(List.of("Books", "Electronics", "Computers", "Games")));
        });
  }

  @Test
  void testFetchAllCustomers() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(CUSTOMERS).fetch();
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
          final var records = dsl.selectFrom(ORDER_ITEMS).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllOrders() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(ORDERS).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllProductCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(PRODUCT_CATEGORIES).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchAllProducts() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var records = dsl.selectFrom(PRODUCTS).fetch();
          assertFalse(records.isEmpty());
        });
  }

  @Test
  void testFetchOrdersWithCustomerDetails() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(ORDERS.ID, CUSTOMERS.FULL_NAME, ORDERS.ORDER_DATE, ORDERS.TOTAL_AMOUNT)
                  .from(ORDERS)
                  .join(CUSTOMERS)
                  .on(ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMERS.FULL_NAME));
                assertNotNull(record.get(ORDERS.ORDER_DATE));
              });
        });
  }

  @Test
  void testFetchProductsWithCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(PRODUCTS.ID, PRODUCTS.NAME, CATEGORIES.NAME.as("category_name"))
                  .from(PRODUCTS)
                  .join(PRODUCT_CATEGORIES)
                  .on(PRODUCTS.ID.eq(PRODUCT_CATEGORIES.PRODUCT_ID))
                  .join(CATEGORIES)
                  .on(PRODUCT_CATEGORIES.CATEGORY_ID.eq(CATEGORIES.ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(PRODUCTS.NAME));
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
                      ORDER_ITEMS.ORDER_ID,
                      PRODUCTS.NAME.as("product_name"),
                      ORDER_ITEMS.QUANTITY,
                      ORDER_ITEMS.UNIT_PRICE)
                  .from(ORDER_ITEMS)
                  .join(PRODUCTS)
                  .on(ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get("product_name"));
                assertNotNull(record.get(ORDER_ITEMS.QUANTITY));
              });
        });
  }

  @Test
  void testTotalOrderAmountPerCustomer() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CUSTOMERS.FULL_NAME, DSL.sum(ORDERS.TOTAL_AMOUNT).as("total_spent"))
                  .from(CUSTOMERS)
                  .join(ORDERS)
                  .on(CUSTOMERS.ID.eq(ORDERS.CUSTOMER_ID))
                  .groupBy(CUSTOMERS.FULL_NAME)
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMERS.FULL_NAME));
                assertNotNull(record.get("total_spent"));
              });
        });
  }

  @Test
  void testProductCountPerCategory() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CATEGORIES.NAME, DSL.count(PRODUCTS.ID).as("product_count"))
                  .from(CATEGORIES)
                  .join(PRODUCT_CATEGORIES)
                  .on(CATEGORIES.ID.eq(PRODUCT_CATEGORIES.CATEGORY_ID))
                  .join(PRODUCTS)
                  .on(PRODUCT_CATEGORIES.PRODUCT_ID.eq(PRODUCTS.ID))
                  .groupBy(CATEGORIES.NAME)
                  .fetch();

          assertFalse(results.isEmpty());
          results.forEach(
              record -> {
                assertNotNull(record.get(CATEGORIES.NAME));
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
                      CUSTOMERS.ID,
                      CUSTOMERS.FULL_NAME,
                      DSL.sum(ORDERS.TOTAL_AMOUNT).as("total_spent"))
                  .from(ORDERS)
                  .join(CUSTOMERS)
                  .on(ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID))
                  .groupBy(CUSTOMERS.ID, CUSTOMERS.FULL_NAME)
                  .orderBy(DSL.sum(ORDERS.TOTAL_AMOUNT).desc())
                  .limit(2)
                  .fetch();

          assertEquals(2, results.size());
          results.forEach(
              record -> {
                assertNotNull(record.get(CUSTOMERS.FULL_NAME));
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
                      CUSTOMERS.FULL_NAME,
                      ORDERS.ID.as("order_id"),
                      ORDERS.ORDER_DATE,
                      ORDERS.TOTAL_AMOUNT)
                  .from(CUSTOMERS)
                  .leftJoin(ORDERS)
                  .on(CUSTOMERS.ID.eq(ORDERS.CUSTOMER_ID))
                  .fetch();

          assertFalse(results.isEmpty());

          results.forEach(record -> assertNotNull(record.get(CUSTOMERS.FULL_NAME)));
        });
  }

  @Test
  void testCustomersWithoutOrdersWithId() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var result =
              dsl.select(CUSTOMERS.ID, CUSTOMERS.FULL_NAME)
                  .from(CUSTOMERS)
                  .leftJoin(ORDERS)
                  .on(CUSTOMERS.ID.eq(ORDERS.CUSTOMER_ID))
                  .where(ORDERS.ID.isNull())
                  .fetch();

          assertFalse(result.isEmpty(), "Expected customers without orders");

          result.forEach(
              record -> {
                assertNotNull(record.get("id", Long.class));
                assertNotNull(record.get("full_name", String.class));
              });
        });
  }

  @Test
  void testLeetcodePersonAddressJoin() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(PERSON.FIRSTNAME, PERSON.LASTNAME, ADDRESS.CITY, ADDRESS.STATE)
                  .from(PERSON)
                  .leftJoin(ADDRESS)
                  .using(PERSON.PERSONID)
                  .fetch();

          assertFalse(results.isEmpty());

          results.forEach(
              record -> {
                assertNotNull(record.get(PERSON.FIRSTNAME));
                assertNotNull(record.get(PERSON.LASTNAME));
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
                  .on(subordinate.MANAGERID.eq(manager.ID))
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
