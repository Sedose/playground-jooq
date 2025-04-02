plugins {
  java
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jooq:jooq:3.20.2")
  implementation("org.jooq:jooq-meta:3.20.2")
  implementation("org.jooq:jooq-codegen:3.20.2")
  runtimeOnly("org.postgresql:postgresql:42.7.5")
  testImplementation(platform("org.junit:junit-bom:5.10.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
  useJUnitPlatform()
}
