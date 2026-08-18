The General Settings sheet contains project-wide configuration.
Every item in the "System Common" category (rows 6-11) is **required** — loading the sheet with any of them blank
is an error. Among those, some must be changed for each new project, others can be left at the template defaults.
"System Common" also has several optional fields (rows 12-19) filled in only when needed, and there are a few other
fields outside "System Common" that are optional, filled in only when the corresponding feature is used.

## 1. Must Be Changed for Each Project (Required)

| Row | Field | Description |
| --- | --- | --- |
| 7 | System name | Project identifier. Becomes the output folder name under `products/`. Use alphanumerics and hyphens (e.g., `my-project`) |
| 8 | Common part of the package name | Common Java package prefix for all generated code (e.g., `jp.example.myapp`) |

## 2. Required, but Normally Left at the Template Defaults

These cannot be blank either, but the template ships with reasonable defaults, so there is normally no need to
change them.

| Row | Field | Default | Description |
| --- | --- | --- | --- |
| 6 | Template version | the template's version (e.g. `5.0.0`) | Version of this Excel file's template. Keep the downloaded value |
| 9 | Framework type | `Spring Framework` | Framework in use (`Spring Framework` or `jakarta EE`) |
| 10 | Character encoding | `UTF-8` | Character encoding of the generated source code |
| 11 | Default language | `en` | Default language, as a locale string (e.g. `en`, `en_US`) |

## 3. Optional, Only When Using the Corresponding Feature

The remaining fields in the "System Common" category (rows 12-19) are optional:

| Row | Field | Description |
| --- | --- | --- |
| 12-14 | Support language 1 / 2 / 3 | Additional supported languages, as locale strings, filled in order (leaving language 1 blank while 2 is filled is an error) |
| 15 | Prohibited characters | Characters prohibited in user input, checked wherever `@Pattern`-based validation applies |
| 16 | Prohibited-chars pattern description (default language) | Human-readable description of the prohibited-characters pattern, in the default language. Required when "Prohibited characters" is not empty |
| 17-19 | Prohibited-chars pattern description (additional language 1 / 2 / 3) | Same description, in each additional supported language. Required when "Prohibited characters" and the corresponding support-language field are both not empty |

A few other fields, outside "System Common", are also optional, filled in only when the corresponding feature is
used:

| Row | Field | Category | Description |
| --- | --- | --- | --- |
| 26 | Table names without grouping | Group Access Restriction Settings | Comma-separated list of table names that do NOT have the group filter column. Required only when using the grouping feature |

### System name

Determines the output directory name and is embedded in the generated `Constants.java`:

```java
// Example: system name = "my-project"
public class Constants {
    public static final String SYSTEM_NAME = "my-project";
}
```

### Common part of the package name

All generated Java sources use this as their package root:

```
jp.example.myapp.base.entity.AccEntity
jp.example.myapp.base.bl.AccBl
...
```

### Table names without grouping

When the grouping feature is enabled (the "Group Access Restriction Settings" category's column name is set),
every table must either have the group column or be explicitly listed here. List the tables that do NOT have this
column (e.g., authentication tables such as `ACC`, `ACC_ADMIN`).

---

## 4. Optional Framework-Convention Fields (Blank Disables the Feature)

These belong to the "Logical Delete" and "Group Access Restriction Settings" categories, outside "System Common".
Unlike the "System Common" fields above, the template ships with the column-name value of both categories
**blank** — leaving it blank disables the corresponding feature (soft delete / multi-tenant grouping) entirely;
there is no built-in default column name. Any column with the configured name is treated as that feature's column,
so pick a name that is not used for anything else. To actually use the feature, also define a column with that
name in the DB Item Definition or DB Common Item Definition sheet.

| Category | Field | Description |
| --- | --- | --- |
| Logical Delete | Column name | Soft-delete flag column name. Blank to disable soft delete |
| Logical Delete | DataType name | DataType of the flag column. Only `BOOLEAN`-type DataTypes are supported |
| Logical Delete | Initial value | Initial value before soft delete (e.g. `false` for a `BOOLEAN` DataType) |
| Logical Delete | Update value | Value written on soft delete (e.g. `true` for a `BOOLEAN` DataType) |
| Group Access Restriction Settings | Column name | Multi-tenant group column name. Blank to disable grouping |
| Group Access Restriction Settings | DataType name | DataType of the group column |

## 5. Required Framework-Convention Field

Unlike the two "Column name" fields above, the "Optimistic Locking" category's column name is **required** —
every generated entity relies on an optimistic-lock version column existing, so leaving it blank is a load error,
not a way to opt out of the feature. Unlike "Logical Delete" / "Group Access Restriction Settings", the template
ships this field pre-filled with `VERSION`. The column itself may live on each table individually or on the common
columns; wherever it lives, its DataType kata must be one of the ones the JPA spec allows for `@Version`: short,
integer, long, `Timestamp`, or `DateTime`.

| Category | Field | Default | Description |
| --- | --- | --- | --- |
| Optimistic Locking | Column name | `VERSION` | Optimistic locking version column name |
