`SplibRestSecurityConfig` が登録する 4 番目のフィルターチェーン（`@Order(11)`、
`securityMatcher("/api/**")`）に到達したリクエストはすべて拒否されます（`anyRequest().denyAll()`）。
これは `/api/public/**` にも `/api/ecuacion-splib/public/**` にも `/api/key/**` にも
`/api/ecuacion-splib/key/**` にも該当しない、`/api/**` 配下すべてに対する catch-all です。

## 独自のセキュリティポリシーを追加する

組み込みプレフィックス以外のパス（`/api/` で始まらないパスなど）でエンドポイントを公開したい場合は、
そのパス用の `securityMatcher` を持つ独自の `SecurityFilterChain` Bean を登録してください。`@Order` は何でも構いません — `SplibRestSecurityConfig` の 4 つのチェーンはいずれも `/api/**` 外のパスにはマッチしないため、独自のチェーンと衝突することはありません。

一方、`/api/**` の一部のサブパスに別のポリシー（セッションベース認証、独自のヘッダー方式など）を適用したい場合は、`@Order` が **8 より小さい** 独自の `SecurityFilterChain` Bean を登録してください。
Spring Security はフィルターチェーンを `@Order` の昇順で評価し、リクエストにマッチする最初の
`securityMatcher` で処理を止めるため、独自のチェーンは組み込みのチェーン（`/api/**` の catch-all
deny-all である `@Order(11)` を含む）より先に評価される必要があります。

```java
@Configuration
public class AppCustomApiSecurityConfig {

  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // このパス用の認証・認可をここで設定する

    return http.build();
  }
}
```

独自の `securityMatcher` にマッチしなかったリクエストは、組み込みの `/api/public/**` /
`/api/ecuacion-splib/public/**`（8）→ `/api/key/**`（9）→ `/api/ecuacion-splib/key/**`（10）→
`/api/**` の deny-all（11）の順にフォールスルーします。

## 予約済みの Order 値

`ecuacion-splib-rest` は
[Public エンドポイント](page?id=rest/security/public-endpoints&lang=ja)、
[API キー認証](page?id=rest/security/api-key/overview&lang=ja)、
[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja)、
deny-all ルールにそれぞれ `@Order(8)`・`@Order(9)`・`@Order(10)`・`@Order(11)` を使用しています。
アプリケーション側で定義するチェーンはこの範囲の外（11 の deny-all は最終フォールバックとして残す必要があるため、8 より小さい値）を使用してください。
