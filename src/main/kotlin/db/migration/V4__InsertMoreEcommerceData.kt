package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.math.BigDecimal

@Suppress("ClassName")
class V4__InsertMoreEcommerceData : BaseJavaMigration() {
  override fun migrate(context: Context) {
    val dslContext = DSL.using(context.connection, SQLDialect.POSTGRES)

    val extraCategories = listOf("Computers", "Games")
    extraCategories.forEach { category ->
      dslContext.insertInto(DSL.table("category"), DSL.field("name")).values(category).execute()
    }

    val insertedCustomer1 =
      requireNotNull(
        dslContext
          .insertInto(DSL.table("customer"), DSL.field("email"), DSL.field("full_name"))
          .values("alice@example.com", "Alice Wonderland")
          .returning(DSL.field("customer_id"))
          .fetchOne(),
      )

    val insertedCustomer2 =
      requireNotNull(
        dslContext
          .insertInto(DSL.table("customer"), DSL.field("email"), DSL.field("full_name"))
          .values("bob@example.com", "Bob Builder")
          .returning(DSL.field("customer_id"))
          .fetchOne(),
      )

    val laptopProduct =
      requireNotNull(
        dslContext
          .insertInto(
            DSL.table("product"),
            DSL.field("name"),
            DSL.field("description"),
            DSL.field("price"),
          ).values(
            "Laptop 2025",
            "Powerful laptop for everyday tasks",
            BigDecimal.valueOf(999.99),
          ).returning(DSL.field("product_id"), DSL.field("price"))
          .fetchOne(),
      )

    val gamingConsoleProduct =
      requireNotNull(
        dslContext
          .insertInto(
            DSL.table("product"),
            DSL.field("name"),
            DSL.field("description"),
            DSL.field("price"),
          ).values(
            "Game Console X",
            "Next generation gaming console",
            BigDecimal.valueOf(299.99),
          ).returning(DSL.field("product_id"), DSL.field("price"))
          .fetchOne(),
      )

    val computersCategoryRecord =
      dslContext
        .select(DSL.field("category_id"))
        .from(DSL.table("category"))
        .where(DSL.field("name").eq("Computers"))
        .fetchOne()
    val computersCategoryId = computersCategoryRecord?.get("category_id", Long::class.java)

    val gamesCategoryRecord =
      dslContext
        .select(DSL.field("category_id"))
        .from(DSL.table("category"))
        .where(DSL.field("name").eq("Games"))
        .fetchOne()
    val gamesCategoryId = gamesCategoryRecord?.get("category_id", Long::class.java)

    dslContext
      .insertInto(
        DSL.table("product_category"),
        DSL.field("product_id"),
        DSL.field("category_id"),
      ).values(laptopProduct.get("product_id", Long::class.java), computersCategoryId)
      .execute()

    dslContext
      .insertInto(
        DSL.table("product_category"),
        DSL.field("product_id"),
        DSL.field("category_id"),
      ).values(gamingConsoleProduct.get("product_id", Long::class.java), gamesCategoryId)
      .execute()

    val createdOrder =
      requireNotNull(
        dslContext
          .insertInto(
            DSL.table("customer_order"),
            DSL.field("customer_id"),
            DSL.field("total_amount"),
          ).values(
            insertedCustomer1.get("customer_id", Long::class.java),
            BigDecimal.valueOf(1299.98),
          ).returning(DSL.field("customer_order_id"))
          .fetchOne(),
      )

    dslContext
      .insertInto(
        DSL.table("order_item"),
        DSL.field("customer_order_id"),
        DSL.field("product_id"),
        DSL.field("quantity"),
        DSL.field("unit_price"),
      ).values(
        createdOrder.get("customer_order_id", Long::class.java),
        laptopProduct.get("product_id", Long::class.java),
        1,
        laptopProduct.get("price", BigDecimal::class.java),
      ).execute()

    val anotherOrder =
      requireNotNull(
        dslContext
          .insertInto(
            DSL.table("customer_order"),
            DSL.field("customer_id"),
            DSL.field("total_amount"),
          ).values(
            insertedCustomer2.get("customer_id", Long::class.java),
            gamingConsoleProduct.get("price", BigDecimal::class.java),
          ).returning(DSL.field("customer_order_id"))
          .fetchOne(),
      )

    dslContext
      .insertInto(
        DSL.table("order_item"),
        DSL.field("customer_order_id"),
        DSL.field("product_id"),
        DSL.field("quantity"),
        DSL.field("unit_price"),
      ).values(
        anotherOrder.get("customer_order_id", Long::class.java),
        gamingConsoleProduct.get("product_id", Long::class.java),
        1,
        gamingConsoleProduct.get("price", BigDecimal::class.java),
      ).execute()
  }
}
