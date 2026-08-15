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
| `ecuacion-splib-batch` | Spring Batch integration, for jobs a scheduler triggers unattended |
| `ecuacion-splib-ui` | UI-facing logic shared across `ecuacion-splib-web` and `ecuacion-splib-cli` (e.g. required-field violation filtering) — not used directly by application code |
| `ecuacion-splib-cli` | Lightweight foundation for command-line (CUI) applications a user runs directly and watches interactively |
| `ecuacion-splib-web` | Full Spring MVC web application framework (controllers, forms, Thymeleaf/Bootstrap templates) |
| `ecuacion-splib-web-jpa` | Glue between `ecuacion-splib-web` and `ecuacion-splib-jpa` |
| `ecuacion-splib-web-markdown` | Renders Markdown files as web pages; this reference site itself is built on it |
| `ecuacion-splib-rest` | Framework for building REST APIs (exception handling, API key / public / denied endpoint security) |
| `ecuacion-splib-dependencies` | Parent POM used for building ecuacion's own modules (not recommended for general application developers) |

`ecuacion-splib-dependencies` is a build-only module for ecuacion-splib itself (and other ecuacion
projects); general application developers don't use it. See "Setup" below for details.

This site currently covers **`ecuacion-splib-rest`** (see the **rest** menu above),
**`ecuacion-splib-batch`** (see the **batch** menu above), and **`ecuacion-splib-cli`** (see the
**cli** menu above). Articles for the other modules will be added over time.

`ecuacion-splib-web` has its own hands-on tutorial application (a separate site) that lets you interact
with real screens built on the framework, rather than reading Markdown articles about it.

---

## Setup

You can manage the versions of the `ecuacion-splib-xxx` modules either by importing
`ecuacion-splib-parent` as a BOM, or by using it as the parent POM. Which one you should choose
also depends on how you want to handle the Spring Boot version, giving three patterns overall.

### Pattern 1: Use ecuacion-splib as the parent POM (recommended)

```xml
<parent>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-parent</artifactId>
    <version>(version)</version>
</parent>
```

The Spring Boot version stays tied to the ecuacion-splib version, so no explicit version is
needed. `ecuacion-splib-parent` is a thin parent POM with no real dependencies and none of
ecuacion's own build enforcement, so it can be used without side effects. The one exception is the
`-parameters` compiler flag, needed for Spring 6+'s `@RequestParam` / `@PathVariable`
parameter-name resolution — since that also needs to apply to the consuming application's own
compilation, it's included here too.

Because this pattern goes through an actual Maven `<parent>`, plugin versions/configuration
(`pluginManagement`) are inherited too. If you want to build an executable jar,
`spring-boot-maven-plugin` can be added with just a `<plugin>` element — no version or
`repackage` execution config needed (Pattern 2's BOM import does not inherit this
`pluginManagement`).

This pattern needs the least boilerplate and leaves the least room for a version mismatch, so
it's the recommended default. If you can't use this pattern because you want a different parent
POM (e.g. an in-house shared parent POM), use Pattern 2 instead.

### Pattern 2: Import both ecuacion-splib and Spring Boot as a BOM

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
parent POM). Use it when you can't use Pattern 1 because you want a parent POM other than
`ecuacion-splib-parent`.

### Pattern 3: Import ecuacion-splib as a BOM, use Spring Boot as the parent POM

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
the Spring Boot version ecuacion-splib is built against. Because of this extra bookkeeping, it's
not recommended.

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

<!-- to build a command-line (CUI) application -->
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-cli</artifactId>
</dependency>
```

See [Setup](page?id=rest/setup&lang=en) under the **rest** menu,
[Setup](page?id=batch/setup&lang=en) under the **batch** menu, or
[Setup](page?id=cli/setup&lang=en) under the **cli** menu, for module-specific details.
