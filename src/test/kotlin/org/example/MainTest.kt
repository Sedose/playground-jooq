package org.example

import org.example.dto.CustomerOrderSummary
import org.example.dto.CustomerWithoutOrder
import org.example.dto.OrderItemWithProductDetails
import org.example.dto.OrderWithCustomerDetails
import org.example.dto.PersonAddressDetails
import org.example.dto.ProductCountPerCategory
import org.example.dto.ProductWithCategory
import org.example.dto.SubordinateName
import org.example.dto.TopCustomerBySpend
import org.example.dto.TotalOrderAmountPerCustomer
import org.jooq.Records
import org.jooq.generated.tables.Address.Companion.ADDRESS
import org.jooq.generated.tables.Category.Companion.CATEGORY
import org.jooq.generated.tables.Customer.Companion.CUSTOMER
import org.jooq.generated.tables.CustomerOrder.Companion.CUSTOMER_ORDER
import org.jooq.generated.tables.Employee.Companion.EMPLOYEE
import org.jooq.generated.tables.FlywaySchemaHistory.Companion.FLYWAY_SCHEMA_HISTORY
import org.jooq.generated.tables.Manager.Companion.MANAGER
import org.jooq.generated.tables.OrderItem.Companion.ORDER_ITEM
import org.jooq.generated.tables.Person.Companion.PERSON
import org.jooq.generated.tables.Product.Companion.PRODUCT
import org.jooq.generated.tables.ProductCategory.Companion.PRODUCT_CATEGORY
import org.jooq.generated.tables.records.CategoryRecord
import org.jooq.impl.DSL
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.sql.Timestamp

class MainTest {
  @Test
  fun testSimpleSelectQuery() {
    TestDatabaseConfig.withDslContext { dslContext ->
      val result = dslContext.select(DSL.`val`(1)).fetchOne(0, Int::class.java)
      assertEquals(1, result)
    }
  }

  @Test
  fun testCurrentTimestampQuery() {
    TestDatabaseConfig.withDslContext { dslContext ->
      val currentTime = dslContext.select(DSL.currentTimestamp()).fetchOne(0, Timestamp::class.java)
      println("Current time: $currentTime")
    }
  }

  @Test
  fun testFetchAllCategories() {
    TestDatabaseConfig.withDslContext { dsl ->
      val categories = dsl.selectFrom(CATEGORY).fetch()
      assertTrue(categories.size >= 4)
      val categoryNames = categories.mapNotNull(CategoryRecord::name)
      assertTrue(categoryNames.containsAll(listOf("Books", "Electronics", "Computers", "Games")))
    }
  }

  @Test
  fun testFetchAllCustomers() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(CUSTOMER).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchAllFlywaySchemaHistory() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(FLYWAY_SCHEMA_HISTORY).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchAllOrderItems() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(ORDER_ITEM).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchAllOrders() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(CUSTOMER_ORDER).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchAllProductCategories() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(PRODUCT_CATEGORY).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchAllProducts() {
    TestDatabaseConfig.withDslContext { dsl ->
      val records = dsl.selectFrom(PRODUCT).fetch()
      assertFalse(records.isEmpty())
    }
  }

  @Test
  fun testFetchOrdersWithCustomerDetails() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(
            CUSTOMER_ORDER.CUSTOMER_ORDER_ID,
            CUSTOMER.FULL_NAME,
            CUSTOMER_ORDER.ORDER_DATE,
            CUSTOMER_ORDER.TOTAL_AMOUNT,
          ).from(CUSTOMER_ORDER)
          .join(CUSTOMER)
          .on(CUSTOMER_ORDER.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID))
          .fetch(Records.mapping(::OrderWithCustomerDetails))

      assertFalse(results.isEmpty())
      results.forEach { order ->
        assertNotNull(order.fullName)
        assertNotNull(order.orderDate)
      }
    }
  }

  @Test
  fun testFetchProductsWithCategories() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(PRODUCT.PRODUCT_ID, PRODUCT.NAME, CATEGORY.NAME)
          .from(PRODUCT)
          .join(PRODUCT_CATEGORY)
          .on(PRODUCT.PRODUCT_ID.eq(PRODUCT_CATEGORY.PRODUCT_ID))
          .join(CATEGORY)
          .on(PRODUCT_CATEGORY.CATEGORY_ID.eq(CATEGORY.CATEGORY_ID))
          .fetch(Records.mapping(::ProductWithCategory))

      assertFalse(results.isEmpty())
      results.forEach { product ->
        assertNotNull(product.productName)
        assertNotNull(product.categoryName)
      }
    }
  }

  @Test
  fun testFetchOrderItemsWithProductDetails() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(
            ORDER_ITEM.CUSTOMER_ORDER_ID,
            PRODUCT.NAME,
            ORDER_ITEM.QUANTITY,
            ORDER_ITEM.UNIT_PRICE,
          ).from(ORDER_ITEM)
          .join(PRODUCT)
          .on(ORDER_ITEM.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
          .fetch(Records.mapping(::OrderItemWithProductDetails))

      assertFalse(results.isEmpty())
      results.forEach { item ->
        assertNotNull(item.productName)
        assertNotNull(item.quantity)
      }
    }
  }

  @Test
  fun testTotalOrderAmountPerCustomer() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(CUSTOMER.FULL_NAME, DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT))
          .from(CUSTOMER)
          .join(CUSTOMER_ORDER)
          .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
          .groupBy(CUSTOMER.FULL_NAME)
          .fetch(Records.mapping(::TotalOrderAmountPerCustomer))

      assertFalse(results.isEmpty())
      results.forEach { total ->
        assertNotNull(total.fullName)
        assertNotNull(total.totalSpent)
      }
    }
  }

  @Test
  fun testProductCountPerCategory() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(CATEGORY.NAME, DSL.count(PRODUCT.PRODUCT_ID))
          .from(CATEGORY)
          .join(PRODUCT_CATEGORY)
          .on(CATEGORY.CATEGORY_ID.eq(PRODUCT_CATEGORY.CATEGORY_ID))
          .join(PRODUCT)
          .on(PRODUCT_CATEGORY.PRODUCT_ID.eq(PRODUCT.PRODUCT_ID))
          .groupBy(CATEGORY.NAME)
          .fetch(Records.mapping(::ProductCountPerCategory))

      assertFalse(results.isEmpty())
      results.forEach { category ->
        assertNotNull(category.categoryName)
        assertTrue(category.productCount > 0)
      }
    }
  }

  @Test
  fun testTopCustomersBySpend() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME, DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT))
          .from(CUSTOMER_ORDER)
          .join(CUSTOMER)
          .on(CUSTOMER_ORDER.CUSTOMER_ID.eq(CUSTOMER.CUSTOMER_ID))
          .groupBy(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME)
          .orderBy(DSL.sum(CUSTOMER_ORDER.TOTAL_AMOUNT).desc())
          .limit(2)
          .fetch(Records.mapping(::TopCustomerBySpend))

      assertEquals(2, results.size)
      results.forEach { customer ->
        assertNotNull(customer.fullName)
        assertNotNull(customer.totalSpent)
      }
    }
  }

  @Test
  fun testCustomersLeftJoinOrders() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(
            CUSTOMER.FULL_NAME,
            CUSTOMER_ORDER.CUSTOMER_ORDER_ID,
            CUSTOMER_ORDER.ORDER_DATE,
            CUSTOMER_ORDER.TOTAL_AMOUNT,
          ).from(CUSTOMER)
          .leftJoin(CUSTOMER_ORDER)
          .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
          .fetch(Records.mapping(::CustomerOrderSummary))

      assertFalse(results.isEmpty())

      results.forEach { summary -> assertNotNull(summary.fullName) }
    }
  }

  @Test
  fun testCustomersWithoutOrdersWithId() {
    TestDatabaseConfig.withDslContext { dsl ->
      val result =
        dsl
          .select(CUSTOMER.CUSTOMER_ID, CUSTOMER.FULL_NAME)
          .from(CUSTOMER)
          .leftJoin(CUSTOMER_ORDER)
          .on(CUSTOMER.CUSTOMER_ID.eq(CUSTOMER_ORDER.CUSTOMER_ID))
          .where(CUSTOMER_ORDER.CUSTOMER_ORDER_ID.isNull)
          .fetch(Records.mapping(::CustomerWithoutOrder))

      assertFalse(result.isEmpty(), "Expected customers without orders")

      result.forEach { customer ->
        assertNotNull(customer.customerId)
        assertNotNull(customer.fullName)
      }
    }
  }

  @Test
  fun testLeetcodePersonAddressJoin() {
    TestDatabaseConfig.withDslContext { dsl ->
      val results =
        dsl
          .select(PERSON.FIRST_NAME, PERSON.LAST_NAME, ADDRESS.CITY, ADDRESS.STATE)
          .from(PERSON)
          .leftJoin(ADDRESS)
          .using(PERSON.PERSON_ID)
          .fetch(Records.mapping(::PersonAddressDetails))

      assertFalse(results.isEmpty())

      results.forEach { person ->
        assertNotNull(person.firstName)
        assertNotNull(person.lastName)
      }
    }
  }

  @Test
  fun testSubordinatesWhoEarnMoreThanTheirManagers() {
    TestDatabaseConfig.withDslContext { dsl ->
      val subordinate = EMPLOYEE.`as`("subordinate")
      val manager = MANAGER.`as`("manager")

      val results =
        dsl
          .select(subordinate.NAME)
          .from(subordinate)
          .join(manager)
          .on(subordinate.MANAGER_ID.eq(manager.MANAGER_ID))
          .where(subordinate.SALARY.gt(manager.SALARY))
          .fetch(Records.mapping(::SubordinateName))

      val employeeNames = results.map(SubordinateName::name)

      assertEquals(1, employeeNames.size, "Expected exactly one subordinate")
      assertTrue(
        employeeNames.contains("Joe"),
        "Expected 'Joe' to be the subordinate earning more than their manager",
      )
    }
  }
}
