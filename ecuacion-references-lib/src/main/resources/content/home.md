# ecuacion-lib reference

This is the reference page for ecuacion library (module name: `ecuacion-lib-xxx`).
It provides comprehensive documentation for the utility classes and features of each library.

## What is ecuacion library?

ecuacion library is a library that provides various common features needed for application development.
It can be used standalone, but is typically used in combination with a base library such as ecuacion-splib.

## Module Structure

ecuacion library consists of the following 5 modules.

| Module Name | Role |
| --- | --- |
| `ecuacion-lib-parent` | Parent POM (version management for ecuacion-lib modules) |
| `ecuacion-lib-dependencies` | BOM (version management for external libraries that ecuacion-lib depends on) |
| `ecuacion-lib-core` | Core features (Item, PropertiesFileUtil, Violation, etc.) |
| `ecuacion-lib-validation` | Custom Jakarta Validation annotation set |
| `ecuacion-lib-validation-business-messages` | Business-oriented validation messages |

For details on `ecuacion-lib-core` and `ecuacion-lib-validation`, see the respective pages in the navigation.
The following sections describe the remaining 3 modules that are fully explained on this page.

---

## Setup

### 1. Import the BOM

By importing `ecuacion-lib-parent` as a BOM, you do not need to specify the version of each module individually.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-parent</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. Add the required modules

```xml
<!-- Core features (Item, PropertiesFileUtil, Violation, etc.) -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-core</artifactId>
</dependency>

<!-- Custom Jakarta Validation annotation set -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation</artifactId>
</dependency>

<!-- Business-oriented validation messages (optional) -->
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation-business-messages</artifactId>
</dependency>
```

Since `ecuacion-lib-validation` depends on `ecuacion-lib-core`,
adding `ecuacion-lib-validation` automatically includes `ecuacion-lib-core` as well.

---

## ecuacion-lib-parent

This is the parent POM for all ecuacion-lib modules. The `dependencyManagement` section defines versions for each ecuacion-lib module
(`ecuacion-lib-core`, `ecuacion-lib-validation`, `ecuacion-lib-validation-business-messages`),
so importing it as a BOM eliminates the need to specify versions individually.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-parent</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## ecuacion-lib-dependencies

This is a Maven BOM that centrally manages versions of external libraries that ecuacion-lib depends on
(`jakarta.validation-api`, `hibernate-validator`, `junit`, etc.).
By importing it into `dependencyManagement`, you can use these libraries at versions compatible with ecuacion-lib.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>jp.ecuacion.lib</groupId>
            <artifactId>ecuacion-lib-dependencies</artifactId>
            <version>(version)</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Why is it separated from ecuacion-lib-parent?

`ecuacion-splib` imports `ecuacion-lib-parent` as a BOM.
If external library version management were included in `ecuacion-lib-parent`,
it could conflict with versions managed by Spring Boot's `dependencyManagement`.
Therefore, external library version management is separated into `ecuacion-lib-dependencies`.

## ecuacion-lib-validation-business-messages

The default Jakarta Validation messages (such as `"must not be null"` provided by Hibernate Validator)
are technical expressions intended for developers. This module replaces them with natural,
user-friendly expressions and contains **only ValidationMessages property files**. It contains no Java code
and takes effect simply by adding it as a dependency.

### Usage

Add the following to your `pom.xml` (the version is managed by the `ecuacion-lib-dependencies` BOM):

```xml
<dependency>
    <groupId>jp.ecuacion.lib</groupId>
    <artifactId>ecuacion-lib-validation-business-messages</artifactId>
</dependency>
```

### Provided Messages

Covers both Jakarta Validation standard annotations and ecuacion-lib-validation custom annotations,
providing messages in English (default) and Japanese (`_ja` locale).

#### Replacement Examples (English)

| Annotation | Default (Hibernate Validator) | After Applying This Module |
| --- | --- | --- |
| `@NotNull` | must not be null | is required |
| `@NotEmpty` | must not be empty | is required |
| `@Pattern` | must match "..." | must be in the correct format |
| `@Size` | size must be between {min} and {max} | must be between {min} and {max} characters |
| `@Email` | must be a well-formed email address | must be a valid email address |

#### Replacement Examples (Japanese)

| Annotation | Default (Hibernate Validator) | After Applying This Module |
| --- | --- | --- |
| `@NotNull` | null は許可されていません | 入力必須です |
| `@NotEmpty` | 空要素は許可されていません | 入力必須です |
| `@Pattern` | 正規表現 "{regexp}" にマッチさせてください | 正しい形式で入力してください |
| `@Size` | {min} から {max} の間のサイズにしてください | {min}文字以上{max}文字以内で入力してください |
| `@Email` | 電子メールアドレスとして正しい形式にしてください | メールアドレスの形式で入力してください |

### Overriding Messages in Your Application

The message keys in this module have a `.default` suffix
(e.g., `jakarta.validation.constraints.NotNull.message.default`).
ecuacion-lib has a mechanism to fall back to the `.default` key when the regular key
(e.g., `jakarta.validation.constraints.NotNull.message`) is not found in the application's
`ValidationMessages.properties`.
This allows you to replace default messages without preventing application-specific customization.
