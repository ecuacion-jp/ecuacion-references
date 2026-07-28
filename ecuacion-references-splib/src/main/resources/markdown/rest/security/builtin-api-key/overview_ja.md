`/api/ecuacion-splib/key/**` 配下にマッピングされたエンドポイントは、
[API キー認証](page?id=rest/security/api-key/overview&lang=ja) と同様に有効な `X-Api-Key`
ヘッダーが必須です。ただしこのプレフィックスは、`ecuacion-splib` 自身の組み込みエンドポイントのうち
副作用のあるもの専用に予約されています。現時点では
[Config エンドポイント](page?id=rest/config-endpoints&lang=ja)
（`ClearPropertiesCacheController`、`SystemErrorController`）がこれに該当します。
このフィルターチェーンは `@Order(10)` で実行され、`/api/key/**`（9）の後、
残りすべてを拒否する `/api/**` のルール（11）より前に評価されます。

## なぜキーセットを分けているか

`SplibBuiltinApiKeyExpectedValueProvider` は `SplibApiKeyExpectedValueProvider` とは別のインターフェースで、
`jp.ecuacion.splib.rest.builtin-api-key.mode` も `jp.ecuacion.splib.rest.api-key.mode` とは
別のプロパティです。これにより、`ecuacion-splib` 自身の運用系エンドポイントを守るキーを、
アプリケーションが `/api/key/**` で独自に発行しているキーとは独立して発行・ローテーション・失効
できるようにしています。

## リクエストヘッダー

`/api/key/**` と同一です：`X-Api-Key`（必須）と `X-Api-Key-Id`（任意）。詳しくは
[API キー認証](page?id=rest/security/api-key/overview&lang=ja) を参照してください。

## 照合ロジックの実装

`SplibBuiltinApiKeyExpectedValueProvider` を実装した Bean を登録します。

```java
@Component
public class AppBuiltinApiKeyExpectedValueProvider
    implements SplibBuiltinApiKeyExpectedValueProvider {

  @Override
  public Collection<String> getExpectedValues(@Nullable String apiKeyId, String presentedApiKey) {
    // application.properties の固定値、apiKeyId をキーにした DB 検索など、
    // アプリケーションに合った方法で期待値（複数可）を取得する。
    // 該当なし（apiKeyId が未知など）の場合は null または空のコレクションを返してリクエストを拒否する。
    return lookUpExpectedValues(apiKeyId);
  }
}
```

`/api/key/**` と同様、複数の有効な値を返すこともでき、比較モード（平文 or SHA-256 ハッシュ、
[比較モード](page?id=rest/security/api-key/comparison-modes&lang=ja) 参照）はアプリケーション全体で
制御されます（こちらは `jp.ecuacion.splib.rest.builtin-api-key.mode`、デフォルト `PLAIN`）。
Provider の Bean が一つも登録されていない場合、`/api/ecuacion-splib/key/**` へのリクエストは
すべて拒否されます。

## Provider を `AppRestSecurityConfig` に渡す

コンストラクタで Provider を受け取り、`super` の第 2 引数として渡してください。

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider,
      @Nullable SplibBuiltinApiKeyExpectedValueProvider builtinApiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider, builtinApiKeyExpectedValueProvider);
  }
}
```

`null` のまま（クイックスタートのデフォルト）にすると、`/api/ecuacion-splib/key/**`
（つまり `clearPropertiesCache`・`systemError`）は、Provider 未登録時の `/api/key/**` と同様に
引き続きすべて拒否されます。

## 拒否時の挙動・認証成功時

`/api/key/**` と同一です。拒否理由（ヘッダー欠落、Provider 未登録、不一致、キー相違）に関わらず
汎用的な `401` を返し、比較には `MessageDigest.isEqual`（定数時間比較）を使用します。
認証に成功すると `ROLE_BUILTIN_API_KEY` 権限で認証されます（`/api/key/**` では `ROLE_API_KEY`）。

## CSRF について

`/api/key/**` と同じ理由で無効化されています。`X-Api-Key` はブラウザが自動付与するアンビエントな
資格情報ではないためです。詳しくは [概要](page?id=rest/overview&lang=ja) を参照してください。
