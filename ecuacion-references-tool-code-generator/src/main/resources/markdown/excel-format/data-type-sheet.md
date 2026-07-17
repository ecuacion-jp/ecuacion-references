The DataType Definition sheet defines the data types (`DT_XXXX`) assigned to each column.
A DataType captures the Java type, validation rules, and character constraints for a field.

## Column Layout

Table name: `テーブル2`, range: `A8:S{last row}`

| Column | Field | Description |
| --- | --- | --- |
| A | DataType Name | Identifier starting with `DT_`. Uppercase letters, digits, and underscores only |
| B | Type | Type name (see Types section below) |
| C | Min Length | STRING: minimum string length (optional) |
| D | Max Length | STRING: maximum string length (required) |
| E | Data Pattern (Japanese) | STRING: Japanese label for the data pattern (required) |
| F | Data Pattern | STRING: data pattern identifier (required) |
| G | Excluded from Forbidden-Char Check | STRING: characters exempt from the forbidden-character check (optional) |
| H | Regex | STRING: custom regex constraint (optional) |
| I | Min Value | Numeric types: minimum value (optional) |
| J | Max Value | Numeric types: maximum value (optional) |
| K | Integer Digits | BIG_DECIMAL: number of integer-part digits (required) |
| L | Decimal Digits | BIG_DECIMAL: number of decimal-part digits (required) |
| M | Code Length | ENUM: fixed code length in characters (required) |
| N | No Timezone | Date-time types: `○` = no timezone (`LocalDateTime`) (optional) |
| O | Notes | Comments (not used in generation) |
| P | Pattern Description (default lang) | Human-readable description of the data pattern (English) |
| Q–S | Pattern Description (lang 1–3) | Language-specific pattern descriptions |

---

## Settings per Type

Available settings differ by type. ○ = required / △ = optional / (blank) = not applicable

| Type | Min Length (C) | Max Length (D) | Data Pattern (E・F) | Regex (H) | Min Value (I) | Max Value (J) | Integer Digits (K) | Decimal Digits (L) | Code Length (M) | No Timezone (N) | Auto-assign (※1) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `STRING` | △ | ○ | ○ | △ | | | | | | | |
| `INTEGER` | | | | | △ | △ | | | | | △ |
| `SHORT` | | | | | △ | △ | | | | | |
| `LONG` | | | | | △ | △ | | | | | △ |
| `FLOAT` | | | | | △ | △ | | | | | |
| `DOUBLE` | | | | | △ | △ | | | | | |
| `BIG_DECIMAL` | | | | | △ | △ | ○ | ○ | | | |
| `TIMESTAMP` | | | | | | | | | | △ | |
| `ENUM` | | | | | | | | | ○ | | |
| `BOOLEAN` | | | | | | | | | | | |

※1 Auto-assign is set in column H of the DB Definition sheet. It is listed here because it is type-dependent.

---

## Types

| Type | Java Type | PostgreSQL Type | Description |
| --- | --- | --- | --- |
| `STRING` | `String` | `varchar` | String with optional length and character constraints |
| `ENUM` | (Enum class) | `varchar` | Enumeration. Values are defined in the Enum Definition sheet |
| `SHORT` | `Short` | `smallint` | 2-byte signed integer |
| `INTEGER` | `Integer` | `int` | 4-byte signed integer |
| `LONG` | `Long` | `bigint` | 8-byte signed integer (surrogate keys, version numbers) |
| `BIG_INTEGER` | `BigInteger` | `numeric` | Arbitrary-precision integer (no decimal) |
| `FLOAT` | `Float` | `real` | Single-precision floating-point (4 bytes) |
| `DOUBLE` | `Double` | `double precision` | Double-precision floating-point (8 bytes) |
| `BIG_DECIMAL` | `BigDecimal` | `numeric` | Arbitrary-precision numeric (precision and scale specified) |
| `YEAR_MONTH` | `YearMonth` | `text` | Year and month |
| `DATE` | `LocalDate` | `date` | Date |
| `TIME` | `LocalTime` | `time` | Time |
| `DATE_TIME` | `LocalDateTime` | `timestamp` | Date and time (no timezone) |
| `TIMESTAMP` | `LocalDateTime` | `timestamp` | Timestamp (no timezone) |
| `BOOLEAN` | `Boolean` | `bool` | Boolean value |

## Data Patterns (STRING Type)

| Value | Description |
| --- | --- |
| `全半角（制限なし）` | No character restriction |
| `半角` | ASCII printable characters only (space through tilde) |

## Standard Built-in DataTypes

These DataTypes are included in all projects.

| DataType | Type | Purpose |
| --- | --- | --- |
| `DT_BOOL` | BOOLEAN | Boolean value |
| `DT_DB_UPD_VER` | LONG | Optimistic locking version number |
| `DT_SERIAL` | LONG | Auto-incremented surrogate key / foreign key |
| `DT_TIMESTAMP` | DATE_TIME | Date-time (audit columns, etc.) |
| `DT_CODE` | STRING (1-100, ASCII) | Code / authentication code |
| `DT_MAIL_ADDRESS` | STRING (1-256) | Email address |
| `DT_ACC_NAME` | STRING (1-30) | Account name |
| `DT_HASHED_PASSWORD` | STRING (60-60) | BCrypt-hashed password |

## Adding Project-Specific DataTypes

Add project-specific DataTypes below the standard ones.

Example (Qiita data integration project):

| DataType Name | Type | Min | Max | Data Pattern |
| --- | --- | --- | --- | --- |
| `DT_QIITA_ITEM_ID` | STRING | 20 | 20 | ASCII |
| `DT_QIITA_TITLE` | STRING | 1 | 255 | (no restriction) |
| `DT_COUNT` | INTEGER | | | |

When adding rows, extend the named table range (`テーブル2`) accordingly.

## DataType and Validation

The `DataTypeValidator` class generated from each DataType defines the field's validation constraints.
For example, `DT_CODE` produces:

```java
@NotEmpty
@Size(min = 1, max = 100)
@Pattern(regexp = "^[a-zA-Z0-9 -/:-@\\[-`{-~]*$")
private String code;
```
