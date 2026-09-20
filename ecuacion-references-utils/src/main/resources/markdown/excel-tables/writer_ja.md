このページでは各 Writer クラスの使い方を説明します。

## Writer クラス一覧

| クラス名 | データ型 | テーブル形式 |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header（1行） |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header（1行）・Bean から書き込み |
| `StringHeaderExcelTableWriter` | String | Header（複数行） |
| `StringHeaderExcelTableFromBeanWriter` | String | Header（複数行）・Bean から書き込み |
| `StringFreeExcelTableWriter` | String | Free |
| `TypedHeaderExcelTableWriter` | Typed | Header（複数行） |
| `TypedOneLineHeaderExcelTableFromBeanWriter` | Typed | Header（1行）・Bean から書き込み |
| `TypedHeaderExcelTableFromBeanWriter` | Typed | Header（複数行）・Bean から書き込み |
| `CellOneLineHeaderExcelTableWriter` | Cell | Header（1行） |
| `CellHeaderExcelTableWriter` | Cell | Header（複数行） |
| `CellFreeExcelTableWriter` | Cell | Free |

## 書き込みの仕組み：テンプレートファイル

Writer クラスはすべて、**テンプレート Excel ファイルを元に書き込み**を行います。

1. テンプレートファイルを指定する
2. ヘッダー行をテンプレートファイルと照合し検証する
3. すべてのヘッダー行の直後の行からデータを書き込む
4. 書き込み結果を出力ファイルに保存する

テンプレートファイルには、あらかじめヘッダー行・書式・列幅などを設定しておきます。

> **注意:** `String`系Writerは`Cell.setCellValue()`のみで値を書き込み、新しい`CellStyle`は
> 一切作成しません。セルはテンプレート行が持っていたスタイルをそのまま維持します。
> 一方`Cell`系Writerと`Typed`系Writer（日付・時刻値を書く場合）は書き込み時に`CellStyle`を
> 作成するため、Excel のセルスタイル上限（64,000件）を超えないよう、行・列をまたいで
> キャッシュ・再利用します。

## 共通：`write()` メソッド

各 Writer クラスには、workbook の開き方・保存方法・クローズのタイミングが異なる
3つの`write()`オーバーロードがあります。

```java
// templateFilePath を開き、data を書き込み、destFilePath に保存してからワークブックを閉じる
void write(String templateFilePath, String destFilePath, List<List<T>> data)
    throws IOException;

// templateFilePath を開き、data を書き込んだ状態の Workbook を返す
// 保存（Workbook#write）とクローズは呼び出し側の責任
Workbook write(String templateFilePath, List<List<T>> data)
    throws IOException;

// 既に開いている Workbook（自分で開いたもの、複数回書き込みで使い回すもの等）に
// data を書き込む。所有権は呼び出し側のまま
void write(Workbook workbook, List<List<T>> data)
    throws IOException;
```

| 引数 | 説明 |
| --- | --- |
| `templateFilePath` | テンプレート Excel ファイルのパス |
| `destFilePath` | 出力先ファイルのパス |
| `workbook` | 既に開いているテンプレートの `Workbook` |
| `data` | 書き込むデータ（外側 List が行、内側 List が列） |

## `StringOneLineHeaderExcelTableWriter`

ヘッダー1行のテーブルに String データを書き込む最も一般的なクラスです。

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringOneLineHeaderExcelTableWriter;
import java.util.Arrays;
import java.util.List;

StringOneLineHeaderExcelTableWriter writer = new StringOneLineHeaderExcelTableWriter(
    "Sheet1",
    new String[] {"商品コード", "商品名", "価格"});

List<List<String>> data = Arrays.asList(
    Arrays.asList("A001", "テスト商品", "1000"),
    Arrays.asList("A002", "サンプル",   "2500")
);

writer.write(
    "/path/to/template.xlsx",
    "/path/to/output.xlsx",
    data);
```

### テーブル位置の指定

```java
StringOneLineHeaderExcelTableWriter writer = new StringOneLineHeaderExcelTableWriter(
    "Sheet1",
    new String[] {"名前", "金額"})
    .tableStartRowNumber(3)
    .tableStartColumnNumber(2);
```

## `StringHeaderExcelTableWriter`

ヘッダーが 2 行以上のテーブルに String データを書き込みます。
`String[][]` でヘッダーを指定すると、連続した同一値が水平マージ・垂直マージとして自動処理されます。

```java
StringHeaderExcelTableWriter writer = new StringHeaderExcelTableWriter(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    });
```

テンプレートファイルのヘッダー行はマルチヘッダー構造と一致している必要があります。

## `StringFreeExcelTableWriter`

ヘッダーなしテーブルに String データを書き込みます。

```java
import jp.ecuacion.util.excel.table.writer.concrete.StringFreeExcelTableWriter;

StringFreeExcelTableWriter writer = new StringFreeExcelTableWriter("Sheet1")
    .tableStartRowNumber(5)
    .tableStartColumnNumber(2);

writer.write("/path/to/template.xlsx", "/path/to/output.xlsx", data);
```

## `TypedHeaderExcelTableWriter`

ヘッダーが 2 行以上のテーブルに、ネイティブな Java 値（`String`、`Double`、
`LocalDate`、`LocalDateTime`、`Boolean` など）からなる `List<List<Object>>` を書き込みます。各値はネイティブ型のままセルに書き込まれます。例えば
`LocalDate` の値は、単なる数値や文字列ではなく日付書式のセルとして書き込まれます。`String[][]` でヘッダーを指定する点は
`StringHeaderExcelTableWriter` と同様です。

```java
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

TypedHeaderExcelTableWriter writer = new TypedHeaderExcelTableWriter(
    "Sheet1",
    new String[][] {
        {"商品情報",   "商品情報",  "発売情報"},
        {"商品コード", "商品名",    "発売日"}
    });

List<List<Object>> data = Arrays.asList(
    Arrays.asList("A001", "テスト商品", LocalDate.of(2026, 1, 15))
);

writer.write("/path/to/template.xlsx", "/path/to/output.xlsx", data);
```

`TypedOneLineHeaderExcelTableWriter` は存在しません。ヘッダーが1行の場合は、
`TypedHeaderExcelTableWriter` に要素数 1 の `String[][]` を渡してください。

日付・日時セルの書式がどのように決まるか（また `defaultDateFormat` /
`defaultDateTimeFormat` によるカスタマイズ方法）については
[From-Bean Writer](page?id=excel-tables/from-bean-writer&lang=ja)
を参照してください。Typed の通常 Writer と FromBeanWriter は、いずれも同じ
`IfDataTypeTypedExcelTableWriter` のロジックを共有しています。

## `StringOneLineHeaderExcelTableFromBeanWriter`

ヘッダー1行のテーブルに `StringExcelTableBean` のリストから書き込みます。
`@ExcelColumn` アノテーションでヘッダーラベルとフィールドを対応付けます（Bean マッピングの逆方向）。

```java
List<ProductBean> beans = ...; // StringExcelTableBean を継承した Bean のリスト

new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"商品コード", "商品名", "価格"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

日付フィールドのフォーマットを変更する場合は fluent setter で指定します。

```java
new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(...)
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .writeFromBean(...);
```

## `StringHeaderExcelTableFromBeanWriter`

ヘッダーが 2 行以上のテーブルに `StringExcelTableBean` のリストから書き込みます。
`String[][]` でヘッダーを指定します。

```java
new StringHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    })
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## `TypedOneLineHeaderExcelTableFromBeanWriter` / `TypedHeaderExcelTableFromBeanWriter`

それぞれヘッダー1行・複数行のテーブルに `TypedExcelTableBean` のリストから書き込みます。`StringOneLineHeaderExcelTableFromBeanWriter` /
`StringHeaderExcelTableFromBeanWriter` の Typed 版にあたります。
重要な違いは、各フィールドの値がネイティブ型のままセルに書き込まれること、
そして日付・日時系のフィールドが必ず日付書式のセルとして書き込まれることです。
詳細（日付セルの書式の決まり方やカスタマイズ方法を含む）は
[From-Bean Writer](page?id=excel-tables/from-bean-writer&lang=ja)
を参照してください。

## `CellOneLineHeaderExcelTableWriter`

ヘッダー1行のテーブルに Cell データを書き込みます。
`data` の型が `List<List<Cell>>` となります。
使い方は `StringOneLineHeaderExcelTableWriter` と同様ですが、書き込む Cell オブジェクトを準備する必要があります。

## `CellHeaderExcelTableWriter`

ヘッダーが 2 行以上のテーブルに Cell データを書き込みます。
`String[][]` でヘッダーを指定します。使い方は `StringHeaderExcelTableWriter` と同様です。

## `CellFreeExcelTableWriter`

ヘッダーなしテーブルに Cell データを書き込みます。
`data` の型が `List<List<Cell>>` となります。
使い方は `StringFreeExcelTableWriter` と同様です。

## fluent setter 一覧

| setter | 説明 |
| --- | --- |
| `tableStartRowNumber(Integer)` | テーブル開始行（1 始まり、null で自動検出） |
| `tableStartColumnNumber(int)` | テーブル開始列（1 始まり） |
| `withIgnoresAdditionalColumnsOfHeaderData(boolean)` | ヘッダーの追加列を無視 |
| `withVerticalAndHorizontalOpposite(boolean)` | 縦横反転テーブル対応 |

## 大きなファイル：`IterableWriter`

書き込む前に`List<List<T>>`をすべてメモリ上に構築するとメモリを消費しすぎる場合は、
`getIterable()`を使って1行ずつ書き込むことができます。

```java
try (ExcelTableWriter.IterableWriter<String> iter =
        writer.getIterable("/path/to/template.xlsx", "/path/to/output.xlsx")) {
    for (List<String> row : rowSource) {
        iter.write(row);
    }
}
```

テンプレートパス／出力先パスを渡すオーバーロード（`getIterable(String, String)`）は、
自身が開いた`Workbook`を所有します。`close()`で出力先パスに保存してからクローズするので、
try-with-resourcesで使ってください。既存の`Workbook`を渡す場合（`getIterable(Workbook)`）は所有権が呼び出し側のままなので、`close()`は何もしません。保存・クローズは呼び出し側の責任です。
