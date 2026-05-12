# ItemUtil

## 概要

`ItemUtil`（`jp.ecuacion.lib.core.util.ItemUtil`）は、
`Item` や `ItemContainer` を扱うユーティリティクラスです。

[ItemContainer#getItem()](item/item-container) が ItemContainer 内部での検索を担うのに対し、
`ItemUtil` は RootBean と fullPropertyPath を受け取り、
ItemContainer の発見・委譲・`itemNameKey` の確定まで行うパイプライン全体を提供します。

---

## resolveItem()

```java
Item item = ItemUtil.resolveItem(fullPropertyPath, rootBean, leafBean);
```

以下のパイプラインを実行し、`itemNameKey` と `showsValue` が確定した完成品の `Item` を返します。

1. `fullPropertyPath` と `rootBean` から ItemContainer を探す（1 階層まで）
2. ItemContainer が見つかれば `ItemContainer#getItem(itemPropertyPath)` に委譲
3. `itemNameKey` を確定し、`showsValue` を反映
4. 完成品の `Item` を返す

### ItemContainer の検索ルール

| rootBean の状態 | 動作 |
| --- | --- |
| rootBean 自身が `ItemContainer` | `rootBean.getItem(fullPropertyPath)` を呼ぶ |
| `fullPropertyPath` の第 1 ノードの子が `ItemContainer` | その子の `getItem(残りのパス)` を呼ぶ |
| どちらでもない | `ItemUtil.getItemNameKey()` で直接解決 |

検索は **1 階層まで** です。

### 使用例

```java
// Jakarta Validation の ConstraintViolation から利用する例
Item item = ItemUtil.resolveItem(
    cv.getPropertyPath().toString(),
    cv.getRootBean(),
    cv.getLeafBean()
);
```

---

## getItemPropertyPath()

```java
String itemPropertyPath = ItemUtil.getItemPropertyPath(rootBean, fullPropertyPath);
```

`resolveItem()` の内部処理のうち、ItemContainer を探して itemPropertyPath を返す部分だけを取り出したメソッドです。

`resolveItem()` を呼ぶと完成品の `Item` が返りますが、
itemPropertyPath の文字列だけが必要な場合はこちらを使います。

---

## getItemNameKey()

```java
// rootBean と propertyPath から leafBeanClass を導出して解決する版
String key = ItemUtil.getItemNameKey(
    explicitlySetItemNameKeyClass, rootBean, leafBean,
    defaultItemNameKeyClass, itemNameKeyField, propertyPath);

// クラス情報を直接渡す版
String key = ItemUtil.getItemNameKey(
    explicitlySetItemNameKeyClass, itemNameKeyClassFromAnnotation,
    itemNameKeyClassFromClassName, itemNameKeyField, propertyPath);
```

[itemNameKey の解決ルール](item/item-name-key) に従って `itemNameKey` を返します。

通常は `resolveItem()` の内部で自動的に呼ばれるため、直接使う場面は限られます。

---

## ItemContainer#getItem() との違い

| | `ItemContainer#getItem()` | `ItemUtil#resolveItem()` |
| --- | --- | --- |
| 入力の基点 | ItemContainer 自身 | RootBean |
| ItemContainer の発見 | しない（自分がそれ） | する（1 階層まで） |
| itemNameKey の確定 | しない | する |
| 返り値 | 中間オブジェクト | 完成品 |
