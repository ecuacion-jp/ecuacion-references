## `ExcelTableException`

The common superclass (`abstract`, `extends ViolationException`) of exceptions thrown when a
table-related error occurs in `ecuacion-util-excel-table`. It is `abstract`, so it is never
thrown directly — one of the concrete subclasses below always is.

### Concrete Subclasses

| Exception class | Thrown when |
| --- | --- |
| `NumberOfTableHeadersDifferException` | The number of header columns found differs from the number of expected header labels. |
| `TableHeaderTitleWrongException` | A header cell's label differs from the expected label at that position. |
| `SheetNotExistException` | The specified sheet name does not exist in the Excel file. |
| `CellContainsErrorException` | A cell contains an error value (e.g. `#NUM!`, `#DIV/0!`). |
| `ExternalWorkbookNotFoundException` | A formula references an external Excel file that cannot be found while evaluating it. |
| `ExcelFeatureNotImplementedException` | The underlying Excel manipulation library (Apache POI) does not support a feature used by a formula. |
| `FormulaEvaluationUnknownErrorException` | An unrecognized error occurs while evaluating a formula. |
| `HeaderCellIsBlankException` | A header cell is blank without being part of a merged region. |
| `ColumnSizeIsZeroException` | The auto-detected column size of the table is zero. |
| `FarLeftHeaderLabelNotFoundException` | Auto-detecting the table's start row fails to find the expected far-left header label. |

All ten live in `jp.ecuacion.util.excel.exception` and extend `ExcelTableException`.

### Catching the Exception

Each failure case has its own exception class, so `catch` by type instead of branching on a
string `messageId`:

```java
import jp.ecuacion.util.excel.exception.ExcelTableException;
import jp.ecuacion.util.excel.exception.SheetNotExistException;

try {
    List<List<String>> data = reader.read("/path/to/file.xlsx");
} catch (SheetNotExistException ex) {
    // handle this specific case differently
} catch (ExcelTableException ex) {
    // catches any of the other cases generically
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

Internally, this context (and an optional cause) is attached via the `workbook()`/`sheet()`/
`cell()`/`cause()` fluent methods, all inherited from the abstract `ExcelTableException` base
class:

```java
throw new SheetNotExistException(sheetName).cause(originalException);
```

Note: `ExcelTableException`'s constructor is `protected`, so — unlike in earlier versions —
application code cannot construct an arbitrary `ExcelTableException` with a custom `messageId`
directly. Only the ten concrete subclasses above can be thrown.

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
