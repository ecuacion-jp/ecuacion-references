# ExceptionUtil

`ExceptionUtil`（`jp.ecuacion.lib.core.util.ExceptionUtil`）は例外からメッセージ文字列を
取り出すユーティリティクラスです。
カスタム例外ハンドラーを実装する際に使用します。

---

## getMessageList — 例外からメッセージリストを取得

例外の種類に関わらず、統一した方法でメッセージ文字列のリストを返します。

```java
List<String> messages = ExceptionUtil.getMessageList(throwable);

// ロケール指定
List<String> messages = ExceptionUtil.getMessageList(throwable, Locale.JAPANESE);
```

内部では例外の型に応じて以下のように処理を振り分けます。

| 例外の型 | 処理 |
| --- | --- |
| `ViolationException` | `Violations` の各 Violation からメッセージを構築 |
| `ConstraintViolationException` | 各 `ConstraintViolation` からメッセージを構築 |
| その他 | `throwable.getMessage()` をそのまま返す |

`ConstraintViolationException` は複数の違反を持てるため、返値は常に `List` です。
通常の例外でも `List`（要素1件）で返ります。

---

## 入力型ごとのオーバーロード

`Throwable` の他にも、直接型を指定したオーバーロードがあります。

```java
// Violations から
List<String> messages = ExceptionUtil.getMessageList(violations);
List<String> messages = ExceptionUtil.getMessageList(violations, Locale.JAPANESE);

// ViolationException から
List<String> messages = ExceptionUtil.getMessageList(violationException);
List<String> messages = ExceptionUtil.getMessageList(violationException, Locale.JAPANESE);

// ConstraintViolation の Set から
List<String> messages = ExceptionUtil.getMessageList(constraintViolationSet);
List<String> messages = ExceptionUtil.getMessageList(constraintViolationSet, Locale.JAPANESE);
```

---

## isMessagesWithItemNamesAsDefault パラメータ

一部のオーバーロードには `isMessagesWithItemNamesAsDefault` フラグがあります。

```java
// true にすると messages_with_item_names.properties を優先して参照
List<String> messages = ExceptionUtil.getMessageList(throwable, Locale.JAPANESE, true);
```

`true` にすると、バリデーションメッセージの解決に
`ValidationMessagesWithItemNames.properties` を優先して使用します。
splib などのフレームワーク側でシステムデフォルトとして設定される値で、
各バリデーション側で個別に指定した値が優先されます。
