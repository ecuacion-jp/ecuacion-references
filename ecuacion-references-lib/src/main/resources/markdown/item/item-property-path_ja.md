## 概要

`itemPropertyPath` は、あるオブジェクト内のフィールドの位置を表す文字列パスです。
バリデーションエラーメッセージなどで「どの項目でエラーが起きたか」を特定するために使われます。

propertyPath と同じドット記法を用います。

| itemPropertyPath | 意味 |
| --- | --- |
| `"name"` | 起点直下の `name` フィールド |
| `"dept.name"` | 起点が保持する `dept` オブジェクトの `name` フィールド |
| `"bookList[1].title"` | `bookList` の 2 番目の要素の `title` フィールド |
| `"strList[0].<list element>"` | `strList`（`List<String>`）の要素そのもの |

---

## 基点となるオブジェクト

`ItemContainer`（`jp.ecuacion.lib.core.item.ItemContainer`）は、フィールドの表示属性をカスタマイズした
`Item` を保持するインターフェースです。Record や Form などのクラスに実装します
（詳細は [ItemContainer](?id=item/item-container) を参照）。

`itemPropertyPath` の起点となるオブジェクトは、`ItemContainer` の有無によって以下のルールで決まります。

| 状況 | itemPropertyPath の基点 |
| --- | --- |
| `ItemContainer` がない | rootBean |
| rootBean 自身が `ItemContainer` | rootBean（= ItemContainer） |
| rootBean の直下の子が `ItemContainer` | その子 ItemContainer |

`ItemContainer` の検索は **1 階層まで** です。
`rootBean.dept.record` のように 2 階層以上ネストした先の `ItemContainer` は自動的には発見されません。

例えば、rootBean が `SomeForm`、その直下に `UserRecord`（ItemContainer）がある場合、
`"userRecord.name"` という fullPropertyPath に対して、
`"name"` が itemPropertyPath として `UserRecord#getItem()` に渡されます。

---

## 存在理由

Web UI では `ItemContainer.customizedItems()` にフィールドごとの属性（項目名キー・値の表示制御など）を定義します。
フォームが DTO を保持する構成（`UserForm` が `UserDto` を持ち、`UserDto` が `name`・`address` を持つ場合）では、
`UserForm` 起点のパスで書くと冗長になります。

```java
// UserForm 起点で書いた場合（冗長）
new Item("userDto.name"), new Item("userDto.address"), ...
```

`itemPropertyPath` は `ItemContainer`（この例では `UserDto`）を起点とするため、短く書けます。

```java
// UserDto 起点（itemPropertyPath）で書いた場合
new Item("name"), new Item("address"), ...
```

Thymeleaf 等のテンプレート側も同じ起点を共有することで、バックエンド・フロントエンド双方の記述量と視認性が改善されます。

---

## itemPropertyPath の書き方と省略形

`new Item()` に渡す `itemPropertyPath` は、Jakarta Validation の `propertyPath` をそのまま
使う形式（インデックスあり）と、インデックスを省いた省略形のどちらでも指定できます。
内部で正規化されるため、どちらを渡しても同じ結果になります。

```java
// どちらも同じ意味
new Item("bookList[1].title")   // propertyPath 準拠形式（インデックスあり）
new Item("bookList[].title")    // 省略形
```

以下の表に、コレクション型ごとの propertyPath 準拠形式と省略形をまとめます。
フィールド名はすべて例示用です。`User` は `name` フィールドを持つクラスとします。

### List

| フィールドの型 | 対象 | propertyPath 準拠形式 | 省略形 |
| --- | --- | --- | --- |
| `List<String> strList` | 要素そのもの | `strList[0].<list element>` | `strList[]` |
| `List<User> userList` | `User.name` | `userList[0].name` | `userList[].name` |
| `List<List<String>> nestedList` | 内側の String 要素 | `nestedList[0].<list element>[1].<list element>` | `nestedList[][]` |
| `List<List<User>> nestedList` | 内側の `User.name` | `nestedList[0].<list element>[1].name` | `nestedList[][].name` |

### Set

Set はコレクションが順序を持たないため、実行時のパスにもインデックスが付きません。
正規化前後で形式が変わらないケースが多くなります。

| フィールドの型 | 対象 | propertyPath 準拠形式 | 省略形 |
| --- | --- | --- | --- |
| `Set<String> strSet` | 要素そのもの | `strSet[].<iterable element>` | `strSet[]` |
| `Set<User> userSet` | `User.name` | `userSet[].name` | `userSet[].name`（同一） |

### Map

Map のキー制約の propertyPath にはキーアクセスを示す修飾子が含まれており、
正規化後も除去されません。そのためキーと値の `itemPropertyPath` は区別されます。

| フィールドの型 | 対象 | propertyPath 準拠形式 | 省略形 |
| --- | --- | --- | --- |
| `Map<String, ?> strMap` | キーそのもの | `strMap<K>[].<map key>` | `strMap<K>[]` |
| `Map<?, String> strMap` | 値そのもの | `strMap[key1].<map value>` | `strMap[]` |
| `Map<?, User> strMap` | 値の `User.name` | `strMap[key1].name` | `strMap[].name` |

### 重複登録について

異なるインデックスを持つパスは正規化後に同一キーとなるため、同じ `customizedItems()`
内で重複登録すると実行時に例外が発生します。

```java
// NG: 正規化後に両方とも "userList[].name" になり重複
new Item("userList[1].name"),
new Item("userList[2].name")
```

---

## コレクション要素のキーワード

コレクションの要素そのものを指す場合に付くキーワードです。

| 型 | キーワード |
| --- | --- |
| `List<T>` | `<list element>` |
| `Set<T>` | `<iterable element>` |
| `Map<K, V>` のキー | `<map key>` |
| `Map<K, V>` の値 | `<map value>` |
