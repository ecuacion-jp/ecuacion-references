## 概要

`Item`（`jp.ecuacion.lib.core.item.Item`）は、フィールド 1 つ分の属性を保持するクラスです。
主にバリデーションエラーメッセージ中での項目名や値の表示方法を制御するために使われます。

通常、`Item` インスタンスはアプリ開発者が直接生成することはなく、
`ItemUtil.resolveItem()` または `ItemContainer.getItem()` 経由で取得します。

直接インスタンスを生成するのは、[ItemContainer](?id=item/item-container) の
`customizedItems()` を実装してフィールドの表示動作をカスタマイズする場合のみです。

```java
@Override
public Item[] customizedItems() {
    return new Item[] {
        new Item("password").hideValue(),
        new Item("name").itemNameKey("fullName")
    };
}
```

---

## itemNameKey の明示指定

`itemNameKey` はエラーメッセージ中の項目名を `item_names.properties` などから引くためのキーです
（詳細は [itemNameKey の解決ルール](?id=item/item-name-key) を参照）。

通常は自動で解決されますが、明示的に指定することもできます。

```java
// クラス部 + フィールド部の両方を指定
new Item("mobilePhoneNumber.value").itemNameKey("mobilePhone.number")

// フィールド部のみ指定（クラス部は自動解決）
new Item("name").itemNameKey("fullName")
```

`"."` を含む場合は `"クラス部.フィールド部"` と解釈され、含まない場合はフィールド部のみと解釈されます。

---

## 値の非表示

パスワードなど、エラーメッセージに値を含めたくないフィールドには `hideValue()` を使います。

```java
new Item("password").hideValue()
```

デフォルトは値を表示する設定（`showsValue = true`）です。

---

## メソッドチェーン

各メソッドは `this` を返すため、メソッドチェーンで記述できます。

```java
Item item = new Item("password")
    .itemNameKey("account.password")
    .hideValue();
```

---

## まとめ

| メソッド | 説明 |
| --- | --- |
| `.itemNameKey(key)` | itemNameKey を明示的に指定（省略時は自動解決） |
| `.hideValue()` | エラーメッセージで値を非表示にする |
