このページでは各 Reader クラスの使い方を説明します。

## Reader クラス一覧

| クラス名 | データ型 | テーブル形式 |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableReader` | String | Header（1行） |
| `StringOneLineHeaderExcelTableToBeanReader` | String | Header（1行）・Bean変換 |
| `StringHeaderExcelTableReader` | String | Header（複数行） |
| `StringHeaderExcelTableToBeanReader` | String | Header（複数行）・Bean変換 |
| `StringFreeExcelTableReader` | String | Free |
| `TypedOneLineHeaderExcelTableReader` | Typed | Header（1行） |
| `TypedOneLineHeaderExcelTableToBeanReader` | Typed | Header（1行）・Bean変換 |
| `TypedHeaderExcelTableReader` | Typed | Header（複数行） |
| `TypedHeaderExcelTableToBeanReader` | Typed | Header（複数行）・Bean変換 |
| `CellOneLineHeaderExcelTableReader` | Cell | Header（1行） |
| `CellHeaderExcelTableReader` | Cell | Header（複数行） |
| `CellFreeExcelTableReader` | Cell | Free |

> **Cell 型に ToBeanReader がない理由：** Bean 変換は値をもとに型変換する仕組み（`StringExcelTableBean` または `TypedExcelTableBean`）のため、Cell 型との組み合わせは提供していません。Cell 型が必要な場面ではスタイルや型情報をそのまま扱う方が自然なためです。

## 共通：`read()` メソッド

すべての Reader クラスは以下の 2 つの `read()` メソッドを持ちます。

```java
// ファイルパスから直接読み込む（ファイルを自動で開閉する）
List<List<T>> read(String filePath) throws IOException;

// 既に開いている Workbook から読み込む
List<List<T>> read(Workbook workbook) throws IOException;
```

`T` はデータ型（`String` または `Cell`）です。
戻り値の外側の `List` が行、内側の `List` が各行のセル値を表します。
ヘッダー行は戻り値に含まれません（複数行の場合はすべてのヘッダー行が除去されます）。

## 共通：fluent setter 一覧

全 Reader クラスで利用できる fluent setter です。

| setter | 型 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `tableStartRowNumber(Integer)` | Integer または null | null（自動検出） | テーブル開始行（1始まり） |
| `tableStartColumnNumber(int)` | int | 1 | テーブル開始列（1始まり） |
| `tableRowSize(Integer)` | Integer または null | null（自動検出） | 読み込む最大行数 |
| `tableColumnSize(Integer)` | Integer または null | null（自動検出） | 読み込む最大列数 |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | boolean | false | ヘッダーの追加列を無視 |
| `withVerticalAndHorizontalOpposite(boolean)` | boolean | false | 縦横反転テーブル対応 |

## `StringOneLineHeaderExcelTableReader`

ヘッダー1行のテーブルを String 型で読み込む最も一般的なクラスです。

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"商品コード", "商品名", "価格"});

List<List<String>> data = reader.read("/path/to/file.xlsx");
// data.get(0) → ["A001", "テスト商品", "1000"]
// data.get(1) → ["A002", "サンプル", null]  ← 空セルは null
```

### テーブル位置の明示指定

同じシートに複数のテーブルがある場合など、位置を明示します。

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "金額"})
    .tableStartRowNumber(5)     // 5 行目からテーブル開始
    .tableStartColumnNumber(3); // C 列からテーブル開始
```

### String 型固有の setter

| setter | デフォルト | 説明 |
| --- | --- | --- |
| `noDataString(NoDataString)` | `NoDataString.NULL` | 空セルの値 |
| `defaultDateTimeFormat(DateTimeFormatter)` | `yyyy-MM-dd` | 全列の日付フォーマット |
| `columnDateTimeFormat(int, DateTimeFormatter)` | `defaultDateTimeFormat` の値にフォールバック | 特定列の日付フォーマット（列番号は 1 始まりの絶対値）。未設定の列は `defaultDateTimeFormat` が適用される |

## `StringHeaderExcelTableReader`

ヘッダーが 2 行以上のテーブルを String 型で読み込むクラスです。
`String[][]` でヘッダーを指定します。

```java
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    });

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

Excel 上でセルが結合（マージ）されていても、自動的に展開して検証します。

## `StringFreeExcelTableReader`

ヘッダーなし・任意位置のテーブルを String で読み込みます。

```java
StringFreeExcelTableReader reader = new StringFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(2)    // 2 行目から開始
    .tableColumnSize(3);       // 3 列だけ読む

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

`tableStartRowNumber` を省略した場合は 1 行目から読み始めます。
データが存在する限り読み続け、全列が空の行で終了します。

> **ToBean 変換について:** Free 形式には ToBeanReader が存在しません。
> ToBeanReader は `@ExcelColumn` アノテーションのヘッダーラベルと Excel のヘッダー行を
> 照合することで列と Bean フィールドを対応付けるため、ヘッダーを持たない Free 形式では
> この仕組みを利用できないためです。

## `TypedOneLineHeaderExcelTableReader`

ヘッダー1行のテーブルを読み込み、各セルの値を文字列に変換せず、
ネイティブな Java 型（`String`、`Double`、`LocalDate`、`LocalDateTime`、
`Boolean`、`null`）として返します。セルの型からどの Java 型に変換されるかは
[データ型の選択](/public/showMarkdown/page?id=excel-tables/data-types&lang=ja)の変換表を参照してください。

```java
import java.time.LocalDate;
import java.util.List;

TypedOneLineHeaderExcelTableReader reader = new TypedOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "得点", "誕生日"});

List<List<Object>> data = reader.read("/path/to/file.xlsx");

String name      = (String) data.get(0).get(0);
Double score     = (Double) data.get(0).get(1);
LocalDate birth  = (LocalDate) data.get(0).get(2);
```

fluent setter は[共通の setter](#共通fluent-setter-一覧)のみで、
`noDataString` や `defaultDateTimeFormat` のような String 型固有の setter は
ありません。返される Java 型はセル自身の型・書式によって決まるためです。

## `TypedHeaderExcelTableReader`

ヘッダーが 2 行以上のテーブルを読み込むクラスです。`String[][]` で
ヘッダーを指定する点以外は `TypedOneLineHeaderExcelTableReader` と同様です。

```java
TypedHeaderExcelTableReader reader = new TypedHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    });

List<List<Object>> data = reader.read("/path/to/file.xlsx");
```

Excel 上でセルが結合（マージ）されていても、自動的に展開して検証します。

> **`TypedOneLineHeaderExcelTableToBeanReader` / `TypedHeaderExcelTableToBeanReader`：**
> ネイティブ型を保ったまま各行を `TypedExcelTableBean` のサブクラスにマッピングします
> （数値はフィールドの宣言型に応じて変換され、必要に応じて四捨五入されます）。
> 詳細は[Bean マッピング](/public/showMarkdown/page?id=excel-tables/bean-mapping&lang=ja)を参照してください。

## `CellOneLineHeaderExcelTableReader`

ヘッダー1行のテーブルをデータ型 `Cell` で読み込みます。
スタイルや数値型などセルの詳細情報が必要な場合に使います。

```java
import org.apache.poi.ss.usermodel.Cell;

CellOneLineHeaderExcelTableReader reader = new CellOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"商品名", "価格"});

List<List<Cell>> data = reader.read("/path/to/file.xlsx");

for (List<Cell> row : data) {
    Cell nameCell   = row.get(0);
    Cell priceCell  = row.get(1);
    String name  = nameCell.getStringCellValue();
    double price = priceCell.getNumericCellValue();
}
```

## `CellHeaderExcelTableReader`

ヘッダーが 2 行以上のテーブルをデータ型 `Cell` で読み込みます。
`String[][]` でヘッダーを指定します。

```java
CellHeaderExcelTableReader reader = new CellHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    });

List<List<Cell>> data = reader.read("/path/to/file.xlsx");
```

## `CellFreeExcelTableReader`

ヘッダーなし・任意位置のテーブルを `Cell` で読み込みます。

```java
CellFreeExcelTableReader reader = new CellFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(3)
    .tableStartColumnNumber(2)
    .tableRowSize(20)
    .tableColumnSize(5);

List<List<Cell>> data = reader.read("/path/to/file.xlsx");
```

## 大量データの反復読み込み：`IterableReader`

大量行を全件 `List` に収めるとメモリを圧迫する場合、
`getIterable()` で行ごとに反復処理できます。

```java
try (ExcelTableReader.IterableReader<String> iter =
        reader.getIterable("/path/to/file.xlsx")) {
    for (List<String> row : iter) {
        // 1 行ずつ処理
    }
}
```

ファイルパス版は try-with-resources で Workbook を自動クローズします。
既存の `Workbook` を渡す版（`getIterable(Workbook)`）では呼び出し元が Workbook を管理します。
