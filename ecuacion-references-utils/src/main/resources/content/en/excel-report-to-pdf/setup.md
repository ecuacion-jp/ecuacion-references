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

## Placing Font Files

**Noto Sans JP** font files are required to render Japanese text correctly in PDFs.

Place the files at these paths:

```
src/main/resources/fonts/NotoSansJP/NotoSansJP-Regular.ttf
src/main/resources/fonts/NotoSansJP/NotoSansJP-Bold.ttf
```

### Obtaining Noto Sans JP

Download it for free from Google Fonts (https://fonts.google.com/noto/specimen/Noto+Sans+JP):

1. Click **Download family** at the top of the page.
2. Extract the downloaded ZIP.
3. Copy `NotoSansJP-Regular.ttf` and `NotoSansJP-Bold.ttf` to the paths above.

> Consider adding the font files to `.gitignore` so they are not committed to
> your repository.
