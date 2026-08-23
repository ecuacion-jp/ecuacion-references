[セットアップ](page?id=rest/setup&lang=ja) で依存関係を追加済みであることを前提に、
このページでは必要な設定クラスを揃え、実際に動く最小のエンドポイントを1つ追加します。

## 1. `SplibRestConfig` を有効化する

`SplibRestConfig`（`jp.ecuacion.splib.rest.config` パッケージに存在）は `ecuacion-splib-rest` の動作に必要なパッケージ（`jp.ecuacion.splib.core.config`・`jp.ecuacion.splib.rest.advice`・
`jp.ecuacion.splib.rest.controller`）をコンポーネントスキャンします。アプリケーションの設定クラスからインポートしてください。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 2. `SplibRestSecurityConfig` を継承する

`SplibRestSecurityConfig` は抽象クラスで、
[概要](page?id=rest/overview&lang=ja) で説明した 4 つのセキュリティフィルターチェーンを設定します。アプリケーション側で具象の `@Configuration` サブクラスを用意してください。

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig() {
    super(null);
  }
}
```

この記載で `/api/public/**` へのアクセスが可能となります。

## 3. コントローラーを書く

```java
@RestController
public class HelloController {

  @GetMapping("/api/public/hello")
  public HelloResponse hello() {
    return new HelloResponse("Hello, world!");
  }
}

record HelloResponse(String message) {
}
```

`/api/public/**` 配下に置いているので、手順2で継承した `SplibRestSecurityConfig` により追加の設定なしで誰でも呼び出せます（`permitAll`）。

## 4. 呼び出してみる

アプリケーションをローカルで起動し、curl で叩いてみます。

```
curl http://localhost:8080/api/public/hello
```

```json
{"message":"Hello, world!"}
```

このリファレンスサイト自身も `ecuacion-splib-rest` を使っており、上のコントローラーとまったく同じものを
[`/api/public/hello`](../../api/public/hello) として公開しています。実装はサイトのソースにある
`jp.ecuacion.references.splib.tutorial.rest.HelloController` を参照してください。
