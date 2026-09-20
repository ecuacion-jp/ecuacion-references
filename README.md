# ecuacion-references

[![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/technologies/downloads/)
[![GitHub Release](https://img.shields.io/github/v/release/ecuacion-jp/ecuacion-references)](https://github.com/ecuacion-jp/ecuacion-references/releases)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

## What is it?

`ecuacion-references` hosts the tutorial and reference web applications for the
[`ecuacion-lib`](https://github.com/ecuacion-jp/ecuacion-lib) /
[`ecuacion-splib`](https://github.com/ecuacion-jp/ecuacion-splib) /
[`ecuacion-utils`](https://github.com/ecuacion-jp/ecuacion-utils) /
[`ecuacion-tools`](https://github.com/ecuacion-jp/ecuacion-tools) /
[`ecuacion-tool-code-generator`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator) family of
libraries. Each tutorial app is a working Spring Boot application built with the library it documents,
so you can read the explanation and browse the source that implements it side by side.

- **ecuacion-references-lib** — Tutorial for `ecuacion-lib` (Markdown article site).
- **ecuacion-references-splib** — Tutorial for `ecuacion-splib` (Markdown article site).
- **ecuacion-references-utils** — Tutorial for `ecuacion-utils` (Markdown article site).
- **ecuacion-references-tools** — Tutorial for `ecuacion-tools` (Markdown article site).
- **ecuacion-references-tool-code-generator** — Tutorial for `ecuacion-tool-code-generator` (Markdown article site).
- **ecuacion-references-splib-web-web** — Hands-on tutorial app for the `ecuacion-splib` web framework, where you
  operate real screens built with the framework to learn its behavior.
- **ecuacion-references-splib-web-base** — Shared entity / record / repository / business-logic classes used by
  `ecuacion-references-splib-web-web`.
- **ecuacion-references-splib-web-project-template** — Starter template for new `ecuacion-splib` web projects.
- **ecuacion-references-core** / **ecuacion-references-core-for-markdown-only-apps** — Shared base modules used
  internally by the tutorial apps above.

## System Requirements

- JDK 21 or above.

## Build & Run

This is a Maven multi-module project.

```bash
# Build all modules
mvn clean install

# Run a specific tutorial app (e.g. the ecuacion-lib tutorial)
cd ecuacion-references-lib && mvn spring-boot:run
```

See the `README` in each module for details.
