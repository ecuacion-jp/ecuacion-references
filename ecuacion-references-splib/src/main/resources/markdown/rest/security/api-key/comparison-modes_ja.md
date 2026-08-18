`SplibApiKeyComparisonMode` は、
[`SplibApiKeyExpectedValueProvider`](page?id=rest/security/api-key/overview&lang=ja)
が返す値1件1件について、クライアントが提示する `X-Api-Key` ヘッダーの値とどう比較するかを選択します。
`SplibApiKeyExpectedValue` を通じて値ごとに持たせます。

```java
new SplibApiKeyExpectedValue(storedValue, SplibApiKeyComparisonMode.BCRYPT)
```

アプリケーション全体で切り替える設定はありません。`getExpectedValues` の1回の呼び出しで
`PLAIN` と `BCRYPT` の値を自由に混在させられます。これにより、保存済みのキーを平文から
bcrypt へ移行する際、一度に全件切り替えるのではなく1行ずつ変換し、移行期間中は両方の形式を受け付ける、といった運用が可能になります。

このモード選択は `/api/key/**` 専用です。[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja)
（`/api/ecuacion-splib/key/**`）は `SplibApiKeyComparisonMode` を一切使いません。こちらの認証情報は
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` または `...password-bcrypt` で直接設定する形で、
別途モード用のプロパティはありません。

## `PLAIN`

`SplibApiKeyExpectedValue.value()` はキーそのものです。提示された値と（定数時間で）直接比較されます。

## `BCRYPT`

`SplibApiKeyExpectedValue.value()` はキーそのものではなく、キーの bcrypt ハッシュです。
これにより、生のキーがアプリケーションから読み取り可能な場所に一切保存されなくなります。
提示されたヘッダー値は、`BCRYPT` の各値に対して Spring Security の
`BCryptPasswordEncoder.matches` で照合されます（最初に一致した時点で打ち切らず、全件照合します）。

保存すべきハッシュ値は、ユーザーパスワードの保存と同様に `BCryptPasswordEncoder` で生成します。

```java
new BCryptPasswordEncoder().encode(rawApiKey)
```

アプリを介さずコマンドラインだけで生成したい場合は、例えば `htpasswd` コマンドが使えます。

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

bcrypt は意図的に低速なアルゴリズムなので、`getExpectedValues` が返す値の数だけ照合処理が走ります。
（例えば `X-Api-Key-Id` ヘッダーで絞り込むなどして）発行済みキーを毎回すべて返すのではなく、
コレクションを小さく保つようにしてください。
