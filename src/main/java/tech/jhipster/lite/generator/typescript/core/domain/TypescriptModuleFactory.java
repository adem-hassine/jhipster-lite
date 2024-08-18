package tech.jhipster.lite.generator.typescript.core.domain;

import static tech.jhipster.lite.module.domain.JHipsterModule.*;
import static tech.jhipster.lite.module.domain.packagejson.VersionSource.COMMON;

import tech.jhipster.lite.module.domain.JHipsterModule;
import tech.jhipster.lite.module.domain.file.JHipsterSource;
import tech.jhipster.lite.module.domain.properties.JHipsterModuleProperties;
import tech.jhipster.lite.shared.error.domain.Assert;

public class TypescriptModuleFactory {

  private static final JHipsterSource SOURCE = from("typescript");

  public JHipsterModule buildModule(JHipsterModuleProperties properties) {
    Assert.notNull("properties", properties);

    //@formatter:off
    return moduleBuilder(properties)
      .packageJson()
        .addType("module")
        .addDevDependency(packageName("typescript"), COMMON)
        .addDevDependency(packageName("@tsconfig/recommended"), COMMON)
        .addDevDependency(packageName("@typescript-eslint/eslint-plugin"), COMMON)
        .addDevDependency(packageName("@typescript-eslint/parser"), COMMON)
        .addDevDependency(packageName("@vitest/coverage-istanbul"), COMMON)
        .addDevDependency(packageName("eslint"), COMMON)
        .addDevDependency(packageName("eslint-import-resolver-typescript"), COMMON)
        .addDevDependency(packageName("eslint-plugin-import"), COMMON)
        .addDevDependency(packageName("eslint-plugin-prettier"), COMMON)
        .addDevDependency(packageName("vite-tsconfig-paths"), COMMON)
        .addDevDependency(packageName("vitest"), COMMON)
        .addDevDependency(packageName("vitest-sonar-reporter"), COMMON)
        .addScript(scriptKey("lint"), scriptCommand("eslint --ext .js,.ts,.tsx src/"))
        .addScript(scriptKey("test"), scriptCommand("npm run test:watch"))
        .addScript(scriptKey("test:coverage"), scriptCommand("vitest run --coverage"))
        .addScript(scriptKey("test:watch"), scriptCommand("vitest --"))
        .and()
      .files()
        .batch(SOURCE, to("."))
          .addFile("tsconfig.json")
          .addTemplate("vitest.config.ts")
          .addFile(".eslintrc.cjs")
          .and()
        .and()
      .build();
    //@formatter:on
  }
}
