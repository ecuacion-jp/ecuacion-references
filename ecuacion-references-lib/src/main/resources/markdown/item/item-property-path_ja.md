## item

`item` とは、本ライブラリで定義される、画面上の入力項目・表示項目など、1 つの「項目」を表す概念です。
その値自体はオブジェクトのフィールドとして保持されますが、画面表示にはラベル文言や入力必須といった値以外の属性も必要になります。
`Item` は、1 つのフィールドに対するこれらの属性情報を保持するオブジェクトです（詳細は [Item](?id=item/item) を参照）。

本ライブラリ（ecuacion-lib）では、`Item` およびそれに関連する各種クラスは、Jakarta Validation 及び `BusinessViolation`（詳細は [違反・例外の概要](?id=concepts/violations-overview) を参照）のエラーメッセージを生成する目的でのみ使用されます。
ecuacion-splib など本ライブラリを利用する他のライブラリでは、画面表示用のラベルなど、画面表示の用途にも使用されています。

---

## itemPropertyPath

`itemPropertyPath` は、item の位置を表す文字列パスです。
propertyPath と同様の位置付けですが、表記の簡略化や、後述する itemNameKey での利用など、item に対応するための追加機能を持ちます。

propertyPath と同じドット記法を用います。
後述の `ItemContainer` を使用しない場合は、propertyPath と全く同一です。

| itemPropertyPath | 意味 |
| --- | --- |
| `"name"` | 起点直下の `name` フィールド |
| `"dept.name"` | 起点が保持する `dept` オブジェクトの `name` フィールド |
| `"bookList[1].title"` | `bookList` の 2 番目の要素の `title` フィールド |

---

## ItemContainer

`ItemContainer`（`jp.ecuacion.lib.core.item.ItemContainer`）は、item を保持するオブジェクトに実装するインターフェースです。
Form やその直下の DTO などのクラスに実装します（詳細は [ItemContainer](?id=item/item-container) を参照）。

item をカスタマイズするには、その item を保持するオブジェクトが `ItemContainer` を実装している必要があります。
以下、`ItemContainer` を使用して item に属性を設定する例を紹介します。

Jakarta Validation には、メッセージに `{invalidValue}` というプレースホルダーを含めることで、エラーとなった値そのものを表示する機能があります。
秘匿性のある項目については、`Item` の `hideValue()` を指定することで、この `{invalidValue}` 部分への値の表示を抑制できます。
ここでは `ItemContainer` の抽象メソッドである `customizedItems()` を実装し、`password` フィールドに `hideValue()` を指定してみます。

```java
public class UserDto implements ItemContainer {

    @Size(min = 8, max = 20)
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("password").hideValue()
        };
    }
}
```

---

## itemPropertyPath の起点となるオブジェクト

例えば、rootBean が `UserForm`、その直下に `UserDto`（`name`・`address` フィールドを持つ）がある場合、Jakarta Validation 標準の
propertyPath（以下、fullPropertyPath と呼びます）では `"userDto.name"` となります。

つまり、`customizedItems()` にはこのように定義することになります。

```java
// fullPropertyPath（UserForm 起点）で書いた場合（冗長）
new Item("userDto.name"), new Item("userDto.address"), ...
```

これだと冗長なので、`Item` に指定する `itemPropertyPath` は `ItemContainer` を起点としたパスで書きます。

```java
// itemPropertyPath（UserDto 起点）で書いた場合
new Item("name"), new Item("address"), ...
```

`itemPropertyPath` の起点となるオブジェクトは、`ItemContainer` の有無によって以下のルールで決まります。

| 状況 | itemPropertyPath の起点 |
| --- | --- |
| `ItemContainer` がない | rootBean |
| rootBean 自身が `ItemContainer` | rootBean（= ItemContainer） |
| rootBean の直下の子が `ItemContainer` | その子 ItemContainer |

`ItemContainer` の検索は **1 階層まで** です。
`rootBean.dept.record` のように 2 階層以上ネストした先の `ItemContainer` は発見されません。

先ほどの例で `UserDto` が `ItemContainer` である（＝ `UserDto` が `ItemContainer` を implements する）場合、
`"userDto.name"` という fullPropertyPath に対して、`"name"` が itemPropertyPath となります。

Thymeleaf 等のテンプレート側も同じ起点を共有することで、バックエンド・フロントエンド双方の記述量と視認性が改善されます。

コレクション（List・Set・Map）のフィールドに対する `itemPropertyPath` の書き方や省略形については、
[collectionにおけるpropertyPath](?id=item/collection-property-path) を参照してください。

---

## 補足: 兄弟関係にある複数の ItemContainer があっても曖昧にならない理由

例えば `UserForm` の直下に `UserDto`・`DeptDto` という 2 つの `ItemContainer` があり、両方とも `name` フィールドを持つ場合、
`itemPropertyPath` として単に `"name"` とだけ書いたのでは、どちらの `ItemContainer` を指しているか区別できないのでは、と疑問に思うかもしれません。
実際には、以下の 2 つの利用パターンにおいて、この曖昧性は生じません。

1. **Jakarta Validation でのメッセージ表示時**（違反の発生したフィールドを起点とするケース）: バリデーション違反が発生したフィールドは、rootBean から辿って
   Jakarta Validation 自身がすでに特定済みです。`ItemContainer` の探索は、その特定済みの fullPropertyPath（例: `"userDto.name"`）を起点に行われるため、
   単なる `"name"` のような曖昧な形になることはありません。
2. **splib の Thymeleaf による項目名表示時**（rootBean から順に対象の item を辿るケース）: 各コンポーネントに記載する `itemPropertyPath` は省略形
   （例: `"name"`）であっても、その省略された部分（どの `ItemContainer` のスコープかという情報）は別途指定されているため、
   コンポーネント全体としては対象を一意に特定できます。
