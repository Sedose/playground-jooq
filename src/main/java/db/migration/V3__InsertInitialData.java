package db.migration;

import static org.jooq.generated.Tables.CATEGORIES;
import static org.jooq.generated.Tables.CUSTOMERS;
import static org.jooq.generated.Tables.PRODUCTS;

import java.math.BigDecimal;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class V3__InsertInitialData extends BaseJavaMigration {
  @Override
  public void migrate(Context context) throws Exception {
    final DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

    dslContext
        .insertInto(CUSTOMERS)
        .columns(CUSTOMERS.EMAIL, CUSTOMERS.FULL_NAME)
        .values("john.doe@example.com", "John Doe")
        .values("jane.smith@example.com", "Jane Smith")
        .execute();

    dslContext
        .insertInto(CATEGORIES)
        .columns(CATEGORIES.NAME)
        .values("Books")
        .values("Electronics")
        .execute();

    dslContext
        .insertInto(PRODUCTS)
        .columns(PRODUCTS.NAME, PRODUCTS.DESCRIPTION, PRODUCTS.PRICE)
        .values("Clean Code", "A book about writing cleaner code", BigDecimal.valueOf(29.99))
        .values("Smartphone XYZ", "A phone that does stuff", BigDecimal.valueOf(499.99))
        .execute();
  }
}
