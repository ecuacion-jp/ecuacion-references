# Blank Handling

## Overview

The validators in `ecuacion-lib-validation` treat blank (empty string `""`) as **valid**.
This is a different design from the Jakarta Validation standard.

---

## Difference from Standard @Pattern

The standard `@Pattern` treats `null` as valid but `""` as invalid.

| Value | Standard `@Pattern` | `@PatternWithDescription` |
| -- | --------------- | ------------------------- |
| `"abc123"` | valid | valid |
| `"ABC"` | invalid (regex mismatch) | invalid (regex mismatch) |
| `null` | valid | valid |
| `""` | **invalid** | **valid** |

---

## Why Blank Is Treated as Valid

In web applications, `null` and `""` have different meanings.

- `""` = The field exists on the screen, and the user submitted it without entering anything
- `null` = The field does not exist on the screen (not included in the submitted data)

When the user submits without entering anything and `@Pattern` returns invalid,
the error "invalid format" is displayed.
However, the proper error in this case should be "required" (from `@NotEmpty`),
and format-checking validators should not react to blank input (`""`).

---

## Design of ecuacion-lib-validation

Format-checking validators treat blank as valid, and leave the required check to `@NotEmpty`.

```java
// Clear separation of responsibilities between required and format checks
public record UserProfile(
    @NotEmpty
    @PatternWithDescription(regexp = "^[a-z0-9]+$", description = "Lowercase alphanumeric")
    String username
) {}
```

| Value | Result | Reason |
| -- | ---- | ---- |
| `"abc123"` | valid | — |
| `"ABC"` | invalid | `@PatternWithDescription` (format mismatch) |
| `null` | invalid | `@NotEmpty` |
| `""` | invalid | `@NotEmpty` |

`@NotEmpty` and format-checking validators clearly have separate responsibilities.

---

## Validators That Treat Blank as Valid

In `ecuacion-lib-validation`, only `@NotEmpty` / `@NotBlank` treat blank as invalid.
All other format-checking validators treat blank as valid.

| Category | Representative Validators |
| -------- | ----------------- |
| Format-checking | `@PatternWithDescription`, `@IntegerString`, `@BooleanString`, etc. |
| Conditional | `@NotEmptyWhen`, `@TrueWhen`, and other `@XxxWhen` variants |
| Comparison | `@LessThan`, `@GreaterThan`, etc. |
