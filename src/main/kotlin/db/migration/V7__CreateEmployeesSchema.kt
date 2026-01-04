package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType

class V7__CreateEmployeesSchema : BaseJavaMigration() {
  override fun migrate(context: Context) {
    val dsl = DSL.using(context.connection, SQLDialect.POSTGRES)

    val employee = DSL.name("employee")

    dsl.createTableIfNotExists(employee)
        .column(DSL.name("employee_id"), SQLDataType.INTEGER.nullable(false))
        .column(DSL.name("name"), SQLDataType.VARCHAR(100).nullable(false))
        .column(DSL.name("salary"), SQLDataType.INTEGER.nullable(false))
        .column(DSL.name("manager_id"), SQLDataType.INTEGER)
        .constraints(
            DSL.constraint("pk_employee").primaryKey(DSL.name("employee_id")),
            DSL.constraint("fk_manager_id")
                .foreignKey(DSL.name("manager_id"))
                .references(employee, DSL.name("employee_id")),
        )
        .execute()

    dsl.insertInto(DSL.table(employee))
        .columns(
            DSL.field(DSL.name("employee_id")),
            DSL.field(DSL.name("name")),
            DSL.field(DSL.name("salary")),
            DSL.field(DSL.name("manager_id")),
        )
        .values(1, "Joe", 70000, 3)
        .values(2, "Henry", 80000, 4)
        .values(3, "Sam", 60000, null)
        .values(4, "Max", 90000, null)
        .execute()
  }
}
