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

これらのアノテーションは `conditionValue` に加え、固有のパラメータを持ちます。

| アノテーション | `propertyPath` が満たすべき条件 | 固有パラメータ |
| ------------- | ------------------------------- | ------------- |
| `@PatternWhen` | 正規表現にマッチすること | `regexp` |
| `@NotPatternWhen` | 正規表現にマッチしないこと | `regexp` |
| `@StringWhen` | 指定した文字列のいずれかであること | `string[]` |
| `@NotStringWhen` | 指定した文字列のいずれでもないこと | `string[]` |
| `@ValueOfPropertyPathWhen` | 別フィールドと同値であること | `valuePropertyPath` |
| `@NotValueOfPropertyPathWhen` | 別フィールドと異なること | `valuePropertyPath` |

> **Note:** `@PatternWhen` / `@NotPatternWhen` の `regexp`、および `conditionValuePatternRegexp`
> （下記 [conditionValue](#conditionvalue--条件の種類) を参照）は、エンドユーザー入力に由来しうる値に対して
> マッチングされます。ネストした量指定子（例：`(a+)+`）のような、破滅的バックトラッキングを起こしやすい
> パターンは避けてください。細工した入力に対してマッチングが指数時間かかることがあります。

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

| `ConditionValue` | 意味 | 追加属性（必須） | 追加属性（任意） |
| ---------------- | ---- | --------------- | --------------- |
| `NULL` | `null` であること | — | — |
| `NOT_NULL` | `null` でないこと | — | — |
| `EMPTY` | 空（null または空文字）であること | — | — |
| `NOT_EMPTY` | 空でないこと | — | — |
| `TRUE` | `true` であること | — | — |
| `FALSE` | `false` であること | — | — |
| `STRING` | 指定文字列のいずれかであること | `conditionValueString` | `conditionValueDisplayStringPropertyPath` |
| `PATTERN` | 正規表現にマッチすること | `conditionValuePatternRegexp` | `conditionValuePatternDescription`, `conditionValueDisplayStringPropertyPath` |
| `VALUE_OF_PROPERTY_PATH` | 別フィールドと同値であること | `conditionValuePropertyPath` | `conditionValueDisplayStringPropertyPath` |

### conditionOperator — 条件演算子

| `ConditionOperator` | 意味 |
| ------------------- | ---- |
| `EQUAL_TO`（デフォルト） | 条件が成立するとき validte する |
| `NOT_EQUAL_TO` | 条件が成立しないとき validate する |

### conditionValuePatternDescription

`conditionValue = PATTERN` のとき、`conditionValuePatternRegexp` に設定した正規表現の代わりにエラーメッセージへ表示する人間向けの説明文を指定します。省略すると正規表現がそのまま表示されます。

```java
// conditionValuePatternDescription なし
// → 「電話番号」が「^(070|080|090).*」に合致する場合は〜

// conditionValuePatternDescription あり
@TrueWhen(
    propertyPath = "smsConsentAgreed",
    conditionPropertyPath = "phone",
    conditionValue = ConditionValue.PATTERN,
    conditionValuePatternRegexp = "^(070|080|090).*",
    conditionValuePatternDescription = "携帯電話番号"  // メッセージに使う説明
)
// → 「電話番号」が「携帯電話番号」に合致する場合は〜
```

### conditionValueDisplayStringPropertyPath

`STRING` / `PATTERN` / `VALUE_OF_PROPERTY_PATH` のとき、エラーメッセージに表示する「条件値の表示名」を別フィールドの itemNameKey から解決する際に指定します。省略すると条件値の実値（文字列・正規表現・フィールド値）がそのまま表示されます。

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

### @PatternWhen — 正規表現で検証（regexp）

```java
// type が "POSTAL" のとき、code は7桁の数字であること
@PatternWhen(
    propertyPath = "code",
    regexp = "\\d{7}",
    conditionPropertyPath = "type",
    conditionValue = ConditionValue.STRING,
    conditionValueString = {"POSTAL"}
)
public class AddressForm { ... }
```

### @StringWhen — 特定の文字列であることを検証（string[]）

```java
// role が NOT_EMPTY のとき、accountType は "ADMIN" または "OPERATOR" であること
@StringWhen(
    propertyPath = "accountType",
    string = {"ADMIN", "OPERATOR"},
    conditionPropertyPath = "role",
    conditionValue = ConditionValue.NOT_EMPTY
)
public class UserForm { ... }
```

### @ValueOfPropertyPathWhen — 別フィールドと同値であることを検証（valuePropertyPath）

```java
// confirmed が true のとき、confirmPassword は password と同じ値であること
@ValueOfPropertyPathWhen(
    propertyPath = "confirmPassword",
    valuePropertyPath = "password",
    conditionPropertyPath = "confirmed",
    conditionValue = ConditionValue.TRUE
)
public class PasswordForm { ... }
```
