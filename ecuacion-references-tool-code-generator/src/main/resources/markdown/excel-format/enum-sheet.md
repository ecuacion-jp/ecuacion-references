The Enum Definition sheet defines the enumeration values for DataTypes of type `ENUM`.

## When to Use

If you define a DataType with type `ENUM` in the DataType Definition sheet,
define the values that Enum can take in this sheet.

## Sheet Structure

The sheet contains one table per Enum class.
The first row of each table is the class name; subsequent rows are the value definitions.

### Example

For a DataType `DT_STATUS` with type `ENUM`, create a table like this in the Enum Definition sheet:

| Enum Class Name | DB Value | Display Name (English) | Display Name (Japanese) |
| --- | --- | --- | --- |
| StatusEnum | | | |
| ACTIVE | 1 | Active | 有効 |
| INACTIVE | 2 | Inactive | 無効 |
| DELETED | 3 | Deleted | 削除済み |

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
