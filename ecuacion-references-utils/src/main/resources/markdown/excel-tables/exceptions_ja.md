## `ExcelTableException`

Excel 読み書き中に発生するアプリケーションエラーを表す例外クラスです。
`ViolationException` を継承しており、メッセージ ID と引数でエラー内容を保持します。

### 主なスロー場面

| 状況 | メッセージ ID |
| --- | --- |
| ヘッダー列数が期待値と異なる | `jp.ecuacion.util.excel.NumberOfTableHeadersDiffer.message` |
| ヘッダーのラベルが期待値と異なる | `jp.ecuacion.util.excel.TableHeaderTitleWrong.message` |
| テーブル開始位置が見つからない | `jp.ecuacion.util.excel.reader.FarLeftHeaderLabelNotFound.message` |
| ヘッダーセルが空（マルチヘッダーで） | `jp.ecuacion.util.excel.reader.HeaderCellIsBlank.message` |

### キャッチ方法

```java
import jp.ecuacion.util.excel.exception.ExcelTableException;

try {
    List<List<String>> data = reader.read("/path/to/file.xlsx");
} catch (ExcelTableException ex) {
    String messageId = ex.getMessageId();
    // ex.getWorkbook(), ex.getSheet(), ex.getCell() でコンテキスト情報を取得できる
    System.err.println("Excel エラー: " + messageId);
}
```

### コンテキスト情報

`ExcelTableException` はエラーが発生した位置情報を保持できます。
ライブラリ内部では `workbook()`・`sheet()`・`cell()` で設定されます。

```java
Workbook wb = ex.getWorkbook(); // null の場合あり
Sheet    sh = ex.getSheet();    // null の場合あり
Cell     c  = ex.getCell();     // null の場合あり
```

`ExcelTableException` 自体を構築・スローする場合はメソッドチェーンで設定します。

```java
throw new ExcelTableException("my.error.message.id", argValue)
    .cell(cell)
    .cause(originalException);
```

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
