import nu.studer.gradle.jooq.JooqEdition
import java.util.Properties
import java.nio.file.Files
import java.nio.file.StandardOpenOption

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

private val loadedDatabaseProperties =
  Properties().apply {
    file("src/test/resources/application.properties")
      .inputStream()
      .use { load(it) }
  }

val databaseSettings =
  mapOf(
    "url" to loadedDatabaseProperties.getProperty("jdbc.url"),
    "username" to loadedDatabaseProperties.getProperty("jdbc.username"),
    "password" to loadedDatabaseProperties.getProperty("jdbc.password"),
  )

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
  url = databaseSettings["url"]
  user = databaseSettings["username"]
  password = databaseSettings["password"]
  driver = "org.postgresql.Driver"
  locations = arrayOf("classpath:db/migration")
  cleanDisabled = false // dangerous in prod env - it drops tables in DB
}

jooq {
  edition.set(JooqEdition.OSS)

  configurations {
    create("main") {
      generateSchemaSourceOnCompilation.set(false)
      jooqConfiguration.apply {
        jdbc.apply {
          driver = "org.postgresql.Driver"
          url = databaseSettings["url"]
          user = databaseSettings["username"]
          password = databaseSettings["password"]
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

val generateJooq by tasks.existing

tasks.named<JavaCompile>("compileJava") {
  dependsOn(generateJooq)
}

sourceSets["main"].java.srcDir(
  layout.buildDirectory.dir("generated-src/jooq/main")
)

tasks.register("ciPipeline") {
  dependsOn(
    "clean",
    "flywayClean",
    "spotlessApply",
    "assemble",
    "flywayMigrate",
    "generateJooq",
    "test",
  )
}

tasks.register("copySourcesToClipboard") {
    group = "custom"
    description = "Collects project files and copies them to clipboard"

    doLast {
        val projectRoot = project.projectDir.toPath()

        val javaFiles =
            Files.walk(projectRoot.resolve("src"))
                .filter { it.toString().endsWith(".java") }
                .toList()

        val extraFiles =
            listOf("README.md", "AGENTS.md", "build.gradle.kts")
                .map { projectRoot.resolve(it) }
                .filter { Files.exists(it) }

        val allFiles = javaFiles + extraFiles

        val combined =
            allFiles.joinToString(System.lineSeparator().repeat(2)) { path ->
                Files.readString(path)
            }

        val tmpFile = Files.createTempFile("collected", ".txt")
        Files.writeString(tmpFile, combined, StandardOpenOption.TRUNCATE_EXISTING)

        exec {
            commandLine("clip.exe")
            standardInput = tmpFile.toFile().inputStream()
        }

        println("Copied ${allFiles.size} files to clipboard.")
    }
}
