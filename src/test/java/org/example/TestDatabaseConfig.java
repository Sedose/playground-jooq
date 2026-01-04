package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.function.Consumer;
import lombok.SneakyThrows;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.DefaultExecuteListenerProvider;
import org.jooq.impl.DSL;

public class TestDatabaseConfig {

  @SneakyThrows
  public static void withDslContext(Consumer<DSLContext> testLogic) {
    final var config = load();
    try (final Connection connection =
        DriverManager.getConnection(config.url(), config.username(), config.password())) {
      final Settings settings = new Settings().withExecuteLogging(true);
      final DefaultConfiguration configuration = new DefaultConfiguration();
      configuration.set(connection);
      configuration.set(SQLDialect.POSTGRES);
      configuration.set(settings);
      configuration.set(new DefaultExecuteListenerProvider(new SqlPrintListener()));
      final DSLContext sqlContext = DSL.using(configuration);
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

  private static final class SqlPrintListener implements ExecuteListener {
    @Override
    public void renderEnd(ExecuteContext ctx) {
      if (ctx.sql() != null) {
        System.out.println("jOOQ SQL: " + ctx.sql());
      }
    }
  }
}
