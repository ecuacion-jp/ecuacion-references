# データ型の選択

Reader/Writer クラスは、Excel セルから取得するデータ型によって
**String 型** と **Cell 型** の 2 種類に分かれています。

## String 型（`IfDataTypeStringExcelTable`）

セルの値を `String` として取得します。数値・日付・文字列を問わず、
すべてのセルを文字列として扱います。最も一般的な選択肢です。

### 空セルの扱い：`NoDataString` enum

String 型では、空セルをどの値で表現するかを `NoDataString` enum で指定できます。

| 値 | 意味 |
| --- | --- |
| `NoDataString.NULL` | `null` を返す（デフォルト） |
| `NoDataString.EMPTY_STRING` | `""` を返す |

通常は `NoDataString.NULL` を使用してください。
`@NotEmpty` は `null` と `""` のどちらも違反として扱うため、必須チェックはどちらを返しても正しく機能します。
一方、`@Pattern` などの形式バリデーターは `null` をスキップしますが、`""` には適用されます。
空セルに `""` を返すと、未入力のセルに対して「形式が不正です」という誤ったエラーが
表示される可能性があります。`null` を返すことでこの問題を回避できます。

変更する場合は fluent setter で指定します。

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "年齢"})
    .noDataString(NoDataString.EMPTY_STRING);  // 空セルを "" にしたい場合
```

### 日付・数値セルの文字列変換

数値セルや日付セルも文字列として取得できます。日付のフォーマットは
デフォルトで `yyyy-MM-dd` です。列単位または全体でフォーマットを変更できます。

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "登録日"})
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .columnDateTimeFormat(2, DateTimeFormatter.ofPattern("MM/dd"));
```

`columnDateTimeFormat` の第 1 引数は 1 始まりの列番号（テーブル内での相対位置ではなく
Excel シート上の絶対列番号）です。

## Cell 型（`IfDataTypeCellExcelTable`）

Apache POI の `Cell` オブジェクトとして取得します。
セルの型情報（数値・文字列・日付）やスタイル情報（背景色・フォントなど）も
参照したい場合に使用します。

```java
import jp.ecuacion.util.excel.table.reader.concrete.CellOneLineHeaderExcelTableReader;
import org.apache.poi.ss.usermodel.Cell;
import java.util.List;

CellOneLineHeaderExcelTableReader reader = new CellOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "金額"});

List<List<Cell>> data = reader.read("/path/to/file.xlsx");

for (List<Cell> row : data) {
    Cell nameCell   = row.get(0);
    Cell amountCell = row.get(1);
    // セルの型に応じて値を取得
    String name   = nameCell.getStringCellValue();
    double amount = amountCell.getNumericCellValue();
}
```

文字列変換が必要な場合は `ExcelReadUtil.getStringFromCell(cell, dateTimeFormatter)` を利用できます。

## どちらを使うべきか

| 用途 | 推奨 |
| --- | --- |
| データをそのまま読み込んで処理したい | **String 型** |
| Bean に変換して Jakarta Validation を使いたい | **String 型** |
| セルのスタイルや型情報も必要 | **Cell 型** |
| 数値として加算・集計したい | **Cell 型**（String 型で読んで `parseInt` する方法はセルのフォーマット次第で失敗するため） |
