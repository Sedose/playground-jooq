package org.example

import org.jooq.DSLContext
import org.jooq.ExecuteContext
import org.jooq.ExecuteListener
import org.jooq.SQLDialect
import org.jooq.conf.Settings
import org.jooq.impl.DSL
import org.jooq.impl.DefaultConfiguration
import org.jooq.impl.DefaultExecuteListenerProvider
import java.io.IOException
import java.sql.DriverManager
import java.util.Properties

object TestDatabaseConfig {
  fun withDslContext(testLogic: (DSLContext) -> Unit) {
    val config = load()
    DriverManager.getConnection(config.url, config.username, config.password).use { connection ->
      val settings = Settings().withExecuteLogging(true)
      val configuration = DefaultConfiguration()
      configuration.set(connection)
      configuration.set(SQLDialect.POSTGRES)
      configuration.set(settings)
      configuration.set(DefaultExecuteListenerProvider(SqlPrintListener()))
      val sqlContext = DSL.using(configuration)
      testLogic(sqlContext)
    }
  }

  fun load(): DatabaseConnectionSettings {
    val input =
      TestDatabaseConfig::class.java.classLoader.getResourceAsStream("application.properties")
        ?: throw RuntimeException("Unable to find application.properties")

    val properties = Properties()
    try {
      input.use { properties.load(it) }
    } catch (e: IOException) {
      throw RuntimeException("Failed to load database config", e)
    }

    val url = properties.getProperty("jdbc.url")
    val username = properties.getProperty("jdbc.username")
    val password = properties.getProperty("jdbc.password")
    return DatabaseConnectionSettings(url, username, password)
  }

  data class DatabaseConnectionSettings(
    val url: String,
    val username: String,
    val password: String,
  )

  private class SqlPrintListener : ExecuteListener {
    override fun renderEnd(ctx: ExecuteContext) {
      val sql = ctx.sql()
      if (sql != null) {
        println("jOOQ SQL: $sql")
      }
    }
  }
}
