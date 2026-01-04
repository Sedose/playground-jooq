package db.migration

import org.flywaydb.core.api.migration.BaseJavaMigration
import org.flywaydb.core.api.migration.Context
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import java.math.BigDecimal

@Suppress("ClassName")
class V3__InsertInitialData : BaseJavaMigration() {
  override fun migrate(context: Context) {
    val dslContext = DSL.using(context.connection, SQLDialect.POSTGRES)

    dslContext
      .insertInto(DSL.table("customer"))
      .columns(DSL.field("email"), DSL.field("full_name"))
      .values("john.doe@example.com", "John Doe")
      .values("jane.smith@example.com", "Jane Smith")
      .execute()

    dslContext
      .insertInto(DSL.table("category"))
      .columns(DSL.field("name"))
      .values("Books")
      .values("Electronics")
      .execute()

    dslContext
      .insertInto(DSL.table("product"))
      .columns(DSL.field("name"), DSL.field("description"), DSL.field("price"))
      .values("Clean Code", "A book about writing cleaner code", BigDecimal.valueOf(29.99))
      .values("Smartphone XYZ", "A phone that does stuff", BigDecimal.valueOf(499.99))
      .execute()
  }
}
