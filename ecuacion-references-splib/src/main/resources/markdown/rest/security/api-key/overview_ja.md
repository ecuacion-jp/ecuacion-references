`/api/key/**` 配下にマッピングされたエンドポイントは、有効な `X-Api-Key` ヘッダーが必須です。
このフィルターチェーンは `@Order(9)` で実行され、`/api/public/**`（8）の後、
`/api/ecuacion-splib/key/**`（10）および残りすべてを拒否する `/api/**` のルール（11）より前に評価されます。

## リクエストヘッダー

| ヘッダー | 必須 | 意味 |
| --- | --- | --- |
| `X-Api-Key` | 必須 | API キー本体。 |
| `X-Api-Key-Id` | 任意 | 任意のキー識別子。Provider へそのまま渡されます。<br>AWS のアクセスキー ID や HTTP Basic のユーザー名に近い位置づけで、「どのレコードの期待値と照合するか」を特定するために使います。<br>単一の共有キーのみを扱う実装では無視して構いません。 |

## 照合ロジックの実装

`SplibApiKeyExpectedValueProvider` を実装した Bean を登録します。

```java
@Component
public class AppApiKeyExpectedValueProvider implements SplibApiKeyExpectedValueProvider {

  @Override
  public Collection<SplibApiKeyExpectedValue> getExpectedValues(@Nullable String apiKeyId,
      String presentedApiKey) {
    // application.properties の固定値、apiKeyId をキーにした DB 検索など、
    // アプリケーションに合った方法で期待値（複数可）を取得する。返す値ごとに
    // SplibApiKeyComparisonMode を持たせるので、平文と bcrypt ハッシュの値を混在できる。
    // 該当なし（apiKeyId が未知など）の場合は null または空のコレクションを返してリクエストを拒否する。
    return lookUpExpectedValues(apiKeyId);
  }
}
```

同じ `apiKeyId` に対して複数の有効な値を返すこともできます（発行したトークンごとに1つ、など）。
これにより、漏洩・失効したキー1つを他のキーを無効にすることなく取り除けます。提示された
`presentedApiKey` が返された値のいずれかと一致すれば、リクエストは認証されます。

返される `SplibApiKeyExpectedValue` はそれぞれ自分自身の `SplibApiKeyComparisonMode`（平文か
bcrypt か）を持ちます。アプリケーション全体で1つに固定する設定ではないため、1回の呼び出しで両方を混在させることもできます（例：保存済みのキーを平文から bcrypt へ1件ずつ移行している間など）。
詳しくは [比較モード](page?id=rest/security/api-key/comparison-modes&lang=ja) を参照してください。

`SplibApiKeyExpectedValueProvider` の Bean が一つも登録されていない場合、`/api/key/**` へのリクエストはすべて拒否されます。`/api/public/**` と異なり、このプレフィックスに「キー不要」というデフォルト動作はありません。

## Provider を `AppRestSecurityConfig` に渡す

上記の Bean を登録しただけでは不十分です。[クイックスタート](page?id=rest/quickstart&lang=ja)
の手順通りだと、`AppRestSecurityConfig` は `super(null)` を呼んでいるため、Provider の Bean を登録しても `/api/key/**` は引き続きすべて拒否されます。コンストラクタで Provider を受け取り、
そのまま渡すように変更してください。

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

上で登録した Bean は Spring が自動的に注入します。引数を `@Nullable` にしているのは、該当する Bean を登録していなくてもアプリが起動できるようにするためです（その場合はクイックスタートのデフォルトと同じく
`/api/key/**` は引き続きすべて拒否されます）。このコンストラクタ引数は `/api/ecuacion-splib/key/**`
とは無関係です。そちらには Provider Bean 自体が存在せず、代わりに `application.properties` で設定します。詳しくは
[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja) を参照してください。

## 拒否時の挙動

`SplibApiKeyAuthenticationFilter` は以下のいずれの場合も同じ汎用的な `401` を返すため、
呼び出し側はどのケースに該当したかを区別できません。

- `X-Api-Key` ヘッダーが欠落または空。
- `SplibApiKeyExpectedValueProvider` の Bean が登録されていない。
- Provider が `null` または空のコレクションを返した（例：`apiKeyId` が未知）。
- 提示されたキーが期待値と一致しない。

詳細はサーバー側のログにのみ出力され、提示されたキーの値自体はログに出力されません。
比較には `MessageDigest.isEqual`（定数時間比較）を用いており、タイミング攻撃を防いでいます。

## 認証成功時

照合に成功すると、`apiKeyId`（`X-Api-Key-Id` が送られていない場合は `"api-key-client"`）として、
`ROLE_API_KEY` 権限で認証されます。

## CSRF について

`X-Api-Key` はブラウザが自動付与するアンビエントな資格情報ではないため、CSRF が無効化されています。
詳しくは [概要](page?id=rest/overview&lang=ja) を参照してください。
