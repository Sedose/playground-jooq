package db.migration;

import java.math.BigDecimal;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class V3__InsertInitialData extends BaseJavaMigration {

  @Override
  public void migrate(Context context) {
    final DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

    dslContext
        .insertInto(DSL.table("customers"))
        .columns(DSL.field("email"), DSL.field("full_name"))
        .values("john.doe@example.com", "John Doe")
        .values("jane.smith@example.com", "Jane Smith")
        .execute();

    dslContext
        .insertInto(DSL.table("categories"))
        .columns(DSL.field("name"))
        .values("Books")
        .values("Electronics")
        .execute();

    dslContext
        .insertInto(DSL.table("products"))
        .columns(DSL.field("name"), DSL.field("description"), DSL.field("price"))
        .values("Clean Code", "A book about writing cleaner code", BigDecimal.valueOf(29.99))
        .values("Smartphone XYZ", "A phone that does stuff", BigDecimal.valueOf(499.99))
        .execute();
  }
}
