package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class V6__InsertLeetcodeData extends BaseJavaMigration {
  @Override
  public void migrate(Context context) {
    final DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

    final var personTable = DSL.name("leetcode", "Person");
    final var addressTable = DSL.name("leetcode", "Address");

    dsl.insertInto(DSL.table(personTable))
        .columns(
            DSL.field(DSL.name("personId")),
            DSL.field(DSL.name("lastName")),
            DSL.field(DSL.name("firstName")))
        .values(1, "Wang", "Allen")
        .values(2, "Alice", "Bob")
        .execute();

    dsl.insertInto(DSL.table(addressTable))
        .columns(
            DSL.field(DSL.name("addressId")),
            DSL.field(DSL.name("personId")),
            DSL.field(DSL.name("city")),
            DSL.field(DSL.name("state")))
        .values(1, 1, "New York City", "New York")
        .values(2, 2, "Leetcode", "California")
        .execute();
  }
}
