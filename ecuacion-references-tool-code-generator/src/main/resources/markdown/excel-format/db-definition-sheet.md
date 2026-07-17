The DB Definition sheet defines the database tables and columns.
This sheet drives the generation of Entity, Repository, BL, and related classes.

## Column Layout

Table name: `テーブル7`, range: `A5:AI{last row}`

| Column | Field | Description |
| --- | --- | --- |
| A | Table Name | DB table name (UPPER_SNAKE_CASE). Repeat for every row of the same table |
| B | Column Name | DB column name (UPPER_SNAKE_CASE) |
| C | dataType | DataType name in `DT_XXXX` format |
| D | DataType Check | VLOOKUP formula for auto-validation (copy to apply) |
| E | Java Only | `○` = no DB column created; only Entity/Record property is generated |
| F | PK / UK | `S` = surrogate key (PK), `U` = unique key |
| G | Nullable | `○` = NULL allowed (blank = NOT NULL) |
| H | Auto-assign | `○` = auto-sets a value when null on insert |
| I | Force-assign | `○` = always overwrites with assigned value on insert |
| J | Auto-update | `○` = auto-sets a value when null on update |
| K | Force-update | `○` = always overwrites with updated value on update |
| L | Group Identifier | `○` = marks this column as the group identifier |
| M | Spring Audit | `CB` / `CD` / `LB` / `LD` (Spring Data audit annotations) |
| N | Relation: Kind | `@ManyToOne` / `@OneToOne` / `@OneToMany` |
| O | Relation: Direction | `unidirectional` / `bidirectional` |
| P | Relation: Source Variable | Java field name (camelCase) |
| Q | Relation: Target Table | Referenced table name |
| R | Relation: Target Column | Referenced column name (usually `ID`) |
| S | Relation: Target Variable | Reverse reference field name (bidirectional only) |
| T | Relation: Eager | blank = lazy (default) / `○` = eager |
| U–AD | index1–10 | Assign sequential integers to define columns in an index in that order |
| AE | Notes | Comments (not used in generation) |
| AF | Column Display Name (default lang) | English display name |
| AG–AI | Column Display Name (lang 1–3) | Language-specific display names |

---

## Table Entry Patterns

### Basic Structure

Each table is a group of rows. The first row of every table must be the surrogate key (`S`).

```
Table Name  | Column       | DataType    | (D) | ... | PK/UK | null | incr | ...
------------|--------------|-------------|-----|-----|-------|------|------|
MY_TABLE    | ID           | DT_SERIAL   | ○   |     | S     |      | ○    |
MY_TABLE    | CODE         | DT_CODE     | ○   |     | U     |      |      |
MY_TABLE    | NAME         | DT_ACC_NAME | ○   |     |       | ○    |      |
```

### Surrogate Key (Required in Every Table)

The first row of every table must define the surrogate key.

| Table Name | Column | dataType | F (PK/UK) | H (Auto-assign) |
| --- | --- | --- | --- | --- |
| MY_TABLE | ID | DT_SERIAL | S | ○ |

- `F = S`: Treated as the primary key (surrogate key)
- `H = ○`: Auto-incremented by a DB sequence

### Unique Key (Natural Key)

Mark columns that serve as natural keys with `U` in column F.
Composite unique keys are not currently supported.

### Nullable Column

Columns with `G (nullable) = ○` are nullable and receive `@Nullable` in generated code.

---

## Java Only (Column E)

Setting `E = ○` skips DB column creation and generates only an Entity/Record property.

- When `E = ○`, columns F (PK/UK) onward must be left blank.
- Use this to define non-persisted properties such as values derived from another table.

---

## Auto-assign (Column H) / Force-assign (Column I)

### Auto-assign

When `H = ○`, the value is automatically set on insert if it is null.

| Type | Assigned Value |
| --- | --- |
| `int` / `long` | Sequential number starting from 1 |
| Timestamp types | Current date-time |
| `boolean` | `false` |
| Other | Error |

### Force-assign

When `I = ○`, the same logic as auto-assign applies, but the value is **overwritten even if already set**.

---

## Auto-update (Column J) / Force-update (Column K)

### Auto-update

When `J = ○`, the value is automatically set on update if it is null.

| Type | Set Value |
| --- | --- |
| Timestamp types | Current date-time at update |
| `boolean` / `DT_FLG` | `false` (`FlgEnum.FALSE`) |
| Other | Error |

### Force-update

When `K = ○`, the same logic as auto-update applies, but the value is **overwritten even if already set**.

---

## Group Identifier (Column L)

Setting `L = ○` marks the column as the group identifier.

Various settings can reference a group identifier column by name, but the key of the group table itself
(e.g., `GROUP.ID`) is named `ID`, not `GROUP_ID`. To treat that `ID` as the group identifier in the same
way, set `L = ○`. Normally only one column per system is marked this way.

---

## Spring Audit (Column M)

Attaches Spring Data audit annotations to the field.

| Value | Annotation | Meaning |
| --- | --- | --- |
| `CB` | `@CreatedBy` | Created by |
| `CD` | `@CreatedDate` | Created date |
| `LB` | `@LastModifiedBy` | Last modified by |
| `LD` | `@LastModifiedDate` | Last modified date |

---

## Defining Relationships

Relationships between tables are defined in columns N through T.

### @ManyToOne (Most Common)

```
Table     | Column   | dataType  | ... | N            | O               | P        | Q           | R   | S | T |
----------|----------|-----------|-----|--------------|-----------------|----------|-------------|-----|---|---|
MY_TABLE  | GROUP_ID | DT_SERIAL | ... | @ManyToOne   | unidirectional  | groupVar | GROUP_TABLE | ID  |   |   |
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
and provide the reverse reference field name in column S:

```
... | @ManyToOne | bidirectional | parentVar | PARENT_TABLE | ID | childListVar | |
```

The referenced Entity (PARENT_TABLE) gets:

```java
@OneToMany(mappedBy = "parentVar")
private List<ChildEntity> childListVar;
```

### @OneToOne

One-to-one relationship. Specified the same way as `@ManyToOne`.

### Relation: Eager (Column T)

Column T is blank for lazy (default) fetch; set `○` for eager fetch.

Eager is acceptable when the relation clearly points to a parent entity in the DB structure and is
`unidirectional`. Use lazy when self-referencing or when a circular reference through other entities
is possible.

---

## Index (Columns U–AD)

Assign integers starting from 1 in columns U–AD (index1–10) to create an index with the columns
ordered by those numbers.

Example: creating a composite index `(COL_A, COL_B)` on `MY_TABLE`:

| Column | U (index1) | V (index2) |
| --- | --- | --- |
| COL_A | 1 | |
| COL_B | 2 | |

To define multiple indexes, use index2 (column V) onward in the same way.

---

## DataType Check Column (Column D) VLOOKUP Formula

Column D automatically verifies that the referenced DataType exists in the DataType Definition sheet.
A value of `×` means the DataType is undefined and generation will fail.

Formula within table bounds (auto-populated by copy):

```
=IF(NOT(ISNA(VLOOKUP(テーブル7[[#This Row],[dataType]], dataType定義!A:A, 1,FALSE))), "○", "×")
```

For rows outside the named table range, use a cell reference instead:

```
=IF(NOT(ISNA(VLOOKUP(C{row}, dataType定義!A:A, 1,FALSE))), "○", "×")
```

---

## Existing Authentication Tables

Sample Excel files already contain the authentication tables required by `ecuacion-splib`
(e.g., `ACC`, `ACC_ADMIN`). Leave these as-is and append your new tables immediately after them.
