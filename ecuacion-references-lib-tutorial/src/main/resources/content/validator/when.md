# When系（条件付きバリデーション）

## 概要

When系アノテーションは、「`conditionPropertyPath` が特定の状態のとき、`propertyPath` は〇〇でなければならない」という条件付きルールを表現します。クラスレベルのアノテーションです。

```java
@TrueWhen(
    propertyPath = "agreedToTerms",
    conditionPropertyPath = "accountType",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class RegistrationForm { ... }
```

---

## アノテーション一覧

### 基本条件（conditionValue のみで指定）

| アノテーション | `propertyPath` が満たすべき条件 |
| ------------- | ------------------------------- |
| `@TrueWhen` | `true` であること |
| `@FalseWhen` | `false` であること |
| `@NullWhen` | `null` であること |
| `@NotNullWhen` | `null` でないこと |
| `@EmptyWhen` | 空（null または空文字）であること |
| `@NotEmptyWhen` | 空でないこと |

### パターン・文字列・値参照条件

| アノテーション | `propertyPath` が満たすべき条件 |
| ------------- | ------------------------------- |
| `@PatternWhen` | 正規表現にマッチすること |
| `@NotPatternWhen` | 正規表現にマッチしないこと |
| `@StringWhen` | 文字列型であること |
| `@NotStringWhen` | 文字列型でないこと |
| `@ValueOfPropertyPathWhen` | 別フィールドと同値であること |
| `@NotValueOfPropertyPathWhen` | 別フィールドと異なること |

---

## 主要属性

### propertyPath — 検証対象フィールド

```java
propertyPath = "agreedToTerms"            // 単一フィールド
propertyPath = {"startDate", "endDate"}   // 複数フィールドを同じルールで検証
```

### conditionPropertyPath — 条件フィールド

条件の判定に使うフィールド名です。ネストしたフィールドも指定できます。

```java
conditionPropertyPath = "accountType"
conditionPropertyPath = "address.country"
```

### conditionValue — 条件の種類

`conditionPropertyPath` のフィールドがどういう状態のときにバリデーションを実行するかを指定します。

| `ConditionValue` | 意味 | 追加属性 |
| ---------------- | ---- | -------- |
| `NULL` | `null` であること | なし |
| `NOT_NULL` | `null` でないこと | なし |
| `EMPTY` | 空（null または空文字）であること | なし |
| `NOT_EMPTY` | 空でないこと | なし |
| `TRUE` | `true` であること | なし |
| `FALSE` | `false` であること | なし |
| `STRING` | 指定文字列のいずれかであること | `conditionValueString` |
| `PATTERN` | 正規表現にマッチすること | `conditionValuePatternRegexp` |
| `VALUE_OF_PROPERTY_PATH` | 別フィールドと同値であること | `conditionValuePropertyPath` |

### conditionOperator — 条件演算子

| `ConditionOperator` | 意味 |
| ------------------- | ---- |
| `EQUAL_TO`（デフォルト） | 条件が成立するとき validte する |
| `NOT_EQUAL_TO` | 条件が成立しないとき validate する |

### falseWhenConditionNotSatisfied

`true` にすると、条件が成立しないときに逆のルールを適用します（`@TrueWhen` なら、条件不成立のとき `false` であることを検証）。デフォルトは `false`。

---

## 使用例

### 基本例 — 条件付き必須

```java
// accountType が入力済みのとき、agreedToTerms は true でなければならない
@TrueWhen(
    propertyPath = "agreedToTerms",
    conditionPropertyPath = "accountType",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class RegistrationForm { ... }
```

### STRING 条件 — 特定の値のとき

```java
// role が "ADMIN" のとき、adminCode は空でないこと
@NotEmptyWhen(
    propertyPath = "adminCode",
    conditionPropertyPath = "role",
    conditionValue = ConditionValue.STRING,
    conditionValueString = {"ADMIN"}
)
public class UserForm { ... }
```

### NOT_EQUAL_TO — 条件不成立時に検証

```java
// 送信確認フラグが true でないとき、reason は空でないこと
@NotEmptyWhen(
    propertyPath = "reason",
    conditionPropertyPath = "confirmed",
    conditionValue = ConditionValue.TRUE,
    conditionOperator = ConditionOperator.NOT_EQUAL_TO
)
public class CancelForm { ... }
```
