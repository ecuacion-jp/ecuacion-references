# itemNameKey の解決ルール

## 概要

`itemNameKey` は、項目の表示名を `item_names.properties` などから引くためのキーです。
形式は `"クラス部.フィールド部"` （例：`"user.name"`）です。

`Item` に明示的に指定しない限り、`itemPropertyPath` やクラス情報をもとに自動で決まります。

---

## フィールド部の決定ルール

フィールド部は以下の優先順位で決まります。

| 優先度 | 条件 | フィールド部の値 |
| :---: | --- | --- |
| 1 | `itemNameKey()` でフィールド部を明示指定 | 指定した値 |
| 2 | 上記以外 | `itemPropertyPath` の右端ノード（コレクション部除く） |

右端ノードの例：

| itemPropertyPath | 右端ノード → フィールド部 |
| --- | --- |
| `"name"` | `"name"` |
| `"dept.name"` | `"name"` |
| `"item.property.path"` | `"path"` |
| `"strList[0].<list element>"` | `"strList"` |

---

## クラス部の決定ルール

クラス部は以下の優先順位で決まります。

| 優先度 | 条件 | クラス部の値 |
| :---: | --- | --- |
| 1 | `itemNameKey("cls.field")` でクラス部を明示指定 | 指定した値 |
| 2 | フィールドが属するクラスに `@ItemNameKeyClass` アノテーションがある | アノテーションの値（先頭小文字） |
| 3 | 上記以外 | `ItemContainer#getItem()` が設定するクラス名（先頭小文字） |

---

## 具体例

以下のようなクラス構成を例に解説します。

```java
public class UserRecord implements ItemContainer {
    private String name;
    private String email;
    // ...
}
```

| 設定内容 | itemNameKey |
| --- | --- |
| 設定なし（`"name"` という itemPropertyPath） | `"userRecord.name"` |
| `.itemNameKey("fullName")` | `"userRecord.fullName"` |
| `.itemNameKey("person.fullName")` | `"person.fullName"` |

---

## `@ItemNameKeyClass` アノテーション

クラス名と異なるクラス部を一括で指定したい場合に使います。

```java
@ItemNameKeyClass("user")
public class UserRecord implements ItemContainer {
    private String name;    // → itemNameKey: "user.name"
    private String email;   // → itemNameKey: "user.email"
}
```

フィールドごとに `itemNameKey()` を指定する手間を省けます。

---

## item_names.properties との対応

解決された `itemNameKey` を使って、`item_names.properties` から項目名が取得されます。

```properties
# item_names.properties
userRecord.name=氏名
userRecord.email=メールアドレス
user.name=氏名
```
