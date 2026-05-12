# Violation 入門

## 概要

ecuacion-lib では、バリデーション違反を `Violations` クラスに集約してから
`throwIfAny()` でまとめてスローするパターンを採用しています。

```java
Violations violations = new Violations();
violations.add("error.some-business-rule");  // ビジネスルール違反を追加
violations.throwIfAny();                      // 違反があれば ViolationException をスロー
```

複数チェックを先に行ってまとめてスローできるため、1リクエストで全エラーをユーザーに提示できます。

---

## 2種類の違反

`Violations` は2種類の違反を受け付けます。

**BusinessViolation** — 業務ロジック上の違反を手動で追加します。

```java
violations.add("error.some-business-rule");
```

**ConstraintViolation** — Jakarta Validation の検証結果を追加します。

```java
violations.validate(form);
```

---

## 詳細リファレンス

- 違反の追加パターン・`ViolationException` の詳細 → **共通事項など &gt; BusinessViolation と Violations**
- Jakarta Validation アノテーションの一覧 → **共通事項など &gt; Jakarta Validation**
