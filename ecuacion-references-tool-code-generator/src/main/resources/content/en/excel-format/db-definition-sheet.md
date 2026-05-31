# DB Definition Sheet

The DB Definition sheet defines the database tables and columns.
This sheet drives the generation of Entity, Repository, BL, and related classes.

## Column Layout

Table name: `テーブル7`, range: `A5:AB{last row}`

| Column | Field | Description |
| --- | --- | --- |
| A | Table Name | DB table name (UPPER_SNAKE_CASE). Repeat for every row of the same table |
| B | Display Name (default lang) | English display name |
| C | Column Name | DB column name (UPPER_SNAKE_CASE) |
| D | dataType | DataType name in `DT_XXXX` format |
| E | DataType Check | VLOOKUP formula for auto-validation (copy to apply) |
| G | PK / UK | `S` = surrogate key (PK), `U` = unique key |
| H | Nullable | `○` = NULL allowed (blank = NOT NULL) |
| I | Auto-increment | `○` = DB sequence-based auto-numbering |
| O | Relation: Kind | `@ManyToOne` / `@OneToOne` / `@OneToMany` |
| P | Relation: Direction | `unidirectional` / `bidirectional` |
| Q | Relation: Source Variable | Java field name (camelCase) |
| R | Relation: Target Table | Referenced table name |
| S | Relation: Target Column | Referenced column name (usually `ID`) |
| T | Relation: Target Variable | Reverse reference field name (bidirectional only) |
| Y | Notes | Comments (not used in generation) |
| Z | Display Name (lang 1) | Japanese display name |

---

## Table Entry Patterns

### Basic Structure

Each table is a group of rows. The first row of every table must be the surrogate key (`S`).

```
Table Name  | Display | Column | DataType  | (E) |   | PK/UK | null | incr | ...
------------|---------|--------|-----------|-----|---|-------|------|------|
MY_TABLE    | name    | ID     | DT_SERIAL | ○   |   | S     |      | ○    |
MY_TABLE    |         | CODE   | DT_CODE   | ○   |   | U     |      |      |
MY_TABLE    |         | NAME   | DT_ACC_NAME | ○ |   |       | ○    |      |
```

### Surrogate Key (Required in Every Table)

The first row of every table must define the surrogate key.

| Table Name | Display | Column | dataType | G (PK/UK) | I (Auto-incr) |
| --- | --- | --- | --- | --- | --- |
| MY_TABLE | name | ID | DT_SERIAL | S | ○ |

- `G = S`: Treated as the primary key (surrogate key)
- `I = ○`: Auto-incremented by a DB sequence

### Unique Key (Natural Key)

Mark columns that serve as natural keys with `U` in column G.
Composite unique keys are not currently supported.

### Nullable Column

Columns with `H (nullable) = ○` are nullable and receive `@Nullable` in generated code.

---

## Defining Relationships

Relationships between tables are defined in columns O through T.

### @ManyToOne (Most Common)

```
Table     | Display   | Column   | dataType  | ... | O            | P               | Q        | R           | S   | T |
----------|-----------|----------|-----------|-----|--------------|-----------------|----------|-------------|-----|---|
MY_TABLE  | group ID  | GROUP_ID | DT_SERIAL | ... | @ManyToOne   | unidirectional  | groupVar | GROUP_TABLE | ID  |   |
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
and provide the reverse reference field name in column T:

```
... | @ManyToOne | bidirectional | parentVar | PARENT_TABLE | ID | childListVar | ...
```

The referenced Entity (PARENT_TABLE) gets:

```java
@OneToMany(mappedBy = "parentVar")
private List<ChildEntity> childListVar;
```

### @OneToOne

One-to-one relationship. Specified the same way as `@ManyToOne`.

---

## DataType Check Column (Column E) VLOOKUP Formula

Column E automatically verifies that the referenced DataType exists in the DataType Definition sheet.
A value of `×` means the DataType is undefined and generation will fail.

Formula within table bounds (auto-populated by copy):

```
=IF(NOT(ISNA(VLOOKUP(テーブル7[[#This Row],[dataType]], dataType定義!A:A, 1,FALSE))), "○", "×")
```

For rows outside the named table range, use a cell reference instead:

```
=IF(NOT(ISNA(VLOOKUP(D{row}, dataType定義!A:A, 1,FALSE))), "○", "×")
```

---

## Existing Authentication Tables

Sample Excel files already contain the authentication tables required by `ecuacion-splib`
(e.g., `ACC`, `ACC_ADMIN`). Leave these as-is and append your new tables immediately after them.
