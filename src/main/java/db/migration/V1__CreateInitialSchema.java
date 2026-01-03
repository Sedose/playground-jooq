package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class V1__CreateInitialSchema extends BaseJavaMigration {

  @Override
  public void migrate(Context context) {
    final DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);
    dsl.createTable("test_table")
        .column("test_table_id", SQLDataType.INTEGER.nullable(false))
        .column("name", SQLDataType.VARCHAR(100))
        .constraints(DSL.constraint("pk_test_table").primaryKey("test_table_id"))
        .execute();
  }
}
