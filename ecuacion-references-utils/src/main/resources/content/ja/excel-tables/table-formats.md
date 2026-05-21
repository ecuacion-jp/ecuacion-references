# テーブル形式の選択

Reader/Writer クラスは、テーブルの形式によって
**Header 形式** と **Free 形式** の 2 種類に分かれています。

## Header 形式（`IfFormatHeaderExcelTable`）

テーブルの先頭に 1 行以上のヘッダー行があります。
コンストラクタで期待するヘッダーラベルを指定し、読み込み時に自動検証されます。

### テーブル開始位置の自動検出

`tableStartRowNumber` を指定しない（デフォルト `null`）場合、
**ヘッダーの最左列値をシート内で上から検索**し、テーブルの開始行を自動で特定します。

```
シートに複数のテーブルがある場合 → tableStartRowNumber で行番号を明示指定してください
```

### 複数行ヘッダー

ヘッダーが 2 行以上の場合は `String[][]` で指定します。

```java
// 2行ヘッダーの例
//  行1: | 個人情報 | 個人情報 | 連絡先 |
//  行2: | 名前     | 年齢     | メール |
StringHeaderExcelTableReader reader = new StringHeaderExcelTableReader(
    "Sheet1",
    new String[][] {
        {"個人情報", "個人情報", "連絡先"},
        {"名前",     "年齢",     "メール"}
    });
```

Excel 上でセルが結合（マージ）されていても、自動的に展開して検証します。

### ヘッダーの追加列を許容する

実際の Excel にヘッダー列が多い場合（指定列より多い列がある場合）、
デフォルトでは `ExcelTableException` がスローされます。
`withIgnoresAdditionalColumnsOfHeaderData(true)` で追加列を無視できます。

```java
StringOneLineHeaderExcelTableReader reader = new StringOneLineHeaderExcelTableReader(
    "Sheet1",
    new String[] {"名前", "年齢"})
    .withIgnoresAdditionalColumnsOfHeaderData(true);
```

### 縦横反転テーブル

通常はヘッダーが上部にありますが、ヘッダーが左側にある縦横反転テーブルも対応しています。

```java
reader.withVerticalAndHorizontalOpposite(true);
```

## Free 形式（`IfFormatFreeExcelTable`）

ヘッダー行を持たないテーブル、またはヘッダー検証が不要なテーブルに使います。
テーブルの開始位置と読み込み範囲を fluent setter で直接指定します。

```java
import jp.ecuacion.util.excel.table.reader.concrete.StringFreeExcelTableReader;

StringFreeExcelTableReader reader = new StringFreeExcelTableReader("Sheet1")
    .tableStartRowNumber(3)      // 3行目から開始（1始まり）
    .tableStartColumnNumber(2)   // B列から開始（1始まり）
    .tableRowSize(10)            // 最大 10 行読み込む
    .tableColumnSize(4);         // 4 列読み込む

List<List<String>> data = reader.read("/path/to/file.xlsx");
```

`tableRowSize` と `tableColumnSize` を指定しない場合は自動検出されます
（全列が空の行まで読み込み / 最初の空ヘッダーセルまで列を読み込み）。

## どちらを使うべきか

| 状況 | 推奨形式 |
| --- | --- |
| ヘッダー行があり、列の並びを保証したい | **Header** |
| 任意の位置にあるデータ範囲を読み込みたい | **Free** |
| ヘッダーはあるが検証不要 | **Free**（または Header で `.withIgnoresAdditionalColumnsOfHeaderData(true)`） |
