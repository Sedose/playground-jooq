package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class V2__CreateEcommerceSchema extends BaseJavaMigration {

	@Override
	public void migrate(Context context) {
		final DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

		dslContext.createTableIfNotExists("customers").column("id", SQLDataType.BIGINT.nullable(false).identity(true))
				.column("email", SQLDataType.VARCHAR(255).nullable(false))
				.column("full_name", SQLDataType.VARCHAR(255).nullable(false))
				.column("created_at",
						SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)))
				.constraints(DSL.constraint("pk_customers").primaryKey("id")).execute();

		dslContext.createTableIfNotExists("products").column("id", SQLDataType.BIGINT.nullable(false).identity(true))
				.column("name", SQLDataType.VARCHAR(255).nullable(false)).column("description", SQLDataType.CLOB)
				.column("price", SQLDataType.DECIMAL(10, 2).nullable(false))
				.column("created_at",
						SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)))
				.constraints(DSL.constraint("pk_products").primaryKey("id")).execute();

		dslContext.createTableIfNotExists("categories").column("id", SQLDataType.BIGINT.nullable(false).identity(true))
				.column("name", SQLDataType.VARCHAR(100).nullable(false))
				.constraints(DSL.constraint("pk_categories").primaryKey("id")).execute();

		dslContext.createTableIfNotExists("product_categories").column("product_id", SQLDataType.BIGINT.nullable(false))
				.column("category_id", SQLDataType.BIGINT.nullable(false))
				.constraints(DSL.constraint("pk_product_categories").primaryKey("product_id", "category_id"),
						DSL.constraint("fk_product_categories_product_id").foreignKey("product_id")
								.references("products", "id"),
						DSL.constraint("fk_product_categories_category_id").foreignKey("category_id")
								.references("categories", "id"))
				.execute();

		dslContext.createTableIfNotExists("orders").column("id", SQLDataType.BIGINT.nullable(false).identity(true))
				.column("customer_id", SQLDataType.BIGINT.nullable(false))
				.column("order_date",
						SQLDataType.TIMESTAMP.nullable(false).defaultValue(DSL.field("NOW()", SQLDataType.TIMESTAMP)))
				.column("total_amount", SQLDataType.DECIMAL(10, 2).nullable(false))
				.constraints(DSL.constraint("pk_orders").primaryKey("id"),
						DSL.constraint("fk_orders_customer_id").foreignKey("customer_id").references("customers", "id"))
				.execute();

		dslContext.createTableIfNotExists("order_items").column("id", SQLDataType.BIGINT.nullable(false).identity(true))
				.column("order_id", SQLDataType.BIGINT.nullable(false))
				.column("product_id", SQLDataType.BIGINT.nullable(false))
				.column("quantity", SQLDataType.INTEGER.nullable(false))
				.column("unit_price", SQLDataType.DECIMAL(10, 2).nullable(false))
				.constraints(DSL.constraint("pk_order_items").primaryKey("id"),
						DSL.constraint("fk_order_items_order_id").foreignKey("order_id").references("orders", "id"),
						DSL.constraint("fk_order_items_product_id").foreignKey("product_id").references("products",
								"id"))
				.execute();
	}
}
