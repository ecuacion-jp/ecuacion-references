リクエストは、組み込みの `/api/public/**` / `/api/ecuacion-splib/public/**`（8）→ `/api/key/**`
（9）→ `/api/ecuacion-splib/key/**`（10）→ `/api/**` の deny-all（11）の順にフォールスルーします。
`@Order(11)`、`/api/**` に到達したリクエストはすべて拒否されます（`anyRequest().denyAll()`）。
これは `/api/public/**` にも `/api/ecuacion-splib/public/**` にも `/api/key/**` にも
`/api/ecuacion-splib/key/**` にも該当しない、`/api/**` 配下すべてに対する catch-all です。

> **重要:** これら 4 つのチェーンはいずれも `/api/**` 配下のパスにしかマッチしません。`/api/**`
> の外側にマッピングされたエンドポイント（単に `/api` プレフィックスを付け忘れた場合を含む）は
> これらのどのチェーンにも見えず、`ecuacion-splib-rest` はそのパスに対して一切のセキュリティを
> 提供しません。アプリケーション側でそのパス用の独自の `SecurityFilterChain` を登録しない限り
> （後述）、無防備な状態のままになります。

## 独自のセキュリティポリシーを追加する

組み込みプレフィックス以外のパス（`/api/` で始まらないパスなど）でエンドポイントを公開したい場合は、
そのパス用の `securityMatcher` を持つ独自の `SecurityFilterChain` Bean を登録してください。`@Order` は何でも構いません — `SplibRestSecurityConfig` の 4 つのチェーンはいずれも `/api/**` 外のパスにはマッチしないため、独自のチェーンと衝突することはありません。

```java
@Configuration
public class AppCustomApiSecurityConfig {

  @Order(100)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/custom/**");

    // このパス用の認証・認可をここで設定する

    return http.build();
  }
}
```

一方、`/api/**` の一部のサブパスに別のポリシーを適用したい場合は、`@Order` が **8 より小さい** 独自の `SecurityFilterChain` Bean を登録してください。
Spring Security はフィルターチェーンを `@Order` の昇順で評価し、リクエストにマッチする最初の
`securityMatcher` で処理を止めるため、独自のチェーンは組み込みのチェーン（`/api/**` の catch-all
deny-all である `@Order(11)` を含む）より先に評価される必要があります。

```java
  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // このパス用の認証・認可をここで設定する

    return http.build();
  }
```
