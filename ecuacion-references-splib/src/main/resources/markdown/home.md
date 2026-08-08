This is the reference page for ecuacion-splib (module name: `ecuacion-splib-xxx`).
It provides comprehensive documentation for the Spring Boot integration features of each module.

## What is ecuacion-splib?

ecuacion-splib is a Spring Boot base library built on top of ecuacion-lib, providing common
infrastructure (configuration, exception handling, security, JPA/Batch integration, a full web
application framework) so that individual applications do not need to build it from scratch.

## Module Structure

ecuacion-splib consists of the following modules.

| Module | Role |
| --- | --- |
| `ecuacion-splib-core` | Common base features shared by every other `ecuacion-splib-xxx` module (configuration, exception-handling contracts) |
| `ecuacion-splib-jpa` | JPA integration (entities, repositories) independent of the web layer |
| `ecuacion-splib-batch` | Spring Batch integration |
| `ecuacion-splib-web` | Full Spring MVC web application framework (controllers, forms, Thymeleaf/Bootstrap templates) |
| `ecuacion-splib-web-jpa` | Glue between `ecuacion-splib-web` and `ecuacion-splib-jpa` |
| `ecuacion-splib-web-markdown` | Renders Markdown files as web pages; this reference site itself is built on it |
| `ecuacion-splib-rest` | Framework for building REST APIs (exception handling, API key / public / denied endpoint security) |

This site currently covers **`ecuacion-splib-rest`** (see the **rest** menu above) and
**`ecuacion-splib-batch`** (see the **batch** menu above). Articles for the other modules will be added
over time.

`ecuacion-splib-web` has its own hands-on tutorial application (a separate site) that lets you interact
with real screens built on the framework, rather than reading Markdown articles about it.

---

## Setup

You can manage the versions of the `ecuacion-splib-xxx` modules either by importing
`ecuacion-splib-parent` as a BOM, or by using it as the parent POM. Which one you should choose
also depends on how you want to handle the Spring Boot version, and whether you want to align with
ecuacion's own build conventions (static analysis, etc.), giving four patterns overall.

### Pattern 1: Import both ecuacion-splib and Spring Boot as a BOM (recommended)

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.splib</groupId>
            <artifactId>ecuacion-splib-parent</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Because `ecuacion-splib-parent` itself imports `spring-boot-dependencies` in its
`dependencyManagement`, that import is transitively included in this BOM import too. So Spring
Boot-related dependencies (e.g. `spring-boot-starter-tomcat`) can also be added without specifying
the Spring Boot version separately.

Note, however, that plugin versions/configuration (`pluginManagement`) is not inherited via BOM
import. If you need a plugin such as `spring-boot-maven-plugin` (e.g. to build an executable jar),
add it to your own project. Spring Boot's official
[Using Spring Boot without the Parent POM](https://docs.spring.io/spring-boot/maven-plugin/using.html#using.import)
guide is a good reference.

This pattern lets you keep whatever parent POM your project already uses (e.g. an in-house shared
parent POM).

### Pattern 2: Import ecuacion-splib as a BOM, use Spring Boot as the parent POM

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>(Spring Boot version)</version>
</parent>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.splib</groupId>
            <artifactId>ecuacion-splib-parent</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

With this pattern, the Spring Boot version is no longer tied to the ecuacion-splib version. The
version in `<parent>` cannot reference a property (Maven resolves a parent POM's version before
property resolution happens), so you must specify it explicitly and keep it in sync by hand with
the Spring Boot version ecuacion-splib is built against.

### Pattern 3: Use ecuacion-splib as the parent POM

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>(version)</version>
</parent>
```

As with Pattern 1, the Spring Boot version stays tied to the ecuacion-splib version, so no explicit
version is needed. `ecuacion-splib-parent` is a thin parent POM with no real dependencies and none
of ecuacion's own build enforcement, so it can be used just as safely as Pattern 1. The one
exception is the `-parameters` compiler flag, needed for Spring 6+'s `@RequestParam` /
`@PathVariable` parameter-name resolution — since that also needs to apply to the consuming
application's own compilation, it's included here too.

If your project already has its own shared parent POM, use Pattern 1. Otherwise, this pattern is
simpler since it saves you from writing a separate BOM import.

### Pattern 4: Use ecuacion-splib-dependencies as the parent POM

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-dependencies</artifactId>
    <version>(version)</version>
</parent>
```

`ecuacion-splib-dependencies` is the parent POM used by ecuacion-splib's own modules (such as
`ecuacion-splib-core`). On top of everything in Pattern 3, it also brings in the following build
configuration:

- `spring-boot-devtools` / `spring-boot-starter-test` / `allure-jupiter` are added automatically as
  actual dependencies, not just managed versions
- NullAway / Error Prone static analysis is enforced at compile time
- Test execution settings such as `maven-surefire-plugin`'s `useModulePath=false` are overridden
- Code-quality checks via checkstyle and spotbugs, and automatic license-header insertion into source files
- `failOnMissingWebXml=false` is set for WAR packaging, and if `NOTICE.txt` / `LICENSE.txt` are
  present, they're automatically bundled into the META-INF of both the jar and the war

Some ecuacion projects, such as `ecuacion-tool-code-generator`, use this pattern when they want to
align with ecuacion's own build conventions. General applications usually don't need it — use
Pattern 1 or Pattern 3 instead.

### Add the module(s) you need

```xml
<!-- to build a REST API -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>

<!-- to build a Spring Batch job -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-batch</artifactId>
</dependency>
```

See [Setup](page?id=rest/setup&lang=en) under the **rest** menu, or
[Setup](page?id=batch/setup&lang=en) under the **batch** menu, for module-specific
details.
