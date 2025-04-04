package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.math.BigDecimal;
import java.util.List;

public class V4__InsertMoreEcommerceData extends BaseJavaMigration {

	@Override
	public void migrate(Context context) {
		final DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

		final List<String> extraCategories = List.of("Computers", "Games");
		extraCategories.forEach(category -> dslContext.insertInto(DSL.table("categories"), DSL.field("name"))
				.values(category).execute());

		final Record insertedCustomer1 = dslContext
				.insertInto(DSL.table("customers"), DSL.field("email"), DSL.field("full_name"))
				.values("alice@example.com", "Alice Wonderland").returning(DSL.field("id")).fetchOne();

		final Record insertedCustomer2 = dslContext
				.insertInto(DSL.table("customers"), DSL.field("email"), DSL.field("full_name"))
				.values("bob@example.com", "Bob Builder").returning(DSL.field("id")).fetchOne();

		final Record laptopProduct = dslContext
				.insertInto(DSL.table("products"), DSL.field("name"), DSL.field("description"), DSL.field("price"))
				.values("Laptop 2025", "Powerful laptop for everyday tasks", BigDecimal.valueOf(999.99))
				.returning(DSL.field("id"), DSL.field("price")).fetchOne();

		final Record gamingConsoleProduct = dslContext
				.insertInto(DSL.table("products"), DSL.field("name"), DSL.field("description"), DSL.field("price"))
				.values("Game Console X", "Next generation gaming console", BigDecimal.valueOf(299.99))
				.returning(DSL.field("id"), DSL.field("price")).fetchOne();

		final Record computersCategoryRecord = dslContext.select(DSL.field("id")).from(DSL.table("categories"))
				.where(DSL.field("name").eq("Computers")).fetchOne();
		final Long computersCategoryId = computersCategoryRecord != null
				? computersCategoryRecord.get("id", Long.class)
				: null;

		final Record gamesCategoryRecord = dslContext.select(DSL.field("id")).from(DSL.table("categories"))
				.where(DSL.field("name").eq("Games")).fetchOne();
		final Long gamesCategoryId = gamesCategoryRecord != null ? gamesCategoryRecord.get("id", Long.class) : null;

		if (laptopProduct != null && computersCategoryId != null) {
			dslContext.insertInto(DSL.table("product_categories"), DSL.field("product_id"), DSL.field("category_id"))
					.values(laptopProduct.get("id", Long.class), computersCategoryId).execute();
		}

		if (gamingConsoleProduct != null && gamesCategoryId != null) {
			dslContext.insertInto(DSL.table("product_categories"), DSL.field("product_id"), DSL.field("category_id"))
					.values(gamingConsoleProduct.get("id", Long.class), gamesCategoryId).execute();
		}

		if (insertedCustomer1 != null && laptopProduct != null) {
			final Record createdOrder = dslContext
					.insertInto(DSL.table("orders"), DSL.field("customer_id"), DSL.field("total_amount"))
					.values(insertedCustomer1.get("id", Long.class), BigDecimal.valueOf(1299.98))
					.returning(DSL.field("id")).fetchOne();

			if (createdOrder != null) {
				dslContext
						.insertInto(DSL.table("order_items"), DSL.field("order_id"), DSL.field("product_id"),
								DSL.field("quantity"), DSL.field("unit_price"))
						.values(createdOrder.get("id", Long.class), laptopProduct.get("id", Long.class), 1,
								laptopProduct.get("price", BigDecimal.class))
						.execute();
			}
		}

		if (insertedCustomer2 != null && gamingConsoleProduct != null) {
			final Record anotherOrder = dslContext
					.insertInto(DSL.table("orders"), DSL.field("customer_id"), DSL.field("total_amount"))
					.values(insertedCustomer2.get("id", Long.class),
							gamingConsoleProduct.get("price", BigDecimal.class))
					.returning(DSL.field("id")).fetchOne();

			if (anotherOrder != null) {
				dslContext
						.insertInto(DSL.table("order_items"), DSL.field("order_id"), DSL.field("product_id"),
								DSL.field("quantity"), DSL.field("unit_price"))
						.values(anotherOrder.get("id", Long.class), gamingConsoleProduct.get("id", Long.class), 1,
								gamingConsoleProduct.get("price", BigDecimal.class))
						.execute();
			}
		}
	}
}
