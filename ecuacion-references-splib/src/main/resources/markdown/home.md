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

You can manage the versions of the `ecuacion-splib-xxx` modules either by using
`ecuacion-splib-parent` as the parent POM, or by importing it as a BOM. Then add the module(s)
your application needs.

### Pattern 1: Use it as the parent POM

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>(version)</version>
</parent>
```

### Pattern 2: Import it as a BOM

If you cannot use `ecuacion-splib-parent` as the parent POM (for example, because your project
already has another parent POM), import it as a BOM instead.

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
a version.

Note, however, that plugin versions/configuration (`pluginManagement`) such as
`spring-boot-maven-plugin` are not inherited via BOM import. If you use such plugins, you need to
specify their versions yourself.

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
