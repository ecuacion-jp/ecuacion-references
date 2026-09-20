[セットアップ](page?id=rest/setup&lang=ja) で依存関係を追加済みであることを前提に、
このページでは `/api/key/**` 配下に実際に動く最小のエンドポイントを1つ追加します。

## 1. `SplibRestConfig` を有効化する

`SplibRestConfig`（`jp.ecuacion.splib.rest.config` パッケージに存在）は `ecuacion-splib-rest` の動作に必要なパッケージ（`jp.ecuacion.splib.core.config`・`jp.ecuacion.splib.rest.advice`・
`jp.ecuacion.splib.rest.controller`）をコンポーネントスキャンします。アプリケーションの設定クラスからインポートしてください。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 2. 照合ロジックを実装する

`SplibApiKeyExpectedValueProvider` を実装した Bean を登録します。

```java
@Component
public class AppApiKeyExpectedValueProvider implements SplibApiKeyExpectedValueProvider {

  @Override
  public Collection<SplibApiKeyExpectedValue> getExpectedValues(@Nullable String apiKeyId,
      String presentedApiKey) {
    return List.of(
        new SplibApiKeyExpectedValue("your-api-key-here", SplibApiKeyComparisonMode.PLAIN));
  }
}
```

ここでは固定のキー1つとだけ比較する最小構成にしています。実運用での期待値の取得方法（DB検索、
複数キーの扱いなど）は [認証処理](page?id=rest/security/api-key/authentication&lang=ja) の
「照合ロジックの実装」を参照してください。

## 3. `SplibRestSecurityConfig` を継承する

`SplibRestSecurityConfig` は抽象クラスで、
[概要](page?id=rest/overview&lang=ja) で説明した 4 つのセキュリティフィルターチェーンを設定します。
コンストラクタで手順2の Provider を受け取り、そのまま渡す具象の `@Configuration` サブクラスを
用意してください。

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

上で登録した Bean は Spring が自動的に注入します。引数を `@Nullable` にしているのは、該当する Bean
を登録していなくてもアプリが起動できるようにするためです（その場合は `/api/key/**` へのリクエストは
すべて拒否されます）。

## 4. コントローラーを書く

```java
@RestController
public class HelloKeyController {

  @GetMapping("/api/key/hello")
  public HelloResponse hello() {
    return new HelloResponse("Hello, key!");
  }
}

record HelloResponse(String message) {
}
```

`/api/key/**` 配下に置いているので、手順2・3の設定により有効な `X-Api-Key` を送ったリクエストだけが到達します。

## 5. 呼び出してみる

アプリケーションをローカルで起動し、curl で叩いてみます。`X-Api-Key` ヘッダーを付けない場合や
違う値を送った場合は `401` が返ることも確認してみてください。

```
curl -H "X-Api-Key: your-api-key-here" http://localhost:8080/api/key/hello
```

```json
{"message":"Hello, key!"}
```
