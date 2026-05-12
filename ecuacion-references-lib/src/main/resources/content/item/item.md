# Item クラス

## 概要

`Item`（`jp.ecuacion.lib.core.item.Item`）は、フィールド 1 つ分の属性を保持するクラスです。
主にバリデーションエラーメッセージ中での項目名や値の表示方法を制御するために使われます。

コンストラクタに `itemPropertyPath` を渡すだけで生成でき、
追加のカスタマイズはメソッドチェーンで指定します。

---

## コンストラクタ

```java
Item item = new Item("name");
```

引数は [itemPropertyPath](item/item-property-path)（空文字は不可）です。

---

## itemNameKey の明示指定

`itemNameKey` はエラーメッセージ中の項目名を `item_names.properties` などから引くためのキーです
（詳細は [itemNameKey の解決ルール](item/item-name-key) を参照）。

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
| `new Item(propertyPath)` | itemPropertyPath を指定してインスタンスを生成 |
| `.itemNameKey(key)` | itemNameKey を明示的に指定（省略時は自動解決） |
| `.hideValue()` | エラーメッセージで値を非表示にする |
