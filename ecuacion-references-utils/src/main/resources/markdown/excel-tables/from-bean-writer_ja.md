`StringOneLineHeaderExcelTableFromBeanWriter`（ヘッダー1行）または
`StringHeaderExcelTableFromBeanWriter`（ヘッダー複数行）を使うと、
`StringExcelTableBean` を継承した Bean のリストを Excel に書き込めます。

Bean マッピング（読み込み）の[ToBeanReader](page?id=excel-tables/bean-mapping&lang=ja)と対称的な仕組みです。

## 概要

`StringExcelTableBean` に定義したフィールドと `@ExcelColumn` アノテーションを使い、
ヘッダーラベルとフィールドを対応付けて Excel に書き込みます。

```
List<T extends StringExcelTableBean>
  → StringOneLineHeaderExcelTableFromBeanWriter.writeFromBean()
    → Excel ファイル
```

## `writeFromBean()` での書き込み

ヘッダーが1行の場合は `StringOneLineHeaderExcelTableFromBeanWriter` を使います。

```java
List<ProductBean> beans = ...; // StringExcelTableBean を継承した Bean のリスト

new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"商品コード", "商品名", "価格"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

ヘッダーが2行以上の場合は `StringHeaderExcelTableFromBeanWriter` を使います。

```java
new StringHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[][] {
        {"商品情報", "商品情報", "価格情報"},
        {"商品コード", "商品名", "定価"}
    })
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## フィールド → 列のマッピング

### `@ExcelColumn` アノテーションを使う方法（推奨）

Bean クラスの各フィールドに `@ExcelColumn` でヘッダーラベルを指定します。
ヘッダーの列順序に依存せず、ラベルで対応付けます。

```java
public class ProductBean extends StringExcelTableBean {

    @ExcelColumn("商品コード")
    private String productCode;

    @ExcelColumn("商品名")
    private String productName;

    @ExcelColumn("価格")
    private Integer price;

    public ProductBean(List<String> colList) {
        super(colList);
    }
}
```

### `getFieldNameArray()` をオーバーライドする方法

`@ExcelColumn` を使わない場合、フィールド名と列位置を対応付けます。
この場合、ヘッダーの列順序とフィールドの順序が一致している必要があります。

```java
@Override
protected String[] getFieldNameArray() {
    return new String[] {"productCode", "productName", "price"};
}
```

## フィールド型の String 変換

書き込み時に各フィールドの値を文字列に変換します。

| フィールド型 | 変換結果 |
| --- | --- |
| `String` | そのまま |
| `Integer` / `int` / `Long` etc. | `toString()` |
| `BigDecimal` / `BigInteger` | `toString()` |
| `Boolean` / `boolean` | `"true"` または `"false"` |
| `LocalDate` | `defaultDateTimeFormat` で書式化（デフォルト `yyyy-MM-dd`） |
| `LocalDateTime` | `defaultDateTimeFormat` で書式化 |
| `LocalTime` | `toString()`（ISO 形式） |
| `null` | `null`（空セル扱い） |

## 日付フォーマットの変更

日付フィールドの書式は fluent setter で変更できます。

```java
new StringOneLineHeaderExcelTableFromBeanWriter<ProductBean>(
    "Sheet1",
    new String[] {"商品コード", "登録日"})
    .defaultDateTimeFormat(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

## Typed FromBeanWriter：`TypedOneLineHeaderExcelTableFromBeanWriter` / `TypedHeaderExcelTableFromBeanWriter`

`TypedOneLineHeaderExcelTableFromBeanWriter`（ヘッダー1行）または
`TypedHeaderExcelTableFromBeanWriter`（ヘッダー複数行）を使うと、
`TypedExcelTableBean` を継承した Bean のリストを Excel に書き込めます。
これまで説明した Writer の Typed 型版にあたります。

### 概要

String 型の FromBeanWriter との最大の違いは、**各フィールドの値を文字列に変換せず、ネイティブな Java 型のままセルへ書き込む**点です。

```
List<T extends TypedExcelTableBean>
  → TypedOneLineHeaderExcelTableFromBeanWriter.writeFromBean()
    → Excel ファイル（各セルがネイティブ型の値を保持）
```

例えば `Integer` フィールドは、数値に見えるだけの文字列セルではなく数値セルとして書き込まれます。そして最も重要な点として、`LocalDate` /
`LocalDateTime` フィールドは**必ず日付書式のセルとして書き込まれる**ため、
出力された Excel ファイルを開くと日付として認識されます。

### `writeFromBean()` での書き込み

使い方は String 型の FromBeanWriter と同じで、Bean の継承元が
`StringExcelTableBean` から `TypedExcelTableBean` に変わるだけです。

```java
List<PersonBean> beans = ...; // TypedExcelTableBean を継承した Bean のリスト

new TypedOneLineHeaderExcelTableFromBeanWriter<PersonBean>(
    "Sheet1",
    new String[] {"名前", "年齢", "誕生日"})
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

ヘッダーが2行以上の場合は `TypedHeaderExcelTableFromBeanWriter` を使い、
`StringHeaderExcelTableFromBeanWriter` と同様に `String[][]` でヘッダーを指定します。

### フィールド → 列のマッピング

`@ExcelColumn` アノテーションや `getFieldNameArray()` の使い方は、
上で説明した String 型の FromBeanWriter と全く同じです。

### ネイティブ型でのセル書き込み

各フィールドの値は、その Java 型に応じて以下のようにセルへ書き込まれます。

| フィールド型 | 書き込まれるセル |
| --- | --- |
| `String` | 文字列セル |
| `Double` / `Integer` / `Long` / `BigDecimal` / `BigInteger` など | 数値セル（`setCellValue(double)`） |
| `Boolean` | 真偽値セル |
| `LocalDate` | 日付書式の数値セル（詳細は後述） |
| `LocalDateTime` | 日付・時刻書式の数値セル（詳細は後述） |
| `null` | 空白セル |

### 日付セルの書式保証

これが Typed FromBeanWriter の目玉機能です。**テンプレート側のセルの書式がどうであれ、`LocalDate` / `LocalDateTime` の値は必ず Excel が日付として認識できるセルに書き込まれます**。

- **テンプレートのセルに既に日付書式が設定されている場合**
  （`DateUtil.isCellDateFormatted` が `true` を返す場合）は、その書式がそのまま維持されます。`yyyy/mm/dd` や `yyyy年MM月dd日` など、
  あらかじめ作り込んだテンプレートの書式が尊重されます。
- **テンプレートのセルに日付書式が設定されていない場合**は、デフォルトの書式が自動的に適用されます（`LocalDate` には `yyyy-mm-dd`、
  `LocalDateTime` には `yyyy-mm-dd hh:mm:ss`）。テンプレートの日付列に書式を設定し忘れる心配がなくなり、必ず日付として認識される値が書き込まれることが保証されます。

後者のケースで適用されるデフォルト書式は、fluent setter で変更できます。

```java
new TypedOneLineHeaderExcelTableFromBeanWriter<PersonBean>(
    "Sheet1",
    new String[] {"名前", "誕生日"})
    .defaultDateFormat("yyyy/mm/dd")
    .defaultDateTimeFormat("yyyy/mm/dd hh:mm:ss")
    .writeFromBean("/path/to/template.xlsx", "/path/to/output.xlsx", beans);
```

| setter | デフォルト | 説明 |
| --- | --- | --- |
| `defaultDateFormat(String)` | `"yyyy-mm-dd"` | 日付書式が設定されていない `LocalDate` セルに適用される書式パターン |
| `defaultDateTimeFormat(String)` | `"yyyy-mm-dd hh:mm:ss"` | 日付書式が設定されていない `LocalDateTime` セルに適用される書式パターン |

これらの setter には、`DateTimeFormatter` ではなく POI のセル書式パターン文字列（例：`"yyyy/mm/dd"`）を渡します。

## テンプレートファイルについて

FromBeanWriter も通常の Writer と同様に、テンプレート Excel ファイルを元に書き込みます。
テンプレートファイルにはあらかじめヘッダー行・書式・列幅などを設定しておいてください。
詳細は[Writer](page?id=excel-tables/writer&lang=ja)を参照してください。
