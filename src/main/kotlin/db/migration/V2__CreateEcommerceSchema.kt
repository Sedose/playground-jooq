package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType

@Suppress("ClassName")
class V2__CreateEcommerceSchema : BaseJavaMigration() {
  override fun migrate(context: Context) {
    val dslContext = DSL.using(context.connection, SQLDialect.POSTGRES)

    dslContext
      .createTableIfNotExists("customer")
      .column("customer_id", SQLDataType.BIGINT.nullable(false).identity(true))
      .column("email", SQLDataType.VARCHAR(255).nullable(false))
      .column("full_name", SQLDataType.VARCHAR(255).nullable(false))
      .column(
        "created_at",
        SQLDataType.TIMESTAMP
          .nullable(false)
          .defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)),
      ).constraints(DSL.constraint("pk_customer").primaryKey("customer_id"))
      .execute()

    dslContext
      .createTableIfNotExists("product")
      .column("product_id", SQLDataType.BIGINT.nullable(false).identity(true))
      .column("name", SQLDataType.VARCHAR(255).nullable(false))
      .column("description", SQLDataType.CLOB)
      .column("price", SQLDataType.DECIMAL(10, 2).nullable(false))
      .column(
        "created_at",
        SQLDataType.TIMESTAMP
          .nullable(false)
          .defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)),
      ).constraints(DSL.constraint("pk_product").primaryKey("product_id"))
      .execute()

    dslContext
      .createTableIfNotExists("category")
      .column("category_id", SQLDataType.BIGINT.nullable(false).identity(true))
      .column("name", SQLDataType.VARCHAR(100).nullable(false))
      .constraints(DSL.constraint("pk_category").primaryKey("category_id"))
      .execute()

    dslContext
      .createTableIfNotExists("product_category")
      .column("product_id", SQLDataType.BIGINT.nullable(false))
      .column("category_id", SQLDataType.BIGINT.nullable(false))
      .constraints(
        DSL.constraint("pk_product_category").primaryKey("product_id", "category_id"),
        DSL
          .constraint("fk_product_category_product_id")
          .foreignKey("product_id")
          .references("product", "product_id"),
        DSL
          .constraint("fk_product_category_category_id")
          .foreignKey("category_id")
          .references("category", "category_id"),
      ).execute()

    dslContext
      .createTableIfNotExists("customer_order")
      .column("customer_order_id", SQLDataType.BIGINT.nullable(false).identity(true))
      .column("customer_id", SQLDataType.BIGINT.nullable(false))
      .column(
        "order_date",
        SQLDataType.TIMESTAMP
          .nullable(false)
          .defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)),
      ).column("total_amount", SQLDataType.DECIMAL(10, 2).nullable(false))
      .constraints(
        DSL.constraint("pk_customer_order").primaryKey("customer_order_id"),
        DSL
          .constraint("fk_customer_order_customer_id")
          .foreignKey("customer_id")
          .references("customer", "customer_id"),
      ).execute()

    dslContext
      .createTableIfNotExists("order_item")
      .column("order_item_id", SQLDataType.BIGINT.nullable(false).identity(true))
      .column("customer_order_id", SQLDataType.BIGINT.nullable(false))
      .column("product_id", SQLDataType.BIGINT.nullable(false))
      .column("quantity", SQLDataType.INTEGER.nullable(false))
      .column("unit_price", SQLDataType.DECIMAL(10, 2).nullable(false))
      .constraints(
        DSL.constraint("pk_order_item").primaryKey("order_item_id"),
        DSL
          .constraint("fk_order_item_customer_order_id")
          .foreignKey("customer_order_id")
          .references("customer_order", "customer_order_id"),
        DSL
          .constraint("fk_order_item_product_id")
          .foreignKey("product_id")
          .references("product", "product_id"),
      ).execute()
  }
}
