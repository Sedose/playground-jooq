import nu.studer.gradle.jooq.JooqEdition
import java.lang.System.getenv

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.flywaydb:flyway-database-postgresql:11.4.0")
  }
}

apply(plugin = "org.flywaydb.flyway")

plugins {
  java
  checkstyle
  id("com.diffplug.spotless") version "7.0.2"
  id("org.flywaydb.flyway") version "11.4.0"
  id("nu.studer.jooq") version "10.0"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation ("org.slf4j:slf4j-api:2.0.17")
  implementation ("ch.qos.logback:logback-classic:1.5.18")
  jooqGenerator("org.postgresql:postgresql:42.7.5")
  implementation("org.flywaydb:flyway-database-postgresql:11.4.0")
  implementation("org.jooq:jooq:3.20.2")
  implementation("org.jooq:jooq-meta:3.20.2")
  implementation("org.jooq:jooq-codegen:3.20.2")
  implementation("org.postgresql:postgresql:42.7.5")
  compileOnly("org.projectlombok:lombok:1.18.38")
  annotationProcessor("org.projectlombok:lombok:1.18.38")
  testCompileOnly("org.projectlombok:lombok:1.18.38")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
  testImplementation(platform("org.junit:junit-bom:5.10.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}

flyway {
  url = getenv("DB_URL")
  user = getenv("DB_USER")
  password = getenv("DB_PASSWORD")
  driver = "org.postgresql.Driver"
  locations = arrayOf("classpath:db/migration")
}

jooq {
  edition.set(JooqEdition.OSS)

  configurations {
    create("main") {
      generateSchemaSourceOnCompilation.set(false)
      jooqConfiguration.apply {
        jdbc.apply {
          driver = "org.postgresql.Driver"
          url = getenv("DB_URL")
          user = getenv("DB_USER")
          password = getenv("DB_PASSWORD")
        }
        generator.apply {
          name = "org.jooq.codegen.JavaGenerator"
          database.apply {
            name = "org.jooq.meta.postgres.PostgresDatabase"
            inputSchema = "public"
            includes = ".*"
            excludes = ""
          }
        }
      }
    }
  }
}

spotless {
  java {
    googleJavaFormat()
    target("src/**/*.java")
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }
}

checkstyle {
  toolVersion = "10.22.0"
  isShowViolations = true
}

tasks.withType<Checkstyle> {
  exclude("**/generated/**")
}
