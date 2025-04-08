package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

public class V7__CreateEmployeesSchema extends BaseJavaMigration {
  @Override
  public void migrate(Context context) {
    final DSLContext dsl = DSL.using(context.getConnection(), SQLDialect.POSTGRES);

    final var employee = DSL.name("Employee");

    dsl.createTableIfNotExists(employee)
        .column(DSL.name("id"), SQLDataType.INTEGER.nullable(false))
        .column(DSL.name("name"), SQLDataType.VARCHAR(100).nullable(false))
        .column(DSL.name("salary"), SQLDataType.INTEGER.nullable(false))
        .column(DSL.name("managerId"), SQLDataType.INTEGER)
        .constraints(
            DSL.constraint("pk_employee").primaryKey(DSL.name("id")),
            DSL.constraint("fk_manager_id")
                .foreignKey(DSL.name("managerId"))
                .references(employee, DSL.name("id")))
        .execute();

    dsl.insertInto(DSL.table(employee))
        .columns(
            DSL.field(DSL.name("id")),
            DSL.field(DSL.name("name")),
            DSL.field(DSL.name("salary")),
            DSL.field(DSL.name("managerId")))
        .values(1, "Joe", 70000, 3)
        .values(2, "Henry", 80000, 4)
        .values(3, "Sam", 60000, null)
        .values(4, "Max", 90000, null)
        .execute();
  }
}
