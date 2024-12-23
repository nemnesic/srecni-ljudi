plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	id("org.springframework.boot") version "3.4.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.nemnesic"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springAiVersion"] = "1.0.0-M4"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	// https://mvnrepository.com/artifact/org.jgrapht/jgrapht-core
	implementation("org.jgrapht:jgrapht-core:1.5.2")
	implementation ("org.jfree:jfreechart:1.5.3")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// https://mvnrepository.com/artifact/dev.langchain4j/langchain4j
	implementation("dev.langchain4j:langchain4j-spring-boot-starter:1.0.0-alpha1")
	implementation("dev.langchain4j:langchain4j-open-ai-spring-boot-starter:1.0.0-alpha1")
	developmentOnly("org.springframework.boot:spring-boot-devtools")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
	implementation("com.squareup.okhttp3:okhttp:4.9.3") // For HTTP calls
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.0") // For JSON parsing
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
