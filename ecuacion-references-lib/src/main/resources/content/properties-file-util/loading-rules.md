# ロード動作・詳細設定

## フォールバック

一部のファイル種別では、キーが見つからない場合に別のファイル種別へフォールバックします。
フォールバックが設定されている種別は、対応するファイルを作成しなくてもフォールバック先のファイルで動作します。
ファイルを分けた方が管理しやすい場面がある一方、`messages.properties` に一元的に書くのが一般的な運用でもあるため、そのような使い方もそのまま動作するようにフォールバックが設定されています。

| ファイル名 | フォールバック先 |
| --- | --- |
| `application[_xxx].properties` | なし |
| `constants[_xxx].properties` | なし |
| `messages[_xxx].properties` | なし |
| `messages_with_item_names[_xxx].properties` | `messages` |
| `item_names[_xxx].properties` | `messages` |
| `enum_names[_xxx].properties` | `messages` |
| `ValidationMessages[_xxx].properties` | なし |
| `ValidationMessagesWithItemNames[_xxx].properties` | `ValidationMessages` |
| `ValidationMessagesPatternDescriptions[_xxx].properties` | なし |

例えば `item_names.properties` を作成せずに `messages.properties` だけを用意しても、
`getItemName(...)` は `messages.properties` からキーを検索して返します。

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
