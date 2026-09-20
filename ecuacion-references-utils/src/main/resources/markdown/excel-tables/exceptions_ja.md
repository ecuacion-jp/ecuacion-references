## `ExcelTableException`

`ecuacion-util-excel-table` でテーブル関連のエラーが発生した場合にスローされる例外群の共通スーパークラスです（`abstract`、`ViolationException` を継承）。`abstract` なので直接スローされることはなく、必ず下記のいずれかの具象サブクラスがスローされます。

### 具象サブクラス

| 例外クラス | スローされる場面 |
| --- | --- |
| `NumberOfTableHeadersDifferException` | ヘッダー列数が期待値と異なる |
| `TableHeaderTitleWrongException` | ヘッダーのラベルが期待値と異なる |
| `SheetNotExistException` | 指定したシート名がExcelファイルに存在しない |
| `CellContainsErrorException` | セルがエラー値（`#NUM!`、`#DIV/0!`等）を含む |
| `ExternalWorkbookNotFoundException` | 数式が参照する外部Excelファイルが評価時に見つからない |
| `ExcelFeatureNotImplementedException` | excel操作ライブラリ（Apache POI）が数式で使われている機能に対応していない |
| `FormulaEvaluationUnknownErrorException` | 数式評価中に未分類のエラーが発生した |
| `HeaderCellIsBlankException` | ヘッダーセルが空（結合セルの範囲外） |
| `ColumnSizeIsZeroException` | 自動検出されたテーブルの列数がゼロ |
| `FarLeftHeaderLabelNotFoundException` | テーブル開始行の自動検出時、想定される左端ヘッダー文字列が見つからない |

10クラスとも `jp.ecuacion.util.excel.exception` パッケージに属し、`ExcelTableException` を継承しています。

### キャッチ方法

失敗ケースごとに専用の例外クラスがあるため、messageId文字列で分岐するのではなく、
型でcatchします。

```java
import jp.ecuacion.util.excel.exception.ExcelTableException;
import jp.ecuacion.util.excel.exception.SheetNotExistException;

try {
    List<List<String>> data = reader.read("/path/to/file.xlsx");
} catch (SheetNotExistException ex) {
    // このケースだけ個別に処理したい場合
} catch (ExcelTableException ex) {
    // 残りのケースを包括的にキャッチ
    String messageId = ex.getMessageId();
    // ex.getWorkbook(), ex.getSheet(), ex.getCell() でコンテキスト情報を取得できる
    System.err.println("Excel エラー: " + messageId);
}
```

### コンテキスト情報

`ExcelTableException` はエラーが発生した位置情報を保持できます。

```java
Workbook wb = ex.getWorkbook(); // null の場合あり
Sheet    sh = ex.getSheet();    // null の場合あり
Cell     c  = ex.getCell();     // null の場合あり
```

このコンテキスト情報（および任意でcause）は、abstractな`ExcelTableException`基底クラスから継承した `workbook()`・`sheet()`・`cell()`・`cause()` のfluentメソッドで設定します。

```java
throw new SheetNotExistException(sheetName).cause(originalException);
```

注意: `ExcelTableException` のコンストラクタは `protected` になったため、以前のバージョンとは異なり、アプリケーション側で任意の `messageId` を指定して `ExcelTableException` を直接構築することはできません。スローできるのは上記10個の具象サブクラスのみです。

## `LoopBreakException`

反復処理ループを中断するために使う `RuntimeException` です。
外部のループ処理から読み込みを途中で止めたい場合に throw します。

```java
// IterableReader の中で LoopBreakException をスローするとループが終了する
try (ExcelTableReader.IterableReader<String> iter = reader.getIterable(filePath)) {
    for (List<String> row : iter) {
        if ("END".equals(row.get(0))) {
            throw new LoopBreakException();
        }
        // 処理
    }
} catch (LoopBreakException ex) {
    // 正常終了として扱う
}
```
