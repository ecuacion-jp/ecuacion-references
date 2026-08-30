## Overview

When annotations express conditional rules such as "when `conditionPropertyPath` is in a certain state,
`propertyPath` must satisfy a certain condition." These are class-level annotations.

```java
@TrueWhen(
    propertyPath = "agreedToTerms",
    conditionPropertyPath = "accountType",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class RegistrationForm { ... }
```

---

## List of Annotations

### Basic Conditions (Specified Only by conditionValue)

| Annotation | Condition that `propertyPath` Must Satisfy |
| ------------- | ------------------------------- |
| `@TrueWhen` | Must be `true` |
| `@FalseWhen` | Must be `false` |
| `@NullWhen` | Must be `null` |
| `@NotNullWhen` | Must not be `null` |
| `@EmptyWhen` | Must be empty (null or blank string) |
| `@NotEmptyWhen` | Must not be empty |

### Pattern, String, and Value-Reference Conditions

These annotations have their own parameters in addition to `conditionValue`.

| Annotation | Condition that `propertyPath` Must Satisfy | Specific Parameter |
| ------------- | ------------------------------- | ------------- |
| `@PatternWhen` | Must match a regular expression | `regexp` |
| `@NotPatternWhen` | Must not match a regular expression | `regexp` |
| `@StringWhen` | Must be one of the specified strings | `string[]` |
| `@NotStringWhen` | Must not be any of the specified strings | `string[]` |
| `@ValueOfPropertyPathWhen` | Must have the same value as another field | `valuePropertyPath` |
| `@NotValueOfPropertyPathWhen` | Must have a different value from another field | `valuePropertyPath` |

> **Note:** `@PatternWhen` / `@NotPatternWhen`'s `regexp`, and `conditionValuePatternRegexp` (see
> [conditionValue](#conditionvalue--type-of-condition) below), are matched against a value that
> may come from end-user input. Avoid patterns prone to catastrophic backtracking (e.g. nested
> quantifiers like `(a+)+`), which can make matching take exponential time on a crafted input.

---

## Key Attributes

### propertyPath — Field to Validate

```java
propertyPath = "agreedToTerms"            // Single field
propertyPath = {"startDate", "endDate"}   // Multiple fields validated with the same rule
```

### conditionPropertyPath — Condition Field

The field name used for evaluating the condition. Nested fields can also be specified.

```java
conditionPropertyPath = "accountType"
conditionPropertyPath = "address.country"
```

### conditionValue — Type of Condition

Specifies the state of the `conditionPropertyPath` field that triggers validation.

| `ConditionValue` | Meaning | Required Additional Attributes | Optional Additional Attributes |
| ---------------- | ---- | --------------- | --------------- |
| `NULL` | Is `null` | — | — |
| `NOT_NULL` | Is not `null` | — | — |
| `EMPTY` | Is empty (null or blank string) | — | — |
| `NOT_EMPTY` | Is not empty | — | — |
| `TRUE` | Is `true` | — | — |
| `FALSE` | Is `false` | — | — |
| `STRING` | Is one of the specified strings | `conditionValueString` | `conditionValueDisplayStringPropertyPath` |
| `PATTERN` | Matches a regular expression | `conditionValuePatternRegexp` | `conditionValuePatternDescription`, `conditionValueDisplayStringPropertyPath` |
| `VALUE_OF_PROPERTY_PATH` | Has the same value as another field | `conditionValuePropertyPath` | `conditionValueDisplayStringPropertyPath` |

### conditionOperator — Condition Operator

| `ConditionOperator` | Meaning |
| ------------------- | ---- |
| `EQUAL_TO` (default) | Validates when the condition is met |
| `NOT_EQUAL_TO` | Validates when the condition is not met |

### conditionValuePatternDescription

When `conditionValue = PATTERN`, specifies a human-readable description to display in error messages
instead of the regular expression set in `conditionValuePatternRegexp`.
If omitted, the regular expression is displayed as-is.

```java
// Without conditionValuePatternDescription
// → When 'phone number' matches '^(070|080|090).*' ...

// With conditionValuePatternDescription
@TrueWhen(
    propertyPath = "smsConsentAgreed",
    conditionPropertyPath = "phone",
    conditionValue = ConditionValue.PATTERN,
    conditionValuePatternRegexp = "^(070|080|090).*",
    conditionValuePatternDescription = "mobile phone number"  // Description used in message
)
// → When 'phone number' matches 'mobile phone number' ...
```

### conditionValueDisplayStringPropertyPath

For `STRING` / `PATTERN` / `VALUE_OF_PROPERTY_PATH`, specifies another field's itemNameKey
to resolve the "display name of the condition value" to show in error messages.
If omitted, the actual value (string, regex, or field value) is displayed as-is.

### falseWhenConditionNotSatisfied

When set to `true`, applies the reverse rule when the condition is not met
(for `@TrueWhen`, validates that it is `false` when the condition is not met). Default is `false`.

---

## Usage Examples

### Basic Example — Conditional Required

```java
// When accountType is filled in, agreedToTerms must be true
@TrueWhen(
    propertyPath = "agreedToTerms",
    conditionPropertyPath = "accountType",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class RegistrationForm { ... }
```

### STRING Condition — When a Specific Value

```java
// When role is "ADMIN", adminCode must not be empty
@NotEmptyWhen(
    propertyPath = "adminCode",
    conditionPropertyPath = "role",
    conditionValue = ConditionValue.STRING,
    conditionValueString = {"ADMIN"}
)
public class UserForm { ... }
```

### NOT_EQUAL_TO — Validate When Condition Is Not Met

```java
// When the submission confirmation flag is not true, reason must not be empty
@NotEmptyWhen(
    propertyPath = "reason",
    conditionPropertyPath = "confirmed",
    conditionValue = ConditionValue.TRUE,
    conditionOperator = ConditionOperator.NOT_EQUAL_TO
)
public class CancelForm { ... }
```

### @PatternWhen — Validate with Regex (regexp)

```java
// When type is "POSTAL", code must be a 7-digit number
@PatternWhen(
    propertyPath = "code",
    regexp = "\\d{7}",
    conditionPropertyPath = "type",
    conditionValue = ConditionValue.STRING,
    conditionValueString = {"POSTAL"}
)
public class AddressForm { ... }
```

### @StringWhen — Validate That Value Is a Specific String (string[])

```java
// When role is NOT_EMPTY, accountType must be "ADMIN" or "OPERATOR"
@StringWhen(
    propertyPath = "accountType",
    string = {"ADMIN", "OPERATOR"},
    conditionPropertyPath = "role",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class UserForm { ... }
```

### @ValueOfPropertyPathWhen — Validate That Value Matches Another Field (valuePropertyPath)

```java
// When confirmed is true, confirmPassword must have the same value as password
@ValueOfPropertyPathWhen(
    propertyPath = "confirmPassword",
    valuePropertyPath = "password",
    conditionPropertyPath = "confirmed",
    conditionValue = ConditionValue.TRUE
)
public class PasswordForm { ... }
```
