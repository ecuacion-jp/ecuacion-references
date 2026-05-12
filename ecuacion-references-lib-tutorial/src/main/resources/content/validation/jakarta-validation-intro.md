# Jakarta Validation 入門

## 概要

Jakarta Validation は、アノテーションでバリデーションルールを定義する標準仕様です。
`Violations.validate()` でオブジェクトを検証し、結果を `Violations` として受け取ります。

```java
new Violations().validate(form).throwIfAny();
```

`Violations` の使い方は [Violation 入門](../validation/violation-intro) を参照してください。

---

## 独自アノテーション

`ecuacion-lib-validation` は、標準の `@NotNull` / `@Size` などを補完する独自アノテーションを提供しています。
主なカテゴリは次のとおりです。

- **条件付き** — `@TrueWhen`, `@NotNullWhen`, `@NotEmptyWhen` など「〇〇のとき△△であること」を表現
- **比較** — `@LessThan`, `@GreaterThanOrEqualTo` などフィールド間の大小関係を検証
- **文字列フォーマット** — `@IntegerString`, `@BooleanString` など文字列の型変換可否を検証
- **コレクション** — `@AnyNotNull`, `@AllNullOrAllNotNull` など配列・コレクション要素を一括検証

---

## 詳細リファレンス

- 独自アノテーションの全一覧と使い方 → **独自バリデーター**
