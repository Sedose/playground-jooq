package org.example;

import static org.jooq.generated.Tables.CATEGORIES;
import static org.jooq.generated.Tables.CUSTOMERS;
import static org.jooq.generated.Tables.FLYWAY_SCHEMA_HISTORY;
import static org.jooq.generated.Tables.ORDERS;
import static org.jooq.generated.Tables.ORDER_ITEMS;
import static org.jooq.generated.Tables.PRODUCTS;
import static org.jooq.generated.Tables.PRODUCT_CATEGORIES;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.dto.CustomerWithOrdersDto;
import org.example.dto.OrderDto;
import org.jooq.Record;
import org.jooq.generated.tables.records.CategoriesRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  void testSimpleSelectQuery() {
    TestDatabaseConfig.withDslContext(
        sqlContext -> {
          final Record record = sqlContext.select(DSL.val(1).as("result")).fetchOne();
          assertEquals(1, record.get("result"));
        });
  }

  @Test
  void testCurrentTimestampQuery() {
    TestDatabaseConfig.withDslContext(
        sqlContext -> {
          final Record record =
              sqlContext.select(DSL.currentTimestamp().as("current_time")).fetchOne();
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

  // Just to show how to use CTEs
  @Test
  void testCustomersWithoutOrdersWithId() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var noOrdersCTE =
              DSL.name("NoOrders")
                  .fields("id", "full_name")
                  .as(
                      DSL.select(CUSTOMERS.ID, CUSTOMERS.FULL_NAME)
                          .from(CUSTOMERS)
                          .leftJoin(ORDERS)
                          .on(CUSTOMERS.ID.eq(ORDERS.CUSTOMER_ID))
                          .where(ORDERS.ID.isNull()));

          final var result =
              dsl.with(noOrdersCTE)
                  .select(
                      DSL.field(DSL.name("NoOrders", "id"), Long.class),
                      DSL.field(DSL.name("NoOrders", "full_name"), String.class))
                  .from(DSL.table(DSL.name("NoOrders")))
                  .fetch();

          assertFalse(result.isEmpty(), "Expected customers without orders");

          result.forEach(
              record -> {
                final Long id = record.get("id", Long.class);
                final String name = record.get("full_name", String.class);

                System.out.println("Customer without orders: [" + id + "] " + name);

                assertNotNull(id);
                assertNotNull(name);
              });
        });
  }

  // This test demonstrates how to use the multiset feature in jOOQ to fetch a list of orders for
  // each customer. Mapping to custom DTO is done
  @Test
  void testFetchCustomersWithOrdersMultiset() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(
                      CUSTOMERS.ID,
                      CUSTOMERS.FULL_NAME,
                      DSL.multiset(
                              DSL.select(ORDERS.ID, ORDERS.ORDER_DATE, ORDERS.TOTAL_AMOUNT)
                                  .from(ORDERS)
                                  .where(ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID)))
                          .convertFrom(
                              orderRecords ->
                                  orderRecords.map(
                                      r ->
                                          new OrderDto(
                                              r.get(ORDERS.ID),
                                              r.get(ORDERS.ORDER_DATE),
                                              r.get(ORDERS.TOTAL_AMOUNT))))
                          .as("orders"))
                  .from(CUSTOMERS)
                  .fetch()
                  .map(
                      record ->
                          new CustomerWithOrdersDto(
                              record.get(CUSTOMERS.ID),
                              record.get(CUSTOMERS.FULL_NAME),
                              record.get("orders", List.class) // returns List<OrderDto>
                              ));

          assertFalse(results.isEmpty());
          results.forEach(
              customer -> {
                assertNotNull(customer.id());
                assertNotNull(customer.fullName());
                assertNotNull(customer.orders());

                // Optional: print or assert something about orders
                System.out.println(customer.fullName() + " → orders: " + customer.orders().size());
              });
        });
  }
}
