package tech.jhipster.lite.generator.server.springboot.database.mongodb.domain;

import static tech.jhipster.lite.module.domain.JHipsterModule.artifactId;
import static tech.jhipster.lite.module.domain.JHipsterModule.dockerComposeFile;
import static tech.jhipster.lite.module.domain.JHipsterModule.documentationTitle;
import static tech.jhipster.lite.module.domain.JHipsterModule.from;
import static tech.jhipster.lite.module.domain.JHipsterModule.groupId;
import static tech.jhipster.lite.module.domain.JHipsterModule.javaDependency;
import static tech.jhipster.lite.module.domain.JHipsterModule.moduleBuilder;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyKey;
import static tech.jhipster.lite.module.domain.JHipsterModule.propertyValue;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcMainDocker;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcMainJava;
import static tech.jhipster.lite.module.domain.JHipsterModule.toSrcTestJava;
import static tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope.RUNTIME;

import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.LogLevel;
import tech.jhipster.lite.module.domain.docker.DockerImages;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.javabuild.GroupId;
import tech.jhipster.lite.module.domain.javadependency.JavaDependency;
import tech.jhipster.lite.module.domain.javadependency.JavaDependencyScope;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;
import tech.jhipster.lite.shared.error.domain.Assert;

public class MongoDbModuleFactory {

  private static final JHipsterSource SOURCE = from("server/springboot/database/mongodb");
  private static final JHipsterSource MAIN_SOURCE = SOURCE.append("main");
  private static final JHipsterSource TEST_SOURCE = SOURCE.append("test");

  private static final String MONGO_SECONDARY = "wire/mongodb/infrastructure/secondary";
  private static final String REFLECTIONS_GROUP = "org.reflections";

  private static final GroupId SPRING_BOOT_GROUP = groupId("org.springframework.boot");

  private final DockerImages dockerImages;

  public MongoDbModuleFactory(DockerImages dockerImages) {
    this.dockerImages = dockerImages;
  }

  public JHipsterModule buildModule(JHipsterModuleProperties properties) {
    Assert.notNull("properties", properties);

    String packagePath = properties.packagePath();
    String packageName = properties.basePackage().get() + ".";

    //@formatter:off
    return moduleBuilder(properties)
      .documentation(documentationTitle("Mongo DB"), SOURCE.template("mongodb.md"))
      .startupCommands()
        .dockerCompose("src/main/docker/mongodb.yml")
        .and()
      .context()
        .put("mongodbDockerImage", dockerImages.get("mongo").fullName())
        .and()
      .javaDependencies()
        .addDependency(SPRING_BOOT_GROUP, artifactId("spring-boot-starter-data-mongodb"))
        .addDependency(addSpringBootDockerComposeIntegrationDependency())
        .addDependency(reflectionsDependency())
        .addDependency(testContainerDependency())
        .and()
      .files()
        .add(SOURCE.template("mongodb.yml"), toSrcMainDocker().append("mongodb.yml"))
        .batch(MAIN_SOURCE, toSrcMainJava().append(packagePath).append(MONGO_SECONDARY))
          .addTemplate("MongodbDatabaseConfiguration.java")
          .addTemplate("JSR310DateConverters.java")
          .and()
        .add(
              TEST_SOURCE.template("JSR310DateConvertersTest.java"),
              toSrcTestJava().append(packagePath).append(MONGO_SECONDARY).append("JSR310DateConvertersTest.java")
            )
        .add(TEST_SOURCE.template("TestMongoDBManager.java"), toSrcTestJava().append(packagePath).append("TestMongoDBManager.java"))
        .and()
      .springMainProperties()
        .set(propertyKey("spring.data.mongodb.database"), propertyValue(properties.projectBaseName().get()))
        .set(propertyKey("spring.data.mongodb.uri"), propertyValue("mongodb://localhost:27017/" + properties.projectBaseName().get()))
        .and()
      .springTestProperties()
        .set(propertyKey("spring.data.mongodb.uri"), propertyValue("${TEST_MONGODB_URI}"))
        .set(propertyKey("spring.docker.compose.enabled"), propertyValue(false))
        .and()
      .springTestFactories()
        .append(propertyKey("org.springframework.context.ApplicationListener"), propertyValue(packageName + "TestMongoDBManager"))
        .and()
      .dockerComposeFile()
        .append(dockerComposeFile("src/main/docker/mongodb.yml"))
        .and()
      .springMainLogger(REFLECTIONS_GROUP, LogLevel.WARN)
      .springMainLogger("org.mongodb.driver", LogLevel.WARN)
      .springTestLogger(REFLECTIONS_GROUP, LogLevel.WARN)
      .springTestLogger("org.mongodb.driver", LogLevel.WARN)
      .springTestLogger("com.github.dockerjava", LogLevel.WARN)
      .springTestLogger("org.testcontainers", LogLevel.WARN)
      .build();
    //@formatter:on
  }

  private JavaDependency testContainerDependency() {
    return javaDependency()
      .groupId("org.testcontainers")
      .artifactId("mongodb")
      .versionSlug("testcontainers")
      .scope(JavaDependencyScope.TEST)
      .build();
  }

  private JavaDependency reflectionsDependency() {
    return javaDependency().groupId(REFLECTIONS_GROUP).artifactId("reflections").versionSlug("reflections").build();
  }

  private JavaDependency addSpringBootDockerComposeIntegrationDependency() {
    return JavaDependency.builder().groupId(SPRING_BOOT_GROUP).artifactId("spring-boot-docker-compose").scope(RUNTIME).optional().build();
  }
}
