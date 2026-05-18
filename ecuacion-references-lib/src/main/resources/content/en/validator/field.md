# Other Field Validators

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
