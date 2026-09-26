## 概要

`itemNameKey` は、項目の表示名を `item_names.properties` などから引くためのキーです。
形式は `"クラス部.フィールド部"` （例：`"user.name"`）です。

`Item` に明示的に指定しない限り、`itemPropertyPath` やクラス情報をもとに自動で決まります。

---

## 設定方法

`itemNameKey` は通常自動で決まりますが、`Item` の `itemNameKey()` を使って明示的に指定することもできます。

```java
// クラス部 + フィールド部の両方を指定
new Item("mobilePhoneNumber.value").itemNameKey("mobilePhone.number")

// フィールド部のみ指定（クラス部は自動解決）
new Item("name").itemNameKey("fullName")
```

`"."` を含む場合は `"クラス部.フィールド部"` と解釈され、含まない場合はフィールド部のみと解釈されます。

---

## itemNameKeyの決定ルール

itemNameKey は、以下の優先順位で決定されます（1がない場合は2を使用、2がない場合は3を使用）。

1. `itemNameKey()` に指定された値を使用
2. `itemPropertyPath` を使用
3. （クラス部のみ）`ItemContainer` クラスのクラス名を使用

itemNameKey 及び itemPropertyPath は、クラス部の指定有無のパターンがあるため、クラス部とフィールド部はそれぞれ独立で決定されます。

具体的なパターンは以下のとおりです。
尚、ItemContainerを実装するオブジェクトはUserDtoとし、UserDtoのフィールドとしてDeptDto（フィールド名dept）を保持しているものとします。

| itemNameKey() の指定 | itemPropertyPath | 結果の itemNameKey |
| --- | --- | --- |
| `"user.name"`<br>（クラス部+フィールド部） | 任意 | `"user.name"`<br>（itemPropertyPath の内容によらずそのまま採用） |
| `"fullName"`<br>（フィールド部のみ） | `"dept.name"` | `"dept.fullName"`<br>（クラス部は `dept` ノードの名前をそのまま使用） |
| `"fullName"`<br>（フィールド部のみ） | `"name"` | `"userDto.fullName"`<br>（クラス部は `ItemContainer` のクラス名から解決） |
| 未指定 | `"name"` | `"userDto.name"`<br>（フィールド部は右端ノード、クラス部は `ItemContainer` のクラス名） |
| 未指定 | `"dept.name"` | `"dept.name"`<br>（クラス部は `dept` ノードの名前をそのまま使用、フィールド部は右端ノード） |

itemPropertyPath が `dept.manager.name` のように複数階層になる場合、フィールド部には一番右のノード（`name`）、クラス部にはその一つ左のノード（`manager`）が使われます。

---

## `@ItemNameKeyClass` アノテーション

前項では、`itemNameKey()` や `itemPropertyPath` でクラス部が指定されなかった場合、デフォルトで `userDto` が itemNameKey のクラス部として使われることを説明しました。
しかし `item_names.properties` には、通常 `userDto.name` ではなく `user.name` のような一般的な形で定義することが多いはずです。
このデフォルトのクラス部 `userDto` は、`@ItemNameKeyClass`（`jp.ecuacion.lib.core.annotation.ItemNameKeyClass`）を使うことで `user` に変更できます。

`@ItemNameKeyClass` は、クラスに対して `itemNameKey` のクラス部を一括指定するアノテーションです。
指定しない場合、クラス部は前述のとおりクラス名の先頭を小文字化した文字列になります。

```java
@ItemNameKeyClass("user")
public class UserDto implements ItemContainer {
    private String name;    // itemNameKey: "user.name"
    private String email;   // itemNameKey: "user.email"
}
```

---

## item_names.properties との対応

解決された `itemNameKey` を使って、`item_names.properties` から項目名が取得されます。

```properties
# item_names.properties
userDto.name=氏名
userDto.email=メールアドレス
user.name=氏名
```
