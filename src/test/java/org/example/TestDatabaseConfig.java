package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestDatabaseConfig {

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
