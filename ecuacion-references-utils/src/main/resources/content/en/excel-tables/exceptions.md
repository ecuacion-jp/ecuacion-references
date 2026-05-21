# Exception Handling

## `ExcelTableException`

An exception representing application-level errors that occur during
Excel read/write operations. It extends `ViolationException` and holds
the error as a message ID with arguments.

### When It Is Thrown

| Situation | Message ID |
| --- | --- |
| Header column count differs from expected | `jp.ecuacion.util.excel.NumberOfTableHeadersDiffer.message` |
| Header label does not match expected | `jp.ecuacion.util.excel.TableHeaderTitleWrong.message` |
| Table start position not found | `jp.ecuacion.util.excel.reader.FarLeftHeaderLabelNotFound.message` |
| Header cell is blank (multi-row header) | `jp.ecuacion.util.excel.reader.HeaderCellIsBlank.message` |

### Catching the Exception

```java
import jp.ecuacion.util.excel.exception.ExcelTableException;

try {
    List<List<String>> data = reader.read("/path/to/file.xlsx");
} catch (ExcelTableException ex) {
    String messageId = ex.getMessageId();
    // ex.getWorkbook(), ex.getSheet(), ex.getCell() provide context
    System.err.println("Excel error: " + messageId);
}
```

### Context Information

`ExcelTableException` can carry location context for the error.

```java
Workbook wb = ex.getWorkbook(); // may be null
Sheet    sh = ex.getSheet();    // may be null
Cell     c  = ex.getCell();     // may be null
```

When constructing and throwing your own `ExcelTableException`:

```java
throw new ExcelTableException("my.error.message.id", argValue)
    .cell(cell)
    .cause(originalException);
```

## `LoopBreakException`

A `RuntimeException` used to break out of an iteration loop.
Throw it inside an `IterableReader` loop to stop reading early:

```java
try (ExcelTableReader.IterableReader<String> iter = reader.getIterable(filePath)) {
    for (List<String> row : iter) {
        if ("END".equals(row.get(0))) {
            throw new LoopBreakException();
        }
        // process row
    }
} catch (LoopBreakException ex) {
    // treat as normal termination
}
```
