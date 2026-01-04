import nu.studer.gradle.jooq.JooqEdition
import nu.studer.gradle.jooq.JooqConfig
import org.gradle.api.Action
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

buildscript {
  repositories {
    mavenCentral()
  }
  dependencies {
    classpath("org.flywaydb:flyway-database-postgresql:11.20.0")
  }
}

apply(plugin = "org.flywaydb.flyway")

plugins {
  kotlin("jvm") version "2.3.0"
  id("org.flywaydb.flyway") version "11.20.0"
  id("nu.studer.jooq") version "10.2"
  id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
}

private val loadedDatabaseProperties =
    run {
        val propertiesFile = file("src/test/resources/application.properties")

        require(propertiesFile.exists()) {
            """
              Missing application.properties. Please read README.md and follow the setup instructions to create:
              `src/test/resources/application.properties` with correct content
              """.trimIndent()
        }

        Properties().apply {
            propertiesFile.inputStream().use(::load)
        }
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
  implementation(kotlin("stdlib"))
  implementation ("org.slf4j:slf4j-api:2.0.17")
  implementation ("ch.qos.logback:logback-classic:1.5.23")
  jooqGenerator("org.postgresql:postgresql:42.7.8")
  implementation("org.flywaydb:flyway-database-postgresql:11.20.0")
  implementation("org.jooq:jooq:3.20.10")
  implementation("org.jooq:jooq-meta:3.20.10")
  implementation("org.jooq:jooq-codegen:3.20.10")
  implementation("org.postgresql:postgresql:42.7.8")
  testImplementation(platform("org.junit:junit-bom:5.14.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
  val generatedJooqDir = layout.projectDirectory.dir("src/main/generated/jooq")

  configurations {
    create(
      "main",
      object : Action<JooqConfig> {
        override fun execute(configuration: JooqConfig) {
          configuration.generateSchemaSourceOnCompilation.set(false)
          configuration.jooqConfiguration.apply {
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
              target.apply {
                directory = generatedJooqDir.asFile.path
              }
            }
          }
        }
      },
    )
  }
}

val generateJooq by tasks.existing

tasks.named("generateJooq") {
  dependsOn("flywayMigrate")
}

tasks.named("flywayMigrate") {
  dependsOn("compileKotlin")
  mustRunAfter("flywayClean")
}

tasks.named<KotlinCompile>("compileTestKotlin") {
  dependsOn(generateJooq)
}

sourceSets["main"].java.srcDir(
  layout.projectDirectory.dir("src/main/generated/jooq")
)
sourceSets["test"].java.srcDir(
  layout.projectDirectory.dir("src/main/generated/jooq")
)

tasks.register("ciPipeline") {
  dependsOn(
    "clean",
    "flywayClean",
    "assemble",
    "flywayMigrate",
    "generateJooq",
    "test",
  )
}

tasks.named("clean") {
  delete(layout.projectDirectory.dir("src/main/generated/jooq"))
}
