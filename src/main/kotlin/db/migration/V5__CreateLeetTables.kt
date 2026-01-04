package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.jooq.impl.SQLDataType

@Suppress("ClassName")
class V5__CreateLeetTables : BaseJavaMigration() {
  override fun migrate(context: Context) {
    val dslContext = DSL.using(context.connection, SQLDialect.POSTGRES)
    dslContext
      .createTableIfNotExists(DSL.name("person"))
      .column("person_id", SQLDataType.INTEGER.nullable(false))
      .column("last_name", SQLDataType.VARCHAR(100).nullable(false))
      .column("first_name", SQLDataType.VARCHAR(100).nullable(false))
      .constraints(DSL.constraint("pk_person").primaryKey("person_id"))
      .execute()
    dslContext
      .createTableIfNotExists(DSL.name("address"))
      .column("address_id", SQLDataType.INTEGER.nullable(false))
      .column("person_id", SQLDataType.INTEGER.nullable(false))
      .column("city", SQLDataType.VARCHAR(100))
      .column("state", SQLDataType.VARCHAR(100))
      .constraints(
        DSL.constraint("pk_address").primaryKey("address_id"),
        DSL
          .constraint("fk_address_person")
          .foreignKey("person_id")
          .references(DSL.name("person"), DSL.name("person_id")),
      ).execute()
  }
}
