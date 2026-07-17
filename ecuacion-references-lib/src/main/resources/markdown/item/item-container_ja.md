## 概要

`ItemContainer`（`jp.ecuacion.lib.core.item.ItemContainer`）は、
フィールドの表示属性をカスタマイズした `Item` を保持するインターフェースです。

Record や Form などのクラスに実装することで、そのクラスに属するフィールドの
項目名キー（`itemNameKey`）や値の表示/非表示を一元管理できます。

---

## 実装方法

`ItemContainer` を実装するには `customizedItems()` を定義します。

```java
public class UserRecord implements ItemContainer {

    private String name;
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("password").hideValue(),
        };
    }
}
```

カスタマイズが不要なフィールドは `customizedItems()` に含めなくて構いません。
`getItem()` がカスタマイズ済み `Item` を見つけられなかった場合は、
自動生成された `Item` を返します。

カスタマイズが一切不要な場合は空配列を返します。

```java
@Override
public Item[] customizedItems() {
    return new Item[] {};
}
```

---

## getItem()

`getItem(itemPropertyPath)` は指定パスに対応する `Item` を返します。

```java
Item item = userRecord.getItem("password");
```

内部動作：

1. `itemPropertyPath` のコレクションインデックスを除去した形式に変換する（`toIndexlessPath`）
2. クラス階層を上に辿りながら、各レベルの `customizedItems()` から `propertyPath` が一致する `Item` を収集・マージする（詳細は後述）
3. 見つからなければ `new Item(propertyPath)` を生成して返す
4. `@ItemNameKeyClass` アノテーションの情報をフィールドが属するクラスから読み取り、`Item` に設定する

[itemPropertyPath とは](?id=item/item-property-path) で説明したとおり、
`getItem()` に渡す `itemPropertyPath` は **ItemContainer 自身からの相対パス** です。

---

## 親クラスからの設定継承

`ItemContainer` を実装したクラスが `customizedItems()` をオーバーライドした場合、
`getItem()` はクラス階層を上に辿りながら同じ `propertyPath` を持つ `Item` をマージします。
子クラスで明示的に設定されたプロパティが優先され、未設定のプロパティは親クラスの設定を引き継ぎます。

```java
// 親クラス：itemNameKey と hideValue を設定
public class UserRecord implements ItemContainer {
    private String name;
    private String password;

    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("name").itemNameKey("user.name"),
            new Item("password").itemNameKey("user.password").hideValue(),
        };
    }
}

// 子クラス：画面によって name の itemNameKey を上書き
public class EditUserRecord extends UserRecord {
    @Override
    public Item[] customizedItems() {
        return new Item[] {
            new Item("name").itemNameKey("editUser.name"),
        };
    }
}
```

`EditUserRecord` のインスタンスで `getItem("name")` を呼ぶと：

- `itemNameKey` → `"editUser.name"`（子クラスの設定を優先）
- `showsValue` → `true`（親クラスでも設定されていないためデフォルト値）

`getItem("password")` を呼ぶと：

- `itemNameKey` → `"user.password"`（親クラスから継承）
- `showsValue` → `false`（親クラスの `hideValue()` を継承）

子クラスで一切 `customizedItems()` をオーバーライドしない場合は、
単純に親クラスの設定がそのまま使われます。

> **モジュール制約**：この機能は Java の `MethodHandles` を用いて各クラス階層の
> `customizedItems()` を個別に呼び出します。
> Spring Boot の fat JAR などの unnamed module 環境では自動的に動作しますが、
> named module 環境では `module-info.java` に以下のように `opens` を追加する必要があります。
>
> ```java
> module com.example.myapp {
>     requires jp.ecuacion.lib.core;
>     opens com.example.myapp.record to jp.ecuacion.lib.core;
> }
> ```

---

## mergeItems()

共通の `Item[]` と Record 固有の `Item[]` を結合するユーティリティメソッドです。

```java
public class UserRecord implements ItemContainer {

    private static final Item[] COMMON_ITEMS = new Item[] {
        new Item("createdAt").itemNameKey("common.createdAt"),
        new Item("updatedAt").itemNameKey("common.updatedAt"),
    };

    @Override
    public Item[] customizedItems() {
        return mergeItems(COMMON_ITEMS, new Item[] {
            new Item("password").hideValue(),
        });
    }
}
```

同じ `propertyPath` を持つ `Item` が両方の配列に含まれると `RuntimeException` がスローされます。

---

## ItemContainer の検索範囲

`ItemUtil.resolveItem()` は、rootBean と propertyPath をもとに ItemContainer を探して
`Item` を解決するメソッドです。バリデーションエラーメッセージを生成する際などに
フレームワーク内部から呼ばれます（詳細は [ItemUtil](?id=item/item-util) を参照）。

この検索範囲は **rootBean から 1 階層まで** です。
