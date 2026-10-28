The DataType Definition sheet defines the data types (`DT_XXXX`) assigned to each column.
A DataType captures the Java type, validation rules, and character constraints for a field.

## Column Layout

Table name: `Table2`, range: `A8:T{last row}`

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
| O | Max Size [MB] | BINARY: maximum size that can be stored, in megabytes; decimals allowed (required) |
| P | Notes | Comments (not used in generation) |
| Q | Pattern Description (default lang) | Human-readable description of the data pattern (English) |
| R–T | Pattern Description (lang 1–3) | Language-specific pattern descriptions |

---

## Settings per Type

Available settings differ by type. ○ = required / △ = optional / (blank) = not applicable

| Type | Min Length (C) | Max Length (D) | Data Pattern (E・F) | Regex (H) | Min Value (I) | Max Value (J) | Integer Digits (K) | Decimal Digits (L) | Code Length (M) | No Timezone (N) | Max Size [MB] (O) | Auto-assign (※1) |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| `STRING` | △ | ○ | ○ | △ | | | | | | | | |
| `INTEGER` | | | | | △ | △ | | | | | | △ |
| `SHORT` | | | | | △ | △ | | | | | | |
| `LONG` | | | | | △ | △ | | | | | | △ |
| `FLOAT` | | | | | △ | △ | | | | | | |
| `DOUBLE` | | | | | △ | △ | | | | | | |
| `BIG_DECIMAL` | | | | | △ | △ | ○ | ○ | | | | |
| `TIMESTAMP` | | | | | | | | | | △ | | |
| `ENUM` | | | | | | | | | ○ | | | |
| `BOOLEAN` | | | | | | | | | | | | |
| `BINARY` | | | | | | | | | | | ○ | |

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
| `BINARY` | `byte[]` | `bytea` | Binary data (e.g. file contents). See "BINARY Type" below |

## BINARY Type

A type for storing byte sequences such as file contents in the DB.

- **Max Size [MB] (column O) is required.** It is in megabytes and may have a decimal part such as `0.5` (1MB = 1024 × 1024 bytes, the same as Spring's `10MB` etc.).
  It is converted to bytes at generation (the fraction is truncated) and the entity field gets `@Size(max = <bytes>)` (e.g. `10` → `@Size(max = 10485760)`). Exceeding it is a Bean Validation error.
  The same value is set to `length` of `@Column`, so Hibernate picks the DB column type for the DB in use (`bytea` on PostgreSQL; `varbinary` / `mediumblob` / `longblob` etc. by capacity on MySQL / MariaDB).
- **`@Lob` is not added.** With `@Lob`, a `byte[]` field becomes a large object (`oid`) on PostgreSQL, whose contents remain in the DB even after the row is physically deleted.
- **BaseRecord holds the value as `byte[]` as it is** (not converted to a string, like `BOOLEAN`). It is copied as is from entity to record, and from record to entity **`null` leaves the column unchanged**, as with other types (the existing contents are kept).
  To receive the input from the screen, add a separate field to the record (e.g. `MultipartFile iconImageFile`) and set its contents into the `byte[]` field (this conversion is written in the application).
  Decide whether a file is chosen by **whether the filename is empty**, not by `isEmpty()`: a `MultipartFile` with an empty filename arrives even when no file is chosen, and `isEmpty()` (size 0) is also `true` for a chosen zero-byte file.

  ```java
  MultipartFile file = rec.getIconImageFile();
  if (file != null && StringUtils.isNotEmpty(file.getOriginalFilename())) {
    rec.setIconImage(file.getBytes());   // a zero-byte file is stored as a zero-length array
  }
  ```

  To make it required, specify `notEmpty()` of `HtmlItem` on the receiving field (`iconImageFile`). The required check of ecuacion-splib-web also regards an empty filename as "not entered" (a zero-byte file is not an error).
  Since a file is sent only when chosen, "clearing (deleting)" the contents needs to be done explicitly in the application, e.g. setting `null` when a checkbox is checked.
- **For large files, keeping only the contents in a separate table is recommended** (e.g. file name, size etc. in `INQUIRY_ATTACHMENT`, and the contents in `INQUIRY_ATTACHMENT_CONTENT`, 1:1).
  Otherwise the contents are loaded every time, even when only listing records. code-generator does not report an error when they are in the same table.
- Before Bean Validation, the size of uploaded files is also limited by Spring's `spring.servlet.multipart.max-file-size` / `max-request-size` (1MB / 10MB by default). Set them in the application as needed.

## Data Patterns (STRING Type)

Column E holds the display name shown in the dropdown; column F is auto-populated by a VLOOKUP against the
(hidden) "dataType Data Pattern List" sheet and holds the underlying `enumValue`. All 14 defined patterns:

| Display Name (Column E) | enumValue (Column F) | Matches |
| --- | --- | --- |
| `All Characters` | `REG_EX_ALL` | No character restriction |
| `Half-width` | `REG_EX_HAN` | ASCII printable characters (space through tilde) |
| `Half-width Numeric` | `REG_EX_HAN_NUM` | Digits (`0-9`) only |
| `Uppercase` | `REG_EX_HAN_UC` | Uppercase ASCII letters (`A-Z`) only |
| `Uppercase + Underscore` | `REG_EX_HAN_UC_US` | Uppercase ASCII letters and underscore |
| `Lowercase` | `REG_EX_HAN_LC` | Lowercase ASCII letters (`a-z`) only |
| `Lowercase + Underscore` | `REG_EX_HAN_LC_US` | Lowercase ASCII letters and underscore |
| `Numeric + Uppercase` | `REG_EX_HAN_NUM_UC` | Digits and uppercase letters |
| `Numeric + Uppercase + Underscore` | `REG_EX_HAN_NUM_UC_US` | Digits, uppercase letters, and underscore |
| `Numeric + Lowercase` | `REG_EX_HAN_NUM_LC` | Digits and lowercase letters |
| `Numeric + Lowercase + Underscore` | `REG_EX_HAN_NUM_LC_US` | Digits, lowercase letters, and underscore |
| `Alphanumeric` | `REG_EX_HAN_NUM_UC_LC` | Uppercase and lowercase ASCII letters (despite the name, no digits) |
| `Alphanumeric + Underscore` | `REG_EX_HAN_NUM_UC_LC_US` | Uppercase and lowercase ASCII letters, and underscore |
| `Full-width` | `REG_EX_ZEN` | Full-width characters only (excludes ASCII and half-width kana) |

## Conventional DataTypes

The blank template's DataType Definition sheet ships empty — none of these are pre-populated, and the tool has no
built-in knowledge of them either; you add them like any other DataType. This particular set of names is simply
used consistently across `ecuacion-splib`-based projects (including for the authentication tables `ecuacion-splib`
expects), so most projects end up defining at least the first few near the top of their sheet.

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

When adding rows, extend the named table range (`Table2`) accordingly.

## DataType and Validation

The `DataTypeValidator` class generated from each DataType defines the field's validation constraints.
For example, `DT_CODE` produces:

```java
@NotEmpty
@Size(min = 1, max = 100)
@Pattern(regexp = "^[a-zA-Z0-9 -/:-@\\[-`{-~]*$")
private String code;
```
