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

import java.sql.Timestamp;
import java.util.List;
import org.jooq.Records;
import org.jooq.generated.tables.records.CategoryRecord;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.Test;

public class MainTest {

  @Test
  void testSimpleSelectQuery() {
    TestDatabaseConfig.withDslContext(
        dslContext -> {
          final Integer result = dslContext.select(DSL.val(1)).fetchOne(0, Integer.class);
          assertEquals(1, result);
        });
  }

  @Test
  void testCurrentTimestampQuery() {
    TestDatabaseConfig.withDslContext(
        dslContext -> {
          final Timestamp currentTime =
              dslContext.select(DSL.currentTimestamp()).fetchOne(0, Timestamp.class);
          System.out.println("Current time: " + currentTime);
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
                  .fetch(Records.mapping(OrderWithCustomerDetails::new));

          assertFalse(results.isEmpty());

          results.forEach(
              order -> {
                assertNotNull(order.fullName());
                assertNotNull(order.orderDate());
              });
        });
  }

  @Test
  void testFetchProductsWithCategories() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(PRODUCT.PRODUCT_ID, PRODUCT.NAME, CATEGORY.NAME)
                  .from(PRODUCT)
                  .join(PRODUCT_CATEGORY)
                  .on(PRODUCT.PRODUCT_ID.eq(PRODUCT_CATEGORY.PRODUCT_ID))
                  .join(CATEGORY)
                  .on(PRODUCT_CATEGORY.CATEGORY_ID.eq(CATEGORY.CATEGORY_ID))
                  .fetch(Records.mapping(ProductWithCategory::new));

          assertFalse(results.isEmpty());
          results.forEach(
              product -> {
                assertNotNull(product.productName());
                assertNotNull(product.categoryName());
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
                      PRODUCT.NAME,
                      ORDER_ITEM.QUANTITY,
                      ORDER_ITEM.UNIT_PRICE)
                  .from(ORDER_ITEM)
                  .join(PRODUCT)
                  .on(ORDER_ITEM.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
                  .fetch(Records.mapping(OrderItemWithProductDetails::new));

          assertFalse(results.isEmpty());
          results.forEach(
              item -> {
                assertNotNull(item.productName());
                assertNotNull(item.quantity());
              });
        });
  }

  @Test
  void testTotalOrderAmountPerCustomer() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CUSTOMER.FULL_NAME, DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT))
                  .from(CUSTOMER)
                  .join(CUSTOMER_ORDER)
                  .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
                  .groupBy(CUSTOMER.FULL_NAME)
                  .fetch(Records.mapping(TotalOrderAmountPerCustomer::new));

          assertFalse(results.isEmpty());
          results.forEach(
              total -> {
                assertNotNull(total.fullName());
                assertNotNull(total.totalSpent());
              });
        });
  }

  @Test
  void testProductCountPerCategory() {
    TestDatabaseConfig.withDslContext(
        dsl -> {
          final var results =
              dsl.select(CATEGORY.NAME, DSL.count(PRODUCT.PRODUCT_ID))
                  .from(CATEGORY)
                  .join(PRODUCT_CATEGORY)
                  .on(CATEGORY.CATEGORY_ID.eq(PRODUCT_CATEGORY.CATEGORY_ID))
                  .join(PRODUCT)
                  .on(PRODUCT_CATEGORY.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
                  .groupBy(CATEGORY.NAME)
                  .fetch(Records.mapping(ProductCountPerCategory::new));

          assertFalse(results.isEmpty());
          results.forEach(
              category -> {
                assertNotNull(category.categoryName());
                assertTrue(category.productCount() > 0);
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
                      DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT))
                  .from(CUSTOMER_ORDER)
                  .join(CUSTOMER)
                  .on(CUSTOMER_ORDER.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID))
                  .groupBy(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME)
                  .orderBy(DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT).desc())
                  .limit(2)
                  .fetch(Records.mapping(TopCustomerBySpend::new));

          assertEquals(2, results.size());
          results.forEach(
              customer -> {
                assertNotNull(customer.fullName());
                assertNotNull(customer.totalSpent());
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
                      CUSTOMER_ORDER.CUSTOMER_ORDER_ID,
                      CUSTOMER_ORDER.ORDER_DATE,
                      CUSTOMER_ORDER.TOTAL_AMOUNT)
                  .from(CUSTOMER)
                  .leftJoin(CUSTOMER_ORDER)
                  .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
                  .fetch(Records.mapping(CustomerOrderSummary::new));

          assertFalse(results.isEmpty());

          results.forEach(summary -> assertNotNull(summary.fullName()));
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
                  .fetch(Records.mapping(CustomerWithoutOrder::new));

          assertFalse(result.isEmpty(), "Expected customers without orders");

          result.forEach(
              customer -> {
                assertNotNull(customer.customerId());
                assertNotNull(customer.fullName());
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
                  .fetch(Records.mapping(PersonAddressDetails::new));

          assertFalse(results.isEmpty());

          results.forEach(
              person -> {
                assertNotNull(person.firstName());
                assertNotNull(person.lastName());
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
              dsl.select(subordinate.NAME)
                  .from(subordinate)
                  .join(manager)
                  .on(subordinate.MANAGER_ID.eq(manager.EMPLOYEE_ID))
                  .where(subordinate.SALARY.gt(manager.SALARY))
                  .fetch(Records.mapping(SubordinateName::new));

          final List<String> employeeNames = results.stream().map(SubordinateName::name).toList();

          assertEquals(1, employeeNames.size(), "Expected exactly one subordinate");
          assertTrue(
              employeeNames.contains("Joe"),
              "Expected 'Joe' to be the subordinate earning more than their manager");
        });
  }

}
