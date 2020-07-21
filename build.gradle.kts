
plugins {
    java
}

tasks.test {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation("io.vertx:vertx-core:3.8.4}")
    implementation("io.vertx:vertx-web-client:3.8.4")
    implementation("io.vertx:vertx-codegen:3.8.4")

    // test
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.2")
    testImplementation("org.assertj:assertj-core:3.12.0")
    testImplementation("io.vertx:vertx-junit5:3.8.4")
    testImplementation("org.mockito:mockito-core:3.1.0")
}