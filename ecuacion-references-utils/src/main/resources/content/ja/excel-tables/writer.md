# Writer

このページでは各 Writer クラスの使い方を説明します。

## Writer クラス一覧

| クラス名 | データ型 | テーブル形式 |
| --- | --- | --- |
| `StringOneLineHeaderExcelTableWriter` | String | Header（1行） |
| `StringOneLineHeaderExcelTableFromBeanWriter` | String | Header（1行）・Bean から書き込み |
| `StringHeaderExcelTableWriter` | String | Header（複数行） |
| `StringHeaderExcelTableFromBeanWriter` | String | Header（複数行）・Bean から書き込み |
| `StringFreeExcelTableWriter` | String | Free |
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
書き込み時には最初のデータ行のセルスタイルが以降の行にも適用されます
（Excel のセルスタイル上限 64,000 件対策のため）。

## 共通：`write()` メソッド

```java
void write(String templateFilePath, String destFilePath, List<List<T>> data)
    throws IOException;
```

| 引数 | 説明 |
| --- | --- |
| `templateFilePath` | テンプレート Excel ファイルのパス |
| `destFilePath` | 出力先ファイルのパス |
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
