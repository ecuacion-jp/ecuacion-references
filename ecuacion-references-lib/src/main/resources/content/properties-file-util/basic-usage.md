# 基本的な使い方

## PropertiesFileUtil とは

`PropertiesFileUtil`（`jp.ecuacion.lib.core.util.PropertiesFileUtil`）は、アプリケーション内の各種
`.properties` ファイルを一元的に読み込むためのユーティリティクラスです。

Java 標準の `ResourceBundle` をベースに、以下の機能を追加しています。

- 複数種類の `.properties` ファイルをメソッドで使い分けて読み込める
- ecuacion の各モジュールとアプリの複数モジュールにまたがるファイルを一括読み込み
- ファイルをまたいだキー検索とフォールバック（後述）
- キーが存在しない場合の挙動をファイル種別によって切り替え
- `.default` サフィックスによるデフォルト値の上書き機能
- プロパティ値内での `${...}` EL 式評価（詳細は **ValidationMessages** を参照）

---

## サポートするファイルの種類

| ファイル名 | 取得メソッド | Locale | フォールバック先 | 説明 |
| --- | --- | :---: | --- | --- |
| `application[_xxx].properties` | `getApplication(...)` | | なし | アプリ設定値。キー未存在時は例外 |
| `constants[_xxx].properties` | `getConstant(...)` | | なし | 非ローカライズ文字列（定数） |
| `messages[_xxx].properties` | `getMessage(...)` | ✓ | なし | ローカライズ済みメッセージ |
| `messages_with_item_names[_xxx].properties` | `getMessageWithItemName(...)` | ✓ | `messages` | 項目名を含むメッセージ |
| `item_names[_xxx].properties` | `getItemName(...)` | ✓ | `messages` | 項目名 |
| `enum_names[_xxx].properties` | `getEnumName(...)` | ✓ | `messages` | Enum 値の表示名 |
| `ValidationMessages[_xxx].properties` | `getValidationMessage(...)` | ✓ | なし | バリデーションメッセージ |
| `ValidationMessagesWithItemNames[_xxx].properties` | `getValidationMessageWithItemName(...)` | ✓ | `ValidationMessages` | 項目名付きバリデーションメッセージ |
| `ValidationMessagesPatternDescriptions[_xxx].properties` | `getValidationMessagePatternDescription(...)` | ✓ | なし | パターン説明文 |

`ValidationMessages` 系の3種類は引数の形式が他と異なります。
詳細は [ValidationMessages](/public/article?id=properties-file-util/validation-messages) を参照してください。

---

## application.properties を読む

```java
// 値を取得（キーが存在しない場合は例外）
String value = PropertiesFileUtil.getApplication("app.title");

// キーの存在確認
boolean exists = PropertiesFileUtil.hasApplication("app.title");

// デフォルト値付きで取得（キーがなければ第二引数を返す）
String value = PropertiesFileUtil.getApplicationOrElse("app.optional-key", "default-value");
```

`application.properties` はキーが存在しない場合に **例外をスロー** します。

---

## constants.properties を読む

ロケール対応しない固定文字列（URL プレフィックス、コードスニペット、共通定数など）を格納するファイルです。

```java
String value = PropertiesFileUtil.getConstant("common.app-name");
boolean exists = PropertiesFileUtil.hasConstant("common.app-name");
```

---

## messages.properties を読む

`messages.properties` はロケール対応のメッセージファイルです。

```java
// ロケールを指定して取得
String msg = PropertiesFileUtil.getMessage(Locale.JAPANESE, "error.required");

// ロケール指定なし（Locale.ROOT を使用）
String msg = PropertiesFileUtil.getMessage("error.required");

// キーの存在確認
boolean exists = PropertiesFileUtil.hasMessage("error.required");
```

キーが存在しない場合は例外をスローせず **キー文字列をそのまま返します**。

---

## messages_with_item_names.properties を読む

`{item_name}` が項目名プレースホルダーになっているメッセージを格納します。
`messages.properties` との違いは、メッセージに `{item_name}` が含まれる点です。
フレームワークが `{item_name}` を実際の項目名に置換した上でユーザーに表示します。

キーが存在しない場合は `messages.properties` へ自動的にフォールバックします。

```java
// ロケール指定
String msg = PropertiesFileUtil.getMessageWithItemName(Locale.JAPANESE, "error.required");

// ロケール指定なし（Locale.ROOT を使用）
String msg = PropertiesFileUtil.getMessageWithItemName("error.required");

boolean exists = PropertiesFileUtil.hasMessageWithItemName("error.required");
```

### messages.properties との使い分け

`messages_with_item_names.properties` にフォールバックするため、ファイルを別途作成しなくても機能します。
`getMessage(...)` と `getMessageWithItemName(...)` を同じキーで呼んでも、
`messages.properties` しか存在しない場合は同じ値が返ります。

`messages_with_item_names.properties` を別途作成するのが有効なケース：

- 項目名なし版（`messages.properties`）と項目名あり版でメッセージの文言を変えたい場合
- 項目名付きメッセージを整理のためにファイルを分けたい場合

---

## item_names.properties を読む

画面上の項目名（フィールドのラベル）のローカライズ済み表示名を格納するファイルです。
バリデーションエラーメッセージなどで「`{0}` は必須です」の `{0}` 部分に使われます。

キーが存在しない場合は `messages.properties` にフォールバックします。
このフォールバックにより、`item_names.properties` を作成せずに `messages.properties` だけで
`getItemName(...)` を動作させることができます。

```java
// ロケール指定
String itemName = PropertiesFileUtil.getItemName(Locale.JAPANESE, "user.name");

// ロケール指定なし（Locale.ROOT を使用）
String itemName = PropertiesFileUtil.getItemName("user.name");

boolean exists = PropertiesFileUtil.hasItemName("user.name");
```

---

## enum_names.properties を読む

Enum 値のローカライズ済み表示名を格納するファイルです。
例えば `Status.ACTIVE` というキーに対して「有効」などの表示名を定義します。

キーが存在しない場合は `messages.properties` にフォールバックします。

```java
// ロケール指定
String enumName = PropertiesFileUtil.getEnumName(Locale.JAPANESE, "Status.ACTIVE");

// ロケール指定なし（Locale.ROOT を使用）
String enumName = PropertiesFileUtil.getEnumName("Status.ACTIVE");

boolean exists = PropertiesFileUtil.hasEnumName("Status.ACTIVE");
```

---

## 複数モジュールのファイルを一括読み込み

ecuacion では、アプリを複数のモジュール（`base`, `core`, `web`, `batch` など）に分割することを想定しています。
例えばアプリ名が `sample-app` の場合：

```text
sample-app-base  → messages_base.properties
sample-app-core  → messages_core.properties
sample-app-web   → messages.properties
```

`PropertiesFileUtil.getMessage(...)` は上記すべてを一括検索します。
同じキーが複数ファイルに定義されていると **例外がスロー** されます（重複検知）。

上記の `base`・`core`・`web` のようなアプリ固有のサフィックスは、
ecuacion-splib を使用している場合 `spring.messages.basename` の設定から自動的に検出・登録されます。
ecuacion-splib を使用しない場合や、独自のモジュール・フレームワークを構築する場合は
手動で呼び出します。

```java
PropertiesFileUtil.addResourceBundlePostfix("mymodule");
// → messages_mymodule.properties, application_mymodule.properties なども検索対象に追加される
```

---

## キーが存在しない場合の挙動

| ファイル種別 | キー未存在時の挙動 |
| --- | --- |
| `application.properties` | 例外をスロー |
| それ以外 | キー文字列をそのまま返す（例外なし） |

`application.properties` だけ例外をスローするのは、設定値の欠落をアプリ起動時に確実に検知するためです。
一方 `messages.properties` などは開発中に未定義キーが画面に表示される方が都合が良いため、例外をスローしません。

---

## `.default` サフィックスによるデフォルト値の上書き

ecuacion の各モジュールが提供するキーには `.default` サフィックスが付いています。
アプリ側で上書きしたい場合は `.default` なしの同名キーをアプリのファイルに定義します。

```properties
# ecuacion モジュール内のファイル
some.key.default=ecuacion のデフォルト値

# アプリ側のファイル（上書き）
some.key=アプリ独自の値
```

`getApplication("some.key")` はアプリ側の値を優先して返します。
