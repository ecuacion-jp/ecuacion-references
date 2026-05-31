# DataType Definition Sheet

The DataType Definition sheet defines the data types (`DT_XXXX`) assigned to each column.
A DataType captures the Java type, validation rules, and character constraints for a field.

## Column Layout

Table name: `テーブル2`, range: `A8:S{last row}`

| Column | Field | Description |
| --- | --- | --- |
| A | DataType Name | Identifier starting with `DT_`. Uppercase letters, digits, and underscores only |
| B | Type | `BOOLEAN` / `LONG` / `INTEGER` / `DATE_TIME` / `STRING` / `ENUM` |
| C | Min Length | STRING only. Minimum string length |
| D | Max Length | STRING only. Maximum string length |
| E | Data Pattern | STRING only. Allowed character set |

## Types

| Type | Java Type | Description |
| --- | --- | --- |
| `BOOLEAN` | `Boolean` | Boolean value |
| `LONG` | `Long` | Long integer (surrogate keys, version numbers) |
| `INTEGER` | `Integer` | Integer |
| `DATE_TIME` | `LocalDateTime` | Date and time (no timezone) |
| `STRING` | `String` | String with optional length and character constraints |
| `ENUM` | (Enum class) | Enumeration. Values are defined in the Enum Definition sheet |

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
