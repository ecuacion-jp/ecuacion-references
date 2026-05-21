# Bean マッピング（書き込み）

`StringOneLineHeaderExcelTableFromBeanWriter`（ヘッダー1行）または
`StringHeaderExcelTableFromBeanWriter`（ヘッダー複数行）を使うと、
`StringExcelTableBean` を継承した Bean のリストを Excel に書き込めます。

Bean マッピング（読み込み）の[ToBeanReader](/public/ja/article?id=excel-tables/bean-mapping)と
対称的な仕組みです。

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

## テンプレートファイルについて

FromBeanWriter も通常の Writer と同様に、テンプレート Excel ファイルを元に書き込みます。
テンプレートファイルにはあらかじめヘッダー行・書式・列幅などを設定しておいてください。
詳細は[Writer](/public/ja/article?id=excel-tables/writer)を参照してください。
