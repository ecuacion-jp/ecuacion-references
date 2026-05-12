# 項目名とは

## 概要

ecuacion-lib のバリデーションエラーメッセージは、「どのフィールドでエラーが起きたか」を示す
**項目名**を含めて表示できます。

項目名なし（Hibernate Validator デフォルト）:

```
null は許可されていません
```

項目名あり:

```
「名前」にnullは許可されていません
```

この「項目名」とは、`item_names.properties` で管理する表示名です。
本記事では、この仕組みに関わるクラスと全体像を説明します。

---

## 全体の流れ

```text
フィールド
  ↓  itemPropertyPath で特定
Item クラス
  ↓  itemNameKey を解決
item_names.properties のキー
  ↓  値を取得
表示名（例: 名前）
  ↓  ValidationMessagesWithItemNames.properties に代入
エラーメッセージ（例: 「名前」にnullは許可されていません）
```

---

## Item クラス

`Item`（`jp.ecuacion.lib.core.item.Item`）は、フィールド 1 つ分の属性を保持するクラスです。
コンストラクタに `itemPropertyPath` を渡すだけで生成でき、
カスタマイズはメソッドチェーンで指定します。

```java
new Item("name")                          // 最小構成
new Item("password").hideValue()          // 値を非表示
new Item("name").itemNameKey("fullName")  // itemNameKey を明示指定
```

詳細は [Item クラス](item/item) を参照してください。

---

## itemPropertyPath

`itemPropertyPath` は、オブジェクト内のフィールド位置を表すパス文字列です。

| itemPropertyPath | 意味 |
| --- | --- |
| `"name"` | オブジェクト直下の `name` フィールド |
| `"address.zipCode"` | `address` オブジェクトの `zipCode` フィールド |
| `"items[0].price"` | `items` リストの最初の要素の `price` フィールド |

詳細は [itemPropertyPath とは](item/item-property-path) を参照してください。

---

## itemNameKey

`itemNameKey` は `item_names.properties` のキーにあたる文字列で、
`"クラス部.フィールド部"` の形式を取ります（例: `"userRecord.name"`）。

通常は `itemPropertyPath` やクラス名から自動解決されますが、明示指定もできます。
自動解決のルールは [itemNameKey の解決ルール](item/item-name-key) を参照してください。

---

## item_names.properties

項目名の表示名を管理するプロパティファイルです。

```properties
# item_names.properties
userRecord.name=名前
userRecord.email=メールアドレス
userRecord.birthDate=生年月日
```

キーが `itemNameKey`、値が画面に表示される名前です。
ロケール別ファイル（`item_names_ja.properties` など）への分離も可能です。

---

## 詳細リファレンス

- 項目名をバリデーションメッセージへ埋め込む方法 → **メッセージへの埋め込み**
- itemNameKey の自動解決ルール → **[itemNameKey の解決ルール](item/item-name-key)**
- ItemContainer と @ItemNameKeyClass → **ItemContainer と @ItemNameKeyClass**
