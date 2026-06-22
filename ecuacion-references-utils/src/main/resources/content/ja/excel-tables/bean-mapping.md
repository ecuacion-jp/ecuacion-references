# Bean マッピング

`StringOneLineHeaderExcelTableToBeanReader`（ヘッダー1行）または
`StringHeaderExcelTableToBeanReader`（ヘッダー複数行）を使うと、
Excel の各行を Java オブジェクト（Bean）に自動変換して取得できます。
Jakarta Validation によるバリデーションも統合されています。

## 概要

通常の Reader は `List<List<String>>` を返しますが、このクラスは
`StringExcelTableBean` を継承した Bean のリストを返します。

```
StringHeaderExcelTableToBeanReader.readToBean(filePath)
  → List<T extends StringExcelTableBean>
```

## Bean クラスの定義

### `@ExcelColumn` アノテーションを使う方法（推奨）

`StringExcelTableBean` を継承したクラスを作り、
各フィールドに `@ExcelColumn` でヘッダーラベルを指定します。

```java
import jp.ecuacion.util.excel.table.bean.StringExcelTableBean;
import jp.ecuacion.util.excel.table.bean.ExcelColumn;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

public class ProductBean extends StringExcelTableBean {

    @ExcelColumn("商品コード")
    @NotEmpty
    @Pattern(regexp = "[A-Z][0-9]{3}")
    private String productCode;

    @ExcelColumn("商品名")
    @NotEmpty
    private String productName;

    @ExcelColumn("価格")
    private Integer price;          // 自動的に Integer に変換される

    public ProductBean(List<String> colList) {
        super(colList);
    }

    // getter / setter ...
}
```

`@ExcelColumn` を使うと、Excel の列順序に依存しません。
ヘッダーラベルが一致する列の値が対応するフィールドに自動設定されます。

### フィールド型の自動変換

`StringExcelTableBean` は String から以下の型へ自動変換します。

| フィールド型 | 変換元（String） |
| --- | --- |
| `String` | そのまま |
| `Integer` / `int` | `Integer.valueOf(value)` |
| `Long` / `long` | `Long.valueOf(value)` |
| `BigDecimal` | `new BigDecimal(value)` |
| `BigInteger` | `new BigInteger(value)` |
| `Boolean` / `boolean` | `Boolean.valueOf(value)` |
| `LocalDate` | `LocalDate.parse(value, formatter)` |
| `LocalDateTime` | `LocalDateTime.parse(value)` |
| `LocalTime` | `LocalTime.parse(value)` |

`null` または空文字列は `null` に変換されます（`String` 型は除く）。

日付フォーマットをカスタマイズするには `getDateTimeFormatter()` をオーバーライドします。

```java
@Override
protected DateTimeFormatter getDateTimeFormatter() {
    return DateTimeFormatter.ofPattern("yyyy/MM/dd");
}
```

### `getFieldNameArray()` をオーバーライドする方法

`@ExcelColumn` を使わず、列の順序でフィールドを対応付けることもできます。

```java
public class ProductBean extends StringExcelTableBean {

    private String productCode;
    private String productName;
    private Integer price;

    public ProductBean(List<String> colList) {
        super(colList);
    }

    @Override
    protected String[] getFieldNameArray() {
        return new String[] {"productCode", "productName", "price"};
    }
}
```

列をスキップしたい場合は `null` を指定します。

```java
return new String[] {"productCode", null, "price"}; // 2 列目をスキップ
```

## `readToBean()` での読み込み

ヘッダーが1行の場合は `StringOneLineHeaderExcelTableToBeanReader` を使います。

```java
StringOneLineHeaderExcelTableToBeanReader<ProductBean> reader =
    new StringOneLineHeaderExcelTableToBeanReader<>(
        ProductBean.class,
        "Sheet1",
        new String[] {"商品コード", "商品名", "価格"});

List<ProductBean> products = reader.readToBean("/path/to/file.xlsx");
```

ヘッダーが2行以上の場合は `StringHeaderExcelTableToBeanReader` を使います。

```java
StringHeaderExcelTableToBeanReader<ProductBean> reader =
    new StringHeaderExcelTableToBeanReader<>(
        ProductBean.class,
        "Sheet1",
        new String[][] {
            {"個人情報", "個人情報", "連絡先"},
            {"商品コード", "商品名", "価格"}
        });

List<ProductBean> products = reader.readToBean("/path/to/file.xlsx");
```

`readToBean()` は内部で Jakarta Validation を実行します。
バリデーションエラーがあると `ViolationException` がスローされます。

バリデーションをスキップする場合は `readToBean(filePath, false)` を使います。

## `afterReading()` フックメソッド

読み込みと個別フィールドのバリデーションが完了した後に呼ばれるフックです。
フィールド間の連携チェックや追加処理に使います。

```java
@Override
public void afterReading() {
    // 開始日 ≤ 終了日 のチェックなど
    if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
        throw new RuntimeException("開始日は終了日以前にしてください");
    }
}
```

## エラーセルのハイライト：`highlightErrors()`

バリデーションエラーが発生した際に、エラーのある Excel セルを赤くハイライトした
コピーファイルを出力できます。

```java
try {
    reader.readToBean(filePath);
} catch (ViolationException ex) {
    reader.highlightErrors(filePath, ex.getViolations(), "/path/to/error-output.xlsx");
    throw ex;
}
```

`@ExcelColumn` アノテーションを使っている場合は違反フィールドに対応するセルだけが、
使っていない場合はエラー行の全データセルがハイライトされます。

## Typed 型の Bean マッピング：`TypedExcelTableBean`

`TypedOneLineHeaderExcelTableToBeanReader`（ヘッダー1行）または
`TypedHeaderExcelTableToBeanReader`（ヘッダー複数行）は、`StringExcelTableBean`
ではなく `TypedExcelTableBean` を継承した Bean に各行をマッピングします。
Bean クラスの定義方法（`@ExcelColumn` / `getFieldNameArray()` / Jakarta
Validation / `afterReading()` / `highlightErrors()`）はこれまでの説明と全く同じで、
変わるのは「読み込んだ値の型」と「フィールド型への変換ルール」だけです。

```java
import jp.ecuacion.util.excel.table.bean.TypedExcelTableBean;
import jp.ecuacion.util.excel.table.bean.ExcelColumn;
import java.time.LocalDate;
import java.util.List;

public class PersonBean extends TypedExcelTableBean {

    @ExcelColumn("名前")
    private String name;

    @ExcelColumn("年齢")
    private Integer age;            // セルの Double 値から四捨五入で変換される

    @ExcelColumn("誕生日")
    private LocalDate birthday;

    public PersonBean(List<Object> colList) {
        super(colList);
    }

    // getter / setter ...
}
```

### ネイティブ型からの変換

`StringExcelTableBean` は常に `String` の値を受け取りパースしますが、
`TypedExcelTableBean` は各セルの値が既にネイティブな Java 型（`String`、
`Double`、`LocalDate`、`LocalDateTime`、`Boolean`、または `null`。
詳細は[データ型の選択](/public/ja/article?id=excel-tables/data-types)を参照）
に変換された状態で渡され、それをさらにフィールドの宣言型へ変換します。

| 渡される値の型 | 変換可能なフィールド型 |
| --- | --- |
| `String` | `String`、`Boolean` |
| `Double` | `String`、`Double`、`Float`、`Integer`、`Long`、`Short`、`BigDecimal`、`BigInteger` |
| `LocalDate` | `LocalDate`、`LocalDateTime`（その日の 0 時として）、`String` |
| `LocalDateTime` | `LocalDateTime`、`LocalDate`、`LocalTime`、`String` |
| `Boolean` | `Boolean`、`String` |

`Double` を整数系の型（`Integer`、`Long`、`Short`、`BigInteger`）に変換する際は、
切り捨てではなく `Math.round` による四捨五入が行われます。例えばセルの値が
`25.6` の場合は `25` ではなく `26` になります。これにより、数値セルにたまたま
小数が入っていた場合の意図しない挙動を防げます。

`null`（空白セルや空文字列に由来）は、フィールドの宣言型に関わらず `null` に
変換されます。

## マルチヘッダーへの対応

マルチヘッダーを持つテーブルの Bean マッピングでは、
`@ExcelColumn` に複数の要素を指定します（上のヘッダー行から順番に）。

```java
// 2 行ヘッダー: 1 行目「個人情報」/ 2 行目「名前」
@ExcelColumn({"個人情報", "名前"})
private String name;

// 全行で同じラベル（縦結合）のカラムは 1 要素でも一致する
@ExcelColumn("#")
private Integer rowNumber;
```
