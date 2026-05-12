# MessageParameters

## 概要

`Violations.MessageParameters` は、エラーメッセージの生成方法を制御するパラメータクラスです。
`withMessageParameters()` でフルエント設定できます。

```java
violations.withMessageParameters(p -> p
    .isMessageWithItemName(true)
    .messagePrefix("upload.row.prefix"));
```

主なユースケースは、画面表示以外（アップロードされたExcelファイルのバリデーション等）で
わかりやすいエラーメッセージを組み立てたい場合です。

---

## isMessageWithItemName — 項目名あり/なしのメッセージ切り替え

バリデーションエラーのメッセージを `messages.properties` と
`messages_with_item_names.properties` のどちらから取得するかを制御します。

| 設定値 | 動作 |
| ------ | ---- |
| `null`（デフォルト） | フレームワーク層の既定値に従う |
| `true` | 項目名あり版のメッセージを使用 |
| `false` | 項目名なし版のメッセージを使用 |

```java
violations.withMessageParameters(p -> p.isMessageWithItemName(true));
```

**項目名あり版の例:**

```properties
# messages_with_item_names.properties
error.required = {item_name}は入力必須です
```

`isMessageWithItemName(true)` の場合、`{item_name}` にフィールドの項目名が埋め込まれます。
項目名の解決には Item / ItemUtil が使われます（詳細は util &gt; Item を参照）。

---

## showsItemNamePath — 項目名パスの表示

コレクション（リスト等）内の要素に対するバリデーションエラーで、
要素の位置情報（パス）をメッセージに含めるかを制御します。

| 設定値 | 表示例 |
| ------ | ------ |
| `false`（デフォルト） | `名前は入力必須です` |
| `true` | `リストの[1]番目の名前は入力必須です` |

```java
violations.withMessageParameters(p -> p.showsItemNamePath(true));
```

---

## messagePrefix / messagePostfix — メッセージの前後に追加テキスト

各エラーメッセージの前後に固定テキストを付加します。
Excelアップロードのバリデーションで「2行目: 〇〇は必須です」のような表示に使います。

```java
// String 版: messages.properties のキーとして解決、見つからなければそのまま使用
violations.withMessageParameters(p -> p
    .messagePrefix("2行目: ")
    .messagePostfix(" を確認してください"));

// Arg 版: Arg.message() などで動的に組み立てる
violations.withMessageParameters(p -> p
    .messagePrefix(Arg.message("upload.error.row.prefix", rowNumber)));
```

### String 版の挙動

`messagePrefix("some.key")` の `"some.key"` は `messages.properties` のキーとして解決されます。
キーが存在しなければ `"some.key"` という文字列そのものが使われます。

```properties
# messages.properties
upload.error.row.prefix = {0}行目:
```

```java
violations.withMessageParameters(p -> p
    .messagePrefix(Arg.message("upload.error.row.prefix", rowNumber)));
// → "3行目: 名前は入力必須です"
```

---

## 組み合わせ例

Excelファイルの検証で、行番号をprefixに付けつつ項目名を含めたメッセージを出す例です。

```java
for (int i = 0; i < rows.size(); i++) {
  int rowNum = i + 2; // ヘッダー行を除いた行番号
  new Violations()
      .validate(rows.get(i))
      .withMessageParameters(p -> p
          .isMessageWithItemName(true)
          .showsItemNamePath(true)
          .messagePrefix(rowNum + "行目: "))
      .throwIfAny();
}
```
