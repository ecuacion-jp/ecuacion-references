# List・Set・Map の項目名

## 概要

バリデーションエラーが発生したフィールドがコレクション（List・Set・Map）の要素の場合、
項目名には「どの要素か」の情報が付加されます。

例：`items` リストの 2 番目の要素の `productCode` で `@NotNull` 違反が発生した場合:

```
「items」の「productCode」の「2番目の要素」にnullは許可されていません
```

---

## コレクション種別と出力パターン

`itemPropertyPath` のコレクション層から種別を解析し、対応するキーワードで説明文を生成します。

| コレクション種別 | キーワード | デフォルト表示（日本語） |
| --- | --- | --- |
| `List<T>` の要素 | `order` | `{n}番目の要素`（1始まり） |
| `Set<T>` の要素 | `any` | `複数の要素のいずれか` |
| `Map<K, V>` のキー | `mapKey` | `キー項目のいずれか` |
| `Map<K, V>` の値 | `mapValue` | `キーが[{key}]の要素` |

---

## 具体例

### List の場合

```java
public class OrderForm implements ItemContainer {

    @Valid
    private List<OrderItemRecord> items;

    @Override
    public Item[] customizedItems() { return new Item[] {}; }
}

public class OrderItemRecord implements ItemContainer {

    @NotNull
    private String productCode;

    @Override
    public Item[] customizedItems() { return new Item[] {}; }
}
```

`items[1].productCode` で `@NotNull` 違反が発生した場合のエラーメッセージ例:

```
「items」の「productCode」の「2番目の要素」にnullは許可されていません
```

### List&lt;String&gt; の場合（要素そのもの）

```java
@Valid
private List<@NotNull String> tags;
// itemPropertyPath: "tags[0].<list element>"
```

エラーメッセージ例:

```
「tags」の「1番目の要素」にnullは許可されていません
```

### Map の場合

```java
@Valid
private Map<String, @NotNull String> labels;
// itemPropertyPath: "labels[en].<map value>"
```

エラーメッセージ例:

```
「labels」の「キーが[en]の要素」にnullは許可されていません
```

---

## 表示文字列のカスタマイズ

コレクション要素の説明文は `messages.properties` で上書きできます。

| キー（jp.ecuacion.lib.core.common.itemName.*） | デフォルト値（ja） | 説明 |
| --- | --- | --- |
| `order` | `{0}番目の要素` | List の n 番目（`{0}` = 1始まり） |
| `any` | `複数の要素のいずれか` | Set の要素 |
| `mapKey` | `キー項目のいずれか` | Map のキー |
| `mapValue` | `キーが[{0}]の要素` | Map の値（`{0}` = キー値） |
| `collectionItemName` | `{0}の{1}` | 親項目名と要素説明の結合パターン |

カスタマイズ例:

```properties
# messages.properties
jp.ecuacion.lib.core.common.itemName.order=第{0}項目
```

---

## 詳細リファレンス

- itemPropertyPath のコレクション記法 → **[itemPropertyPath とは](item/item-property-path)**
- prefix・postfix・separator のカスタマイズ → **prefix・postfix・separator**
- ネストしたオブジェクトの場合 → **[itemNamePath](messaging/item-name-path)**
