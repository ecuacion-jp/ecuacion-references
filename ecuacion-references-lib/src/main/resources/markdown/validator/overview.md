## Overview

The `ecuacion-lib-validation` module provides custom validators that complement the standard Jakarta Validation
annotations (`@NotNull`, `@Size`, etc.).

```xml
<dependency>
  <groupId>jp.ecuacion.lib</groupId>
  <artifactId>ecuacion-lib-validation</artifactId>
</dependency>
```

---

## Classification by Application Level

| Level | Description | Representative Annotations |
| ------ | ---- | ---------------------- |
| Class level | Applied to the entire class, validates relationships between multiple fields | `@TrueWhen`, `@GreaterThan`, `@AnyNotNull`, `@ReturnTrue` |
| Field level | Applied to individual fields | `@IntegerString`, `@EnumElement`, `@PatternWithDescription` |
| Method level | Applied to methods, validates their return values | `@AssertTrueWithPropertyPath` |

Class-level annotations specify which field to associate the violation with using the `propertyPath` attribute.

---

## Classification by Feature

### When Variants (Conditional Validation)

Expresses conditional rules such as "when field X is Y, this field must be Z."
Class-level annotations that define conditions using `conditionPropertyPath` and `conditionValue`.

| Annotation | Validation Content |
| ------------- | ---------------- |
| `@TrueWhen` | Must be `true` when condition is met |
| `@NotNullWhen` | Must not be `null` when condition is met |
| `@NotEmptyWhen` | Must not be empty when condition is met |
| 9 more | ... |

### Comparison Validators

Validates the relative order of two fields.
Class-level annotations that compare 2 fields: `propertyPath` and `baselinePropertyPath`.

| Annotation | Validation Content |
| ------------- | ---------------- |
| `@GreaterThan` | `propertyPath` > `baselinePropertyPath` |
| `@LessThan` | `propertyPath` < `baselinePropertyPath` |
| 2 more | ... |

### Field Validators

Validates the format or type compatibility of field values. Used at the field level.

| Annotation | Validation Content |
| ------------- | ---------------- |
| `@IntegerString` | Must be an integer string |
| `@EnumElement` | Must be a valid value of the specified Enum |
| `@PatternWithDescription` | Regex match (with user-friendly message support) |
| 3 more | ... |

### Collection and Assertion Validators

Validates the null/empty state of multiple fields in bulk, or validates the return value of a method.

| Annotation | Validation Content |
| ------------- | ---------------- |
| `@AnyNotNull` | At least one of the specified fields must not be `null` |
| `@AllNullOrAllNotNull` | All fields are `null`, or all fields are not `null` |
| `@AssertTrueWithPropertyPath` | Field-associated equivalent of `@AssertTrue` |
| `@ReturnTrue` | The specified method must return `true` |
| 4 more | ... |

---

## Integration with ValidationMessages

Default messages for each annotation are defined in `ValidationMessages.properties`.
To override them in the application, define a key with the annotation's fully qualified name + `.message`.

```properties
# ValidationMessages.properties
jp.ecuacion.lib.validation.constraints.TrueWhen.message = Agreement is required
```
