# メッセージへの埋め込み

## 概要

フィールドの項目名をバリデーションエラーメッセージに含めるには、
`ValidationMessagesWithItemNames.properties` を使います。

通常の `ValidationMessages.properties`:

```properties
jakarta.validation.constraints.NotNull.message = null は許可されていません
```

`ValidationMessagesWithItemNames.properties`（項目名入り、`.base` サフィックス）:

```properties
jakarta.validation.constraints.NotNull.message.base = {0}にnullは許可されていません
```

`{0}` の位置に項目名（例: `「名前」`）が埋め込まれます。

ecuacion-lib-core が提供するデフォルト（`ValidationMessagesWithItemNames_lib_core.properties`）が
上記のメッセージを定義しているため、アプリ側で新たに作成する必要はありません。

---

## セットアップ手順

### 1. ItemContainer を実装する

バリデーション対象のクラスに `ItemContainer` を実装します。
詳細は [ItemContainer](item/item-container) を参照してください。

```java
public class UserRecord implements ItemContainer {

    @NotNull
    private String name;

    @Override
    public Item[] customizedItems() {
        return new Item[] {};  // カスタマイズ不要な場合は空配列
    }
}
```

### 2. item_names.properties に項目名を登録する

```properties
# src/main/resources/item_names.properties
userRecord.name=名前
userRecord.email=メールアドレス
```

---

## エラーメッセージの組み立て

### Jakarta Validation の場合

バリデーション実行時、ecuacion-lib は以下の手順でメッセージを組み立てます。

1. `Violations.validate(userRecord)` を実行
2. `name` フィールドで `@NotNull` 違反を検出
3. `UserRecord#getItem("name")` で `Item` を取得
4. `ItemUtil.resolveItem()` が `itemNameKey`（例: `"userRecord.name"`）を解決
5. `item_names.properties` から `"名前"` を取得
6. `ValidationMessagesWithItemNames.properties` の `{0}` に `「名前」` を代入
7. 結果: `「名前」にnullは許可されていません`

### BusinessViolation の場合

業務ロジックのチェックで `{item_name}` を使いたい場合は、`BusinessViolation` に `itemNameKeys` を渡します。
`itemNameKeys` の値が `item_names.properties` のキーとして使われます。

```java
violations.add(new BusinessViolation(
    new String[] {"customer.email"},  // itemNameKeys
    new String[] {"email"},            // itemPropertyPaths（UI ハイライト用）
    "error.already-registered"));
```

詳細は [Violation](validation/violation) を参照してください。

---

## アプリ側でのメッセージ上書き

アノテーションごとにメッセージを変えたい場合は、アプリの
`ValidationMessagesWithItemNames.properties` に `.base` サフィックス付きで定義します。

```properties
# ValidationMessagesWithItemNames.properties（アプリ側）
jakarta.validation.constraints.NotNull.message.base = {0}は必須入力です
```

ecuacion-lib-core のデフォルトより優先されます。

---

## 詳細リファレンス

- ItemContainer の詳細 → **[ItemContainer](item/item-container)**
- itemNameKey の解決ルール → **[itemNameKey の解決ルール](item/item-name-key)**
- コレクション要素の場合 → **コレクション要素の項目名**
