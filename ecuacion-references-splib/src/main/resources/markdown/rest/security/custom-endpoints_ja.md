リクエストは、組み込みの `/api/public/**` / `/api/ecuacion-splib/public/**`（11）→ `/api/key/**`
（12）→ `/api/ecuacion-splib/key/**`（13）→ `/api/**` の deny-all（14）の順にフォールスルーします。
4番目のチェーンは `@Order(14)`、`securityMatcher("/api/**")` で、そこに到達したリクエストは
すべて拒否されます（`anyRequest().denyAll()`）。これは `/api/public/**` にも
`/api/ecuacion-splib/public/**` にも `/api/key/**` にも `/api/ecuacion-splib/key/**` にも
該当しない、`/api/**` 配下すべてに対する catch-all です。（`ecuacion-splib-rest` のチェーンは
`@Order` の `11`〜`19` を予約しています。詳細は `SplibRestSecurityConfig` の Javadoc を参照。）

5番目のチェーンは `securityMatcher("/**")` で、明示的な `@Order` を持たない（＝最後に評価される）
ため、そこに到達したリクエストもすべて拒否します — これが `/api/**` **外**のパスに対する
catch-all です。カスタムの `SecurityFilterChain` Bean を1つでも登録すると、Spring Boot 標準の
デフォルトセキュリティチェーンは完全に無効化されるため、この5番目のチェーンがなければ、
他の4つのどれにもマッチしないパス（後から `/api/**` の外に追加したエンドポイントや、単に `/api`
プレフィックスを付け忘れた場合など）は Spring Security 自体を素通りしてしまいます —
「拒否される」のではなく「そもそもチェックされない」状態です。このチェーンにより、
デフォルトの挙動が「拒否」になります。

## 独自のセキュリティポリシーを追加する

組み込みプレフィックス以外のパス（`/api/` で始まらないパスなど）でエンドポイントを公開したい場合は、
そのパス用の `securityMatcher` を持つ独自の `SecurityFilterChain` Bean を登録してください。
明示的な有限の `@Order` であれば何でも構いません — 上記の（`@Order` なしの）`/**` catch-all
より自動的に先に評価されるため、そのパスのポリシーは catch-all の無条件拒否ではなく、
あなたのチェーンが決めることになります。

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

> アプリケーションが `ecuacion-splib-web` も使用している場合、`/api` 以外のパスに対する
> 同じcatch-allの役割は既に `SplibWebSecurityConfig`（`@Order(29)`）が担っています —
> [フォームログインとアクセス制御](page?id=web/security/form-login&lang=ja) を参照してください。
> 上記のような独自チェーンは、そのクラスのデフォルト（`permitAll`／ロールベースのルール）とは
> **異なる**ポリシーを適用したいパスにのみ登録してください。

一方、`/api/**` の一部のサブパスに別のポリシーを適用したい場合は、`@Order` が **11 より小さい** 独自の `SecurityFilterChain` Bean を登録してください。
Spring Security はフィルターチェーンを `@Order` の昇順で評価し、リクエストにマッチする最初の
`securityMatcher` で処理を止めるため、独自のチェーンは組み込みのチェーン（`/api/**` の catch-all
deny-all である `@Order(14)` を含む）より先に評価される必要があります。

```java
  @Order(1)
  @Bean
  SecurityFilterChain filterChainForCustomApi(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/custom/**");

    // このパス用の認証・認可をここで設定する

    return http.build();
  }
```
