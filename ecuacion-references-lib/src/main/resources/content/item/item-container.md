# ItemContainer インターフェース

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

1. `itemPropertyPath` をインデックスなしの正規パスに変換して正規化（`toIndexlessPath`）
2. `customizedItems()` の中から `propertyPath` が一致するものを検索
3. 見つかればそれを返す。見つからなければ `new Item(propertyPath)` を生成して返す
4. `@ItemNameKeyClass` アノテーションの情報をフィールドが属するクラスから読み取り、`Item` に設定する

[itemPropertyPath とは](item/item-property-path) で説明したとおり、
`getItem()` に渡す `itemPropertyPath` は **ItemContainer 自身からの相対パス** です。

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

`ItemUtil.resolveItem()` が ItemContainer を探す際の検索範囲は **RootBean から 1 階層まで** です。
詳細は [ItemUtil](item/item-util) を参照してください。
