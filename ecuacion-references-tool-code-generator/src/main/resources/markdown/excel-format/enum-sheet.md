The Enum Definition sheet defines the enumeration values for DataTypes of type `ENUM`.

## When to Use

If you define a DataType with type `ENUM` in the DataType Definition sheet,
define the values that Enum can take in this sheet.

## Sheet Structure

Each row defines one Enum value. There is no separate "class name" row: the DataType Name column is repeated
on every row belonging to the same Enum, the same way the Table Name column repeats in the DB Definition sheet.
The Enum class name itself is not entered directly — it is auto-generated from the DataType name (strip the
`DT_` prefix, convert to UpperCamelCase, and append `Enum`).

| Column | Field | Description |
| --- | --- | --- |
| DataType Name | DataType Name | The `DT_XXXX` DataType this value belongs to. Repeated on every row of the group |
| code | code | The value stored in the DB column (e.g., `1`, `2`, `3`) |
| varName | varName | The generated Java enum constant name (e.g., `ACTIVE`) |
| Java Only | Java Only | Marker column (present in the sheet; not currently used by the code generator) |
| Notes | Notes | Comments (not used in generation) |
| Display Name (Default Lang) | Display Name (Default Lang) | Display name shown in the UI, default language |
| Display Name (Additional Lang 1-3) | Display Name (Additional Lang 1-3) | Display name for each additional supported language |

### Example

For a DataType `DT_STATUS` with type `ENUM`, create rows like this in the Enum Definition sheet:

| DataType Name | code | varName | Display Name (Default Lang) | Display Name (Additional Lang 1) |
| --- | --- | --- | --- | --- |
| DT_STATUS | 1 | ACTIVE | Active | 有効 |
| DT_STATUS | 2 | INACTIVE | Inactive | 無効 |
| DT_STATUS | 3 | DELETED | Deleted | 削除済み |

This produces an Enum class named `StatusEnum` (derived from `DT_STATUS`).

### Generated Code Example

```java
public enum StatusEnum {
    ACTIVE("1"),
    INACTIVE("2"),
    DELETED("3");

    private final String code;

    StatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
```

A `Converter` class for DB ↔ Enum conversion is also generated automatically:

```java
@Converter(autoApply = true)
public class StatusEnumConverter implements AttributeConverter<StatusEnum, String> {
    // DB value ⇔ Enum conversion
}
```

## DB Stored Values

The DB column stores the defined DB value (e.g., `"1"`, `"2"`, `"3"`), not the Enum name.
Currently, only `String` DB values are supported.

## Wiring Flow: Enum → DataType → Column

1. **DataType Definition sheet**: Define a DataType with type `ENUM` (e.g., `DT_STATUS`)
2. **Enum Definition sheet**: Define the values for that Enum (e.g., entries in `StatusEnum`)
3. **DB Definition sheet**: Assign `DT_STATUS` as the DataType for a column

The tool combines these three sheets to generate the Enum class, Converter, and DataType validator.
