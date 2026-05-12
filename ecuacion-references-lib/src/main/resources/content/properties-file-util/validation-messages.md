# ValidationMessages

## 概要

`ValidationMessages` 系の3ファイルは、Jakarta Validation のバリデーションメッセージを扱うためのファイルです。
主に ecuacion フレームワーク内部から自動的に呼び出されますが、独自バリデーションアノテーションを作成する場合や
既存メッセージをカスタマイズする場合に、アプリ開発者が定義・編集する必要があります。

`getMessage()` などと異なり、引数に `Object...` ではなく `Map<String, Object>` を使う点が特徴です。
これは Jakarta Validation のアノテーション属性が `{value}`, `{min}`, `{max}` のような名前付きプレースホルダーを
使用するためです。

| ファイル名 | メソッド | フォールバック先 |
| --- | --- | --- |
| `ValidationMessages[_xxx].properties` | `getValidationMessage(...)` | なし |
| `ValidationMessagesWithItemNames[_xxx].properties` | `getValidationMessageWithItemName(...)` | `ValidationMessages` |
| `ValidationMessagesPatternDescriptions[_xxx].properties` | `getValidationMessagePatternDescription(...)` | なし |

---

## ValidationMessages.properties を読む

バリデーション違反時のメッセージを格納します。キーは通常アノテーションの完全修飾クラス名 + `.message` です。

```properties
# ValidationMessages.properties
jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message=\
    {0} は {min} 文字以上 {max} 文字以下で入力してください。
```

`{min}` や `{max}` はアノテーション属性のプレースホルダーです（`{0}` 形式ではなく名前付き）。

### getValidationMessage()

```java
Map<String, Object> argMap = new HashMap<>();
argMap.put("min", 3);
argMap.put("max", 20);

// ロケール指定
String msg = PropertiesFileUtil.getValidationMessage(
    Locale.JAPANESE,
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message",
    argMap);

// ロケール指定なし（Locale.ROOT を使用）
String msg = PropertiesFileUtil.getValidationMessage(
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message",
    argMap);
// => "{0} は 3 文字以上 20 文字以下で入力してください。"
// ※ {0} はフレームワークが別途 item name に置換する
```

Map のキーはアノテーション属性名そのままです（`value`, `min`, `max` など）。

### hasValidationMessage()

```java
boolean exists = PropertiesFileUtil.hasValidationMessage(
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message");
```

---

## ValidationMessagesWithItemNames.properties を読む

`{0}` が項目名プレースホルダーになっているバリデーションメッセージを格納します。
`ValidationMessages.properties` との違いは、メッセージに `{0}` が含まれる点です。
フレームワークが `{0}` を実際の項目名に置換した上でユーザーに表示します。

```properties
# ValidationMessagesWithItemNames.properties
jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message=\
    {0} は {min} 文字以上 {max} 文字以下で入力してください。
```

### getValidationMessageWithItemName()

```java
Map<String, Object> argMap = new HashMap<>();
argMap.put("min", 3);
argMap.put("max", 20);

// ロケール指定
String msg = PropertiesFileUtil.getValidationMessageWithItemName(
    Locale.JAPANESE,
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message",
    argMap);

// ロケール指定なし（Locale.ROOT を使用）
String msg = PropertiesFileUtil.getValidationMessageWithItemName(
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message",
    argMap);
```

### hasValidationMessageWithItemName()

```java
boolean exists = PropertiesFileUtil.hasValidationMessageWithItemName(
    "jp.ecuacion.lib.core.jakartavalidation.constraint.SomeAnnotation.message");
```

---

## ValidationMessagesPatternDescriptions.properties を読む

`@PatternWithDescription` などで使用する、正規表現の人間向け説明文を格納します。
キーが存在しない場合は `ValidationMessages.properties` にフォールバックします。

```properties
# ValidationMessagesPatternDescriptions.properties
pattern.phone-number=電話番号形式（例：090-1234-5678）
pattern.postal-code=郵便番号形式（例：123-4567）
```

### getValidationMessagePatternDescription()

```java
// ロケール指定
String desc = PropertiesFileUtil.getValidationMessagePatternDescription(
    Locale.JAPANESE, "pattern.phone-number");

// ロケール指定なし（Locale.ROOT を使用）
String desc = PropertiesFileUtil.getValidationMessagePatternDescription("pattern.phone-number");
// => "電話番号形式（例：090-1234-5678）"
```

---

## Map 引数と Object... 引数の違い

| メソッド群 | 引数形式 | プレースホルダー形式 |
| --- | --- | --- |
| `getMessage(...)` / `getConstant(...)` | `Object...` | `{0}`, `{1}`, ... |
| `getMessageWithItemName(...)` | `Object...` | `{item_name}`, `{0}`, `{1}`, ... |
| `getValidationMessage(...)` | `Map<String, Object>` | `{value}`, `{min}`, `{max}`, ... |
| `getValidationMessageWithItemName(...)` | `Map<String, Object>` | `{0}`, `{value}`, `{min}`, `{max}`, ... |

Jakarta Validation のアノテーション属性は名前付き（`{value}`, `{min}` など）のため、
インデックス形式（`{0}`, `{1}`）ではなく `Map` を使用します。

---

## EL 式のサポート

プロパティ値の中で `${...}` を使った EL（Expression Language）式が利用できます。
ValidationMessages は `{min}`, `{max}` などのアノテーション属性を EL 変数として扱えるため、
属性値を使った計算を記述できます。

```properties
# ValidationMessages.properties
# min と max の平均値を計算して埋め込む例
jp.ecuacion.example.constraint.SomeAnnotation.message=\
    推奨値は ${(min + max) / 2} です（{min}〜{max}）。
```

`{min}`, `{max}` は名前付きプレースホルダー（Map 経由で解決）、
`${...}` は EL 式（アノテーション属性値を変数として評価）です。

EL 式はすべてのプロパティファイル種別で利用可能ですが、
EL 変数（アノテーション属性）が渡されるのは ValidationMessages 系のみです。

---

## アプリ開発者が関わる場面

通常これらのメソッドは ecuacion フレームワーク内部から自動的に呼び出されます。
アプリ開発者が直接関わるのは以下の場面です。

- **独自バリデーションアノテーションを作成する場合** — `ValidationMessages.properties` や
  `ValidationMessagesWithItemNames.properties` にメッセージを定義する
- **既存バリデーションメッセージを上書きする場合** — アプリ側のファイルに `.default` なしのキーを定義する
- **`@PatternWithDescription` を使用する場合** — `ValidationMessagesPatternDescriptions.properties` に
  パターン説明文を定義する
