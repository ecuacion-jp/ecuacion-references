[概要](page?id=rest/security/api-key/overview&lang=ja) で説明した `/api/key/**` の認証は、
アプリケーション側で登録する `SplibApiKeyExpectedValueProvider` の実装によって決まります。

## 照合ロジックの実装

`SplibApiKeyExpectedValueProvider` を実装した Bean を登録します。実装方法は
[クイックスタート](page?id=rest/security/api-key/quickstart&lang=ja) を参照してください。

同じ `apiKeyId` に対して複数の有効な値を返すこともできます（発行したトークンごとに1つ、など）。
これにより、漏洩・失効したキー1つを他のキーを無効にすることなく取り除けます。提示された
`presentedApiKey` が返された値のいずれかと一致すれば、リクエストは認証されます。

返される `SplibApiKeyExpectedValue` はそれぞれ自分自身の `SplibApiKeyComparisonMode`（平文か
bcrypt か）を持ちます。アプリケーション全体で1つに固定する設定ではないため、1回の呼び出しで両方を混在させることもできます（例：保存済みのキーを平文から bcrypt へ1件ずつ移行している間など）。
詳しくは下記の [比較モード](#比較モード) を参照してください。

`SplibApiKeyExpectedValueProvider` の Bean が一つも登録されていない場合、`/api/key/**` へのリクエストはすべて拒否されます。`/api/public/**` と異なり、このプレフィックスに「キー不要」というデフォルト動作はありません。

## 比較モード

### `PLAIN`

設定した値がキーそのものです。提示された値と（定数時間で）直接比較されます。

### `BCRYPT`

設定した値はキーそのものではなく、キーの bcrypt ハッシュです。これにより、生のキーがアプリケーションから
読み取り可能な場所に一切保存されなくなります。提示された値は Spring Security の
`BCryptPasswordEncoder.matches` で照合されます。

保存すべきハッシュ値は、ユーザーパスワードの保存と同様に `BCryptPasswordEncoder` で生成します。

```java
new BCryptPasswordEncoder().encode(rawApiKey)
```

アプリを介さずコマンドラインだけで生成したい場合は、例えば `htpasswd` コマンドが使えます。

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

bcrypt は意図的に低速なアルゴリズムなので、複数の値と比較する場合はその分だけ処理コストがかかる点に
注意してください。

## 拒否時の挙動

`SplibApiKeyAuthenticationFilter` は以下のいずれの場合も同じ汎用的な `401` を返すため、
呼び出し側はどのケースに該当したかを区別できません。

- 送信元IPが現在ロックアウト中(下記[レート制限](#レート制限ブルートフォース対策)を参照)。
- `X-Api-Key` ヘッダーが欠落または空。
- `SplibApiKeyExpectedValueProvider` の Bean が登録されていない。
- Provider が `null` または空のコレクションを返した（例：`apiKeyId` が未知）。
- 提示されたキーが期待値と一致しない。

詳細はサーバー側のログにのみ出力され、提示されたキーの値自体はログに出力されません。
比較には `MessageDigest.isEqual`（定数時間比較）を用いており、タイミング攻撃を防いでいます。

## レート制限（ブルートフォース対策）

キー不一致1回ごとに、送信元IPごとの失敗回数としてカウントします。完全にインメモリ（JVMヒープ、DB不使用）です。ある送信元IPが `window-seconds` 秒以内に `max-failures` 回不一致を起こすと、その IP は `lockout-seconds` 秒間ロックアウトされ、以降は上記と同じ汎用 `401` で（キー照合自体を試みることなく）即座に拒否されます。これは `BCRYPT` モード時の CPU コストも同時に抑えます — bcrypt照合を毎回全登録キーに対して強制させる攻撃も、`max-failures` 回で打ち切られ無制限には繰り返せなくなります。照合に成功すると、その IP のカウントはクリアされます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.splib.rest.api-key.rate-limit.max-failures` | int | ロックアウトまでにウィンドウ内で許容する不一致回数。デフォルト: `10`。 |
| `jp.ecuacion.splib.rest.api-key.rate-limit.window-seconds` | long | 上記カウントが適用される時間窓（秒）。デフォルト: `60`。 |
| `jp.ecuacion.splib.rest.api-key.rate-limit.lockout-seconds` | long | ロックアウト発動後、送信元IPがロックアウトされ続ける秒数。デフォルト: `300`。 |

インメモリであるため、再起動でカウントはリセットされ、ロードバランサ配下の複数インスタンス間でも共有されません（各インスタンスが送信元IPを個別に追跡します）。本モジュールの通常の想定デプロイ（単一インスタンス）であれば、DBを不要にする代わりのトレードオフとして許容範囲です。

**リバースプロキシ配下の場合:** 送信元IPは `HttpServletRequest.getRemoteAddr()`（直接のTCP接続相手）です。追加設定なしにリバースプロキシ（nginx、ALB等）配下に置くと、すべてのリクエストでプロキシ自身のアドレスになり、配下の全トラフィックが1つのIPにまとめられてしまいます。これへの対処として `X-Forwarded-For` をここで信頼するのは避けてください — 直接の呼び出し元も自由に設定できるヘッダーなので、これを無条件に信頼すると攻撃者はリクエストごとに新しいIPを詐称してロックアウトを完全に回避できてしまいます。代わりに、プロキシが `X-Forwarded-For` を正しく設定（かつクライアント由来の値を上書き）することを信頼できるなら、Spring Boot 標準の
[`server.forward-headers-strategy=native`](https://docs.spring.io/spring-boot/reference/web/servlet.html#web.servlet.embedded-container.customizing.programmatic)
（または `framework`）を有効にしてください。これにより、このフィルターに到達する時点で `getRemoteAddr()` 自体が既に実クライアントIPを返すようになり、このフィルター自体はプロキシの存在を意識せずに済みます。

## 認証成功時

照合に成功すると、`apiKeyId`（`X-Api-Key-Id` が送られていない場合は `"api-key-client"`）として、
`ROLE_API_KEY` 権限で認証されます。`/api/key/**` の可否自体はこの時点のフィルター通過で決まっており
（フィルターより後段の認可設定は `permitAll`）、`ROLE_API_KEY` はそれ自体でアクセスを制限するものでは
ありません。コントローラー側で `@PreAuthorize("hasAuthority('ROLE_API_KEY')")` のように使い、
呼び出しが API キー認証によるものかどうかを判定する用途などに利用できます。

## キーごとに権限を変える

`SplibApiKeyExpectedValue(value, mode)`（2 引数）を使う限り、どのキーで認証しても付与される権限は
`ROLE_API_KEY` だけで変わりません。キーごとに異なる権限を持たせたい場合は、3 引数コンストラクタで
`extraAuthorities` を渡してください。マッチしたエントリの `extraAuthorities` が `ROLE_API_KEY` に
追加で付与されます。

```java
new SplibApiKeyExpectedValue(key, mode, List.of("ROLE_ADMIN_API_KEY"))
```
