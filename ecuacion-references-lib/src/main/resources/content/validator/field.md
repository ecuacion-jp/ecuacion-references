# その他のフィールドバリデーター

## 概要

フィールドレベルで個別のフィールドに付与するバリデーターです。

---

## 文字列フォーマット系

文字列として保持された値が特定の型に変換可能かを検証します。

| アノテーション | 検証内容 |
| ------------- | -------- |
| `@IntegerString` | `int` の範囲内の整数文字列であること |
| `@LongString` | `long` の範囲内の整数文字列であること |
| `@BooleanString` | `true` / `false` に変換可能な文字列であること |

```java
public class SearchForm {
    @IntegerString
    private String page;

    @BooleanString
    private String includeArchived;
}
```

---

## SizeString — 文字列長

文字列の長さが指定範囲内であることを検証します（標準の `@Size` の文字列版）。

```java
@SizeString(min = 1, max = 100)
private String name;
```

| 属性 | 説明 | デフォルト |
| ---- | ---- | ---------- |
| `min` | 最小文字数 | `0` |
| `max` | 最大文字数 | `Integer.MAX_VALUE` |

---

## EnumElement — Enum 値チェック

指定した Enum の要素として有効な値（`name()`）であることを検証します。

```java
@EnumElement(enumClass = StatusEnum.class)
private String status;
```

`"ACTIVE"`, `"INACTIVE"` などの文字列が `StatusEnum` に定義されているかを確認します。
