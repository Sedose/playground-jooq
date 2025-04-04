package org.example;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.function.Consumer;

public class TestDatabaseConfig {

  public static void withDslContext(Consumer<DSLContext> testLogic) throws Exception {
    final var config = TestDatabaseConfig.load();
    try (final Connection connection =
             DriverManager.getConnection(config.url(), config.username(), config.password())) {
      final DSLContext sqlContext = DSL.using(connection, SQLDialect.POSTGRES);
      testLogic.accept(sqlContext);
    }
  }

  public static DatabaseConnectionSettings load() {
    try (final InputStream input =
        TestDatabaseConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
      final Properties properties = new Properties();
      if (input == null) {
        throw new RuntimeException("Unable to find application.properties");
      }
      properties.load(input);
      final String url = properties.getProperty("jdbc.url");
      final String username = properties.getProperty("jdbc.username");
      final String password = properties.getProperty("jdbc.password");
      return new DatabaseConnectionSettings(url, username, password);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load database config", e);
    }
  }

  public record DatabaseConnectionSettings(String url, String username, String password) {}
}
