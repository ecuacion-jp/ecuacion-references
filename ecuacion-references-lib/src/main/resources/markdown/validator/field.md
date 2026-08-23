## Overview

These are validators applied at the field level to individual fields.

---

## String Format Validators

Validates whether a value stored as a string can be converted to a specific type.

| Annotation | Validation Content |
| ------------- | -------- |
| `@IntegerString` | Must be an integer string within the range of `int` |
| `@LongString` | Must be an integer string within the range of `long` |
| `@BooleanString` | Must be a string convertible to `true` / `false` |

```java
public class SearchForm {
    @IntegerString
    private String page;

    @BooleanString
    private String includeArchived;
}
```

---

## SizeString — String Length

Validates that the string length is within the specified range (the string version of the standard `@Size`).

```java
@SizeString(min = 1, max = 100)
private String name;
```

| Attribute | Description | Default |
| ---- | ---- | ---------- |
| `min` | Minimum character count | `0` |
| `max` | Maximum character count | `Integer.MAX_VALUE` |

---

## EnumElement — Enum Value Check

Validates that the value is a valid element (`name()`) of the specified Enum.

```java
@EnumElement(enumClass = StatusEnum.class)
private String status;
```

Checks whether strings like `"ACTIVE"` or `"INACTIVE"` are defined in `StatusEnum`.

---

## Path Existence Validators — File / Directory Existence Check

Validates that a `String`, `java.io.File` or `java.nio.file.Path` value points to something that
actually exists on the file system.

| Annotation | Validation Content |
| ------------- | -------- |
| `@FileExists` | Must point to an existing regular file (a directory is invalid) |
| `@DirExists` | Must point to an existing directory (a regular file is invalid) |
| `@PathExists` | Must point to an existing file or directory (either is valid) |

```java
public class ImportForm {
    @FileExists
    private String sourceFilePath;

    @DirExists
    private Path outputDir;
}
```

---

## FileExtension — File Extension Check

Validates that the file name extension of a `String`, `java.io.File` or `java.nio.file.Path`
value matches the one specified.

```java
@FileExtension("xlsx")
private String uploadedFileName;
```

- The extension may be specified with or without the leading dot (`"xlsx"` and `".xlsx"` are
  treated the same).
- The comparison is case-insensitive (`"report.XLSX"` matches `@FileExtension("xlsx")`).
- A file name with no extension at all (e.g. `"report"`) is invalid.
