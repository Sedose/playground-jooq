package db.migration;

import java.math.BigDecimal;
import java.util.List;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class V4__InsertMoreEcommerceData extends BaseJavaMigration {

  @Override
  public void migrate(Context context) {
    final DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

    final List<String> extraCategories = List.of("Computers", "Games");
    extraCategories.forEach(
        category ->
            dslContext
                .insertInto(DSL.table("category"), DSL.field("name"))
                .values(category)
                .execute());

    final Record insertedCustomer1 =
        dslContext
            .insertInto(DSL.table("customer"), DSL.field("email"), DSL.field("full_name"))
            .values("alice@example.com", "Alice Wonderland")
            .returning(DSL.field("customer_id"))
            .fetchOne();

    final Record insertedCustomer2 =
        dslContext
            .insertInto(DSL.table("customer"), DSL.field("email"), DSL.field("full_name"))
            .values("bob@example.com", "Bob Builder")
            .returning(DSL.field("customer_id"))
            .fetchOne();

    final Record laptopProduct =
        dslContext
            .insertInto(
                DSL.table("product"),
                DSL.field("name"),
                DSL.field("description"),
                DSL.field("price"))
            .values("Laptop 2025", "Powerful laptop for everyday tasks", BigDecimal.valueOf(999.99))
            .returning(DSL.field("product_id"), DSL.field("price"))
            .fetchOne();

    final Record gamingConsoleProduct =
        dslContext
            .insertInto(
                DSL.table("product"),
                DSL.field("name"),
                DSL.field("description"),
                DSL.field("price"))
            .values("Game Console X", "Next generation gaming console", BigDecimal.valueOf(299.99))
            .returning(DSL.field("product_id"), DSL.field("price"))
            .fetchOne();

    final Record computersCategoryRecord =
        dslContext
            .select(DSL.field("category_id"))
            .from(DSL.table("category"))
            .where(DSL.field("name").eq("Computers"))
            .fetchOne();
    final Long computersCategoryId =
        computersCategoryRecord != null
            ? computersCategoryRecord.get("category_id", Long.class)
            : null;

    final Record gamesCategoryRecord =
        dslContext
            .select(DSL.field("category_id"))
            .from(DSL.table("category"))
            .where(DSL.field("name").eq("Games"))
            .fetchOne();
    final Long gamesCategoryId =
        gamesCategoryRecord != null ? gamesCategoryRecord.get("category_id", Long.class) : null;

    dslContext
        .insertInto(
            DSL.table("product_category"), DSL.field("product_id"), DSL.field("category_id"))
        .values(laptopProduct.get("product_id", Long.class), computersCategoryId)
        .execute();

    dslContext
        .insertInto(
            DSL.table("product_category"), DSL.field("product_id"), DSL.field("category_id"))
        .values(gamingConsoleProduct.get("product_id", Long.class), gamesCategoryId)
        .execute();

    final Record createdOrder =
        dslContext
            .insertInto(
                DSL.table("customer_order"), DSL.field("customer_id"), DSL.field("total_amount"))
            .values(insertedCustomer1.get("customer_id", Long.class), BigDecimal.valueOf(1299.98))
            .returning(DSL.field("customer_order_id"))
            .fetchOne();

    dslContext
        .insertInto(
            DSL.table("order_item"),
            DSL.field("customer_order_id"),
            DSL.field("product_id"),
            DSL.field("quantity"),
            DSL.field("unit_price"))
        .values(
            createdOrder.get("customer_order_id", Long.class),
            laptopProduct.get("product_id", Long.class),
            1,
            laptopProduct.get("price", BigDecimal.class))
        .execute();

    final Record anotherOrder =
        dslContext
            .insertInto(
                DSL.table("customer_order"), DSL.field("customer_id"), DSL.field("total_amount"))
            .values(
                insertedCustomer2.get("customer_id", Long.class),
                gamingConsoleProduct.get("price", BigDecimal.class))
            .returning(DSL.field("customer_order_id"))
            .fetchOne();

    dslContext
        .insertInto(
            DSL.table("order_item"),
            DSL.field("customer_order_id"),
            DSL.field("product_id"),
            DSL.field("quantity"),
            DSL.field("unit_price"))
        .values(
            anotherOrder.get("customer_order_id", Long.class),
            gamingConsoleProduct.get("product_id", Long.class),
            1,
            gamingConsoleProduct.get("price", BigDecimal.class))
        .execute();
  }
}
