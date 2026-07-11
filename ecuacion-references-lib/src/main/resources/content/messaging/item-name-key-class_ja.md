# @ItemNameKeyClass

## 概要

`@ItemNameKeyClass`（`jp.ecuacion.lib.core.annotation.ItemNameKeyClass`）は、
クラスに対して `itemNameKey` のクラス部を一括指定するアノテーションです。

指定なしの場合、クラス部はクラス名の先頭を小文字化した文字列になります。

```java
public class UserRecord implements ItemContainer { ... }
// クラス部: "userRecord"（UserRecord の先頭小文字化）
```

`@ItemNameKeyClass` を付けると、任意の文字列を指定できます。

```java
@ItemNameKeyClass("user")
public class UserRecord implements ItemContainer { ... }
// クラス部: "user"
```

---

## 使い方

```java
@ItemNameKeyClass("user")
public class UserRecord implements ItemContainer {

    private String name;    // itemNameKey: "user.name"
    private String email;   // itemNameKey: "user.email"
    private String zipCode; // itemNameKey: "user.zipCode"

    @Override
    public Item[] customizedItems() {
        return new Item[] {};
    }
}
```

`item_names.properties`:

```properties
user.name=名前
user.email=メールアドレス
user.zipCode=郵便番号
```

---

## @ItemNameKeyClass なしとの比較

| 設定 | クラス部 | item_names キー例 |
| --- | --- | --- |
| なし | `userRecord` | `userRecord.name` |
| `@ItemNameKeyClass("user")` | `user` | `user.name` |

クラス名をそのままキーのクラス部にしたい場合は `@ItemNameKeyClass` は不要です。
クラス名と異なる短い名前や既存の命名規則に合わせたい場合に使います。

---

## ItemContainer との組み合わせ

`@ItemNameKeyClass` の情報は `ItemContainer#getItem()` 内で読み取られ、
返す `Item` に自動的に反映されます。
`customizedItems()` で `Item` を個別定義する場合でも、
クラス部の指定に `@ItemNameKeyClass` が優先して使われます。

ただし `Item` に対して `itemNameKey("cls.field")` のようにクラス部まで明示指定した場合は、
その値が最優先されます。

---

## 詳細リファレンス

- itemNameKey の自動解決ルール全体 → **[itemNameKey の解決ルール](?id=item/item-name-key)**
- ItemContainer との連携 → **[ItemContainer](?id=item/item-container)**
