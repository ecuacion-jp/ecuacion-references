# Jakarta Validation

## 概要

Jakarta Validation（旧 Bean Validation）は、アノテーションでバリデーションルールを定義する標準仕様です。
ecuacion-lib では `Violations.validate()` を通じて Jakarta Validation の結果を `Violations` に変換します。

---

## Violations.validate()

```java
new Violations().validate(someObject).throwIfAny();
```

`Violations.validate()` はオブジェクトを検証し、`ConstraintViolation` を `Violations` に追加して返します。
`BusinessViolation` と組み合わせることもできます。

```java
Violations violations = new Violations();
violations.add(new BusinessViolation("error.additional-check"));
violations.validate(someForm);
violations.throwIfAny();
```

### バリデーショングループを指定する

```java
new Violations().validate(someObject, GroupA.class, GroupB.class).throwIfAny();
```

---

## ecuacion-lib-validation の独自制約アノテーション

標準アノテーション（`@NotNull`, `@Size` など）を補完する独自アノテーションの全一覧と使い方は
**独自バリデーター** メニューを参照してください。

---

## 詳細リファレンス

- バリデーションエラーメッセージの仕組み → **util &gt; PropertiesFileUtil &gt; ValidationMessages**
