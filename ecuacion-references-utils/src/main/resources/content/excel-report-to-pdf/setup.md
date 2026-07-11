# Setup

## Adding the Dependency

Add the following to `pom.xml`:

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-report-to-pdf</artifactId>
    <version>(version)</version>
</dependency>
```

## Font Configuration

You must configure the font used to render text in the PDF.
`PdfGenerateOptions` supports two approaches.

### Option 1: Use System Fonts (`useSystemFonts(true)`)

With `useSystemFonts(true)`, the library searches the OS font directories
(including fonts installed by Microsoft Office) for the workbook's default font.

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .useSystemFonts(true)
    .build();
```

If no matching system font is found, a `PdfGenerateException` is thrown.
You can also set `regularFontPath` as a fallback for when the system font is not found.

> **Font licensing notice:** The located system font is embedded in the output PDF.
> Confirm that the font's licence permits embedding and distribution before enabling this option.

### Option 2: Specify a Font File Path (`regularFontPath`)

When `useSystemFonts` is not set (default `false`), you must provide a TTF font
file path explicitly via `regularFontPath`.

```java
PdfGenerateOptions options = PdfGenerateOptions.builder()
    .regularFontPath(Path.of("/path/to/NotoSansJP-Regular.ttf"))
    .boldFontPath(Path.of("/path/to/NotoSansJP-Bold.ttf"))  // optional
    .build();
```

For documents containing Japanese text, use a Japanese-compatible font.
For example, **Noto Sans JP** is available for free download from
[Google Fonts](https://fonts.google.com/noto/specimen/Noto+Sans+JP).
