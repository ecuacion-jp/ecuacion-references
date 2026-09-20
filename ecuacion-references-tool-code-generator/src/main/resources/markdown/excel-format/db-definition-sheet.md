The DB Definition sheet defines the database tables and columns.
This sheet drives the generation of Entity, Repository, BL, and related classes.

## Column Layout

Table name: `Table7`, range: `A5:AK{last row}`

| Column | Field | Description |
| --- | --- | --- |
| A | Table Name | DB table name (UPPER_SNAKE_CASE). Repeat for every row of the same table |
| B | Column Name | DB column name (UPPER_SNAKE_CASE) |
| C | dataType | DataType name in `DT_XXXX` format |
| D | dataType Check | VLOOKUP formula for auto-validation (copy to apply) |
| E | Java Only | `○` = no DB column created; only Entity/Record property is generated |
| F | Surrogate key | `○` = this column is the table's surrogate key (primary key) |
| G | natural key (unique key) | `○` = this column is part of the table's natural/unique key |
| H | nullable | `○` = NULL allowed (blank = NOT NULL) |
| I | Auto Numbering | `○` = auto-sets a value when null on insert |
| J | Force Numbering | `○` = always overwrites with assigned value on insert |
| K | Auto Update | `○` = auto-sets a value when null on update |
| L | Force Update | `○` = always overwrites with updated value on update |
| M | Group Identifier | `○` = marks this column as the group identifier |
| N | Spring Audit | `CB` / `CD` / `LB` / `LD` (Spring Data audit annotations) |
| O | Relation: Type | `@ManyToOne` or `@OneToOne` (see [Defining Relationships](#defining-relationships)) |
| P | Relation: Direction | `unidirectional` / `bidirectional` |
| Q | Relation: Source Var Name | Java field name for the relation, on the owning (source) side (camelCase) |
| R | Relation: Source Object Var Name | Optional override for the object variable name used in generated code; falls back to column Q when blank |
| S | Relation: Target Table | Referenced table name |
| T | Relation: Target Column | Referenced column name (usually `ID`) |
| U | Relation: Target Var Name | Reverse reference field name (bidirectional only) |
| V | Relation: Eager | blank = lazy (default) / `○` = eager |
| W–AF | index1–10 | Assign sequential integers to define columns in an index in that order |
| AG | Notes | Comments (not used in generation) |
| AH | Column Display Name (Default Lang) | Display name, default language |
| AI–AK | Column Display Name (Additional Lang 1–3) | Language-specific display names |

Table-level (rather than column-level) display names are set separately, in the **Table List** sheet.

Note: F (Surrogate key) and G (natural key) used to be a single combined column (`S`/`U` letter codes) in
templates prior to v5.0.0; they were split into two independently `○`-marked columns in v5.0.0.

---

## Table Entry Patterns

### Basic Structure

Each table is a group of rows sharing the same Table Name (column A).

```
Table Name  | Column       | DataType    | (D) | ... | F (Surr.) | G (Nat.) | H (null) | I (auto) | ...
------------|--------------|-------------|-----|-----|-----------|----------|----------|----------|
MY_TABLE    | ID           | DT_SERIAL   | ○   |     | ○         |          |          | ○        |
MY_TABLE    | CODE         | DT_CODE     | ○   |     |           | ○        |          |          |
MY_TABLE    | NAME         | DT_ACC_NAME | ○   |     |           |          | ○        |          |
```

### Surrogate Key (Required in Every Table)

Every table must have exactly one column marked as the surrogate key; it must not be nullable. By convention
this is written as the table's first row, but row order itself is not enforced by the tool.

| Table Name | Column | dataType | F (Surrogate key) | I (Auto Numbering) |
| --- | --- | --- | --- | --- |
| MY_TABLE | ID | DT_SERIAL | ○ | ○ |

- `F = ○`: Treated as the primary key (surrogate key). A table with zero or with more than one column marked
  `○` in F is a load error.
- `I = ○`: Auto-incremented by a DB sequence

### Unique Key (Natural Key)

Mark columns that serve as natural keys with `○` in column G.
Composite unique keys are not currently supported (all natural-key columns of a table are combined into a
single unique constraint).

### Nullable Column

Columns with `H (nullable) = ○` are nullable and receive `@Nullable` in generated code.

---

## Java Only (Column E)

Setting `E = ○` skips DB column creation and generates only an Entity/Record property.

- When `E = ○`, columns F (Surrogate key) onward must be left blank.
- Use this to define non-persisted properties such as values derived from another table.

---

## Auto Numbering (Column I) / Force Numbering (Column J)

### Auto Numbering

When `I = ○`, the value is automatically set on insert if it is null.

| Type | Assigned Value |
| --- | --- |
| `int` / `long` | Sequential number starting from 1 |
| Timestamp types | Current date-time |
| `boolean` | `false` |
| Other | Error |

### Force Numbering

When `J = ○`, the same logic as Auto Numbering applies, but the value is **overwritten even if already set**.

---

## Auto Update (Column K) / Force Update (Column L)

### Auto Update

When `K = ○`, the value is automatically set on update if it is null.

| Type | Set Value |
| --- | --- |
| Timestamp types | Current date-time at update |
| `boolean` / `DT_FLG` | `false` (`FlgEnum.FALSE`) |
| Other | Error |

### Force Update

When `L = ○`, the same logic as Auto Update applies, but the value is **overwritten even if already set**.

---

## Group Identifier (Column M)

Setting `M = ○` marks the column as the group identifier.

Various settings can reference a group identifier column by name, but the key of the group table itself
(e.g., `GROUP.ID`) is named `ID`, not the group column's configured name. To treat that `ID` as the group
identifier in the same way, set `M = ○`. Normally only one column per system is marked this way.

---

## Spring Audit (Column N)

Attaches Spring Data audit annotations to the field.

| Value | Annotation | Meaning |
| --- | --- | --- |
| `CB` | `@CreatedBy` | Created by |
| `CD` | `@CreatedDate` | Created date |
| `LB` | `@LastModifiedBy` | Last modified by |
| `LD` | `@LastModifiedDate` | Last modified date |

---

## Defining Relationships

Relationships between tables are defined in columns O through V.

Column O (Relation: Type) only accepts `@ManyToOne` or `@OneToOne` as input. `@OneToMany` is never entered
directly — it is generated automatically on the referenced table's side of a `bidirectional` `@ManyToOne`
relation (see below). The FK Types reference sheet in the workbook lists only these two supported shapes;
one-to-many and many-to-many relations from the "many" side are not supported by the framework.

### @ManyToOne (Most Common)

```
Table     | Column   | dataType  | ... | O            | P               | Q        | R   | S           | T   | U            | V |
----------|----------|-----------|-----|--------------|-----------------|----------|-----|-------------|-----|--------------|---|
MY_TABLE  | GROUP_ID | DT_SERIAL | ... | @ManyToOne   | unidirectional  | groupVar |     | GROUP_TABLE | ID  |              |   |
```

- `@ManyToOne`: Many-to-one (defined on the table that owns the foreign key)
- `unidirectional`: One-way reference only. Use this in most cases.

Generated Entity code (excerpt):

```java
@ManyToOne
@JoinColumn(name = "GROUP_ID")
private GroupEntity groupVar;
```

### @ManyToOne (Bidirectional)

To navigate the association in both directions, set the direction to `bidirectional`
and provide the reverse reference field name in column U:

```
... | @ManyToOne | bidirectional | parentVar | | PARENT_TABLE | ID | childListVar | |
```

The referenced Entity (PARENT_TABLE) gets:

```java
@OneToMany(mappedBy = "parentVar")
private List<ChildEntity> childListVar;
```

### Relation: Source Object Var Name (Column R)

Optional. When set, this overrides the object variable name used for the relation in generated code; when
left blank, the value from column Q (Relation: Source Var Name) is used instead.

### @OneToOne

One-to-one relationship. Specified the same way as `@ManyToOne`.

### Relation: Eager (Column V)

Column V is blank for lazy (default) fetch; set `○` for eager fetch.

Eager is acceptable when the relation clearly points to a parent entity in the DB structure and is
`unidirectional`. Use lazy when self-referencing or when a circular reference through other entities
is possible.

---

## Index (Columns W–AF)

Assign integers starting from 1 in columns W–AF (index1–10) to create an index with the columns
ordered by those numbers.

Example: creating a composite index `(COL_A, COL_B)` on `MY_TABLE`:

| Column | W (index1) | X (index2) |
| --- | --- | --- |
| COL_A | 1 | |
| COL_B | 2 | |

To define multiple indexes, use index2 (column X) onward in the same way.

---

## DataType Check Column (Column D) VLOOKUP Formula

Column D automatically verifies that the referenced DataType exists in the DataType Definition sheet.
A value of `×` means the DataType is undefined and generation will fail.

Formula within table bounds (auto-populated by copy):

```
=IF(NOT(ISNA(VLOOKUP(Table7[[#This Row],[dataType]], 'dataType Definition'!A:A, 1,FALSE))), "○", "×")
```

For rows outside the named table range, use a cell reference instead:

```
=IF(NOT(ISNA(VLOOKUP(C{row}, 'dataType Definition'!A:A, 1,FALSE))), "○", "×")
```

---

## DB Common Item Definition Sheet

The **DB Common Item Definition** sheet defines columns that are applied to **every** table automatically, instead
of repeating the same columns on each table in DB Definition. It has the exact same column layout described above
(columns A–AK), with one difference: column A (Table Name) is left blank, since the row applies to all tables
rather than one specific table.

This is the conventional place to define cross-cutting columns such as audit columns (created/last-modified by and
at), a soft-delete flag, and the optimistic-lock version column — see
[General Settings Sheet](page?id=excel-format/general-settings&lang=en) for how those features are enabled by
name. For a working example, see
[qiita-data-viewer's `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format).

---

## Authentication Tables Required by ecuacion-splib

The blank template's DB Item Definition sheet ships empty — it does not come pre-populated with the
authentication tables (`ACC`, `ACC_ADMIN`, etc.) that `ecuacion-splib`-based projects need. For a filled-in
example that includes them, see a real project's Excel file (e.g.
[qiita-data-viewer's `excel-format/`](https://github.com/ecuacion-jp/qiita-data-viewer/tree/main/excel-format)).
