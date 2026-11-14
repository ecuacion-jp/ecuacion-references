## Adding the Dependency

Add the following to `pom.xml`:

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-word-document-to-pdf</artifactId>
    <version>(version)</version>
</dependency>
```

`ecuacion-util-pdf-common` (font resolution and the exceptions shared with
`ecuacion-util-excel-report-to-pdf`) is loaded automatically as a dependency.

## Compatibility

| ecuacion-utils | ecuacion-lib |
| --- | --- |
| 5.x | 16.x |

## Font Configuration

You must configure the font used to render text in the PDF.
With `builderForSystemFonts()`, the fonts specified in the document are looked up from the fonts
installed on the OS (including fonts installed by Microsoft Office). To use a font file instead,
prepare a TTF file and use `builderForExplicitFont(Path)`.

For details, see [Options](/public/showMarkdown/page?id=word-document-to-pdf/options&lang=en).
