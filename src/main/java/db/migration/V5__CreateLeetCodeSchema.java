package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class V5__CreateLeetCodeSchema extends BaseJavaMigration {
  @Override
  public void migrate(Context context) {
    DSLContext dslContext = DSL.using(context.getConnection(), SQLDialect.POSTGRES);
    dslContext.execute("CREATE SCHEMA IF NOT EXISTS leetcode");
    dslContext
        .createTableIfNotExists(DSL.name("leetcode", "Person"))
        .column("personId", SQLDataType.INTEGER.nullable(false))
        .column("lastName", SQLDataType.VARCHAR(100).nullable(false))
        .column("firstName", SQLDataType.VARCHAR(100).nullable(false))
        .constraints(DSL.constraint("pk_person").primaryKey("personId"))
        .execute();
    dslContext
        .createTableIfNotExists(DSL.name("leetcode", "Address"))
        .column("addressId", SQLDataType.INTEGER.nullable(false))
        .column("personId", SQLDataType.INTEGER.nullable(false))
        .column("city", SQLDataType.VARCHAR(100))
        .column("state", SQLDataType.VARCHAR(100))
        .constraints(
            DSL.constraint("pk_address").primaryKey("addressId"),
            DSL.constraint("fk_address_person")
                .foreignKey("personId")
                .references(DSL.name("leetcode", "Person"), DSL.name("personId")))
        .execute();
  }
}
