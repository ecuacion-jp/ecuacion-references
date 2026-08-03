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
also depends on how you want to handle the Spring Boot version, giving three patterns overall.

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
version is needed. Note, however, that using it as the parent POM also inherits the following
build configuration that `ecuacion-splib-parent` uses internally, not just version management:

- `spring-boot-devtools` / `spring-boot-starter-test` / `allure-jupiter` are added automatically as
  actual dependencies, not just managed versions
- NullAway / Error Prone static analysis is enforced at compile time
- Test execution settings such as `maven-surefire-plugin`'s `useModulePath=false` are overridden

Some ecuacion projects, such as `ecuacion-tool-code-generator`, use this pattern, but for general
applications Pattern 1 is usually recommended unless you're fine with the side effects above.

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
