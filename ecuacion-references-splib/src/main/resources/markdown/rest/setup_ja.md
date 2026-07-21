## 1. 依存関係の追加

```xml
<dependency>
    <groupId>jp.ecuacion.splib</groupId>
    <artifactId>ecuacion-splib-rest</artifactId>
</dependency>
```

WAR としてパッケージする場合は `spring-boot-starter-tomcat` を `provided` スコープで追加します。

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

## 2. `SplibRestConfig` を有効化する

`SplibRestConfig` は `ecuacion-splib-rest` の動作に必要なパッケージ
（`jp.ecuacion.splib.core.config`・`jp.ecuacion.splib.rest.advice`・`jp.ecuacion.splib.rest.controller`）を
コンポーネントスキャンします。アプリケーションの設定クラスからインポートしてください。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.rest.config")
public class AppConfig {
}
```

## 3. `SplibRestSecurityConfig` を継承する

`SplibRestSecurityConfig` は抽象クラスで、
[概要](/public/showMarkdown/page?id=rest/overview&lang=ja) で説明した 3 つのセキュリティフィルターチェーンを
設定します。アプリケーション側で具象の `@Configuration` サブクラスを用意してください。

```java
@Configuration
public class AppRestSecurityConfig extends SplibRestSecurityConfig {

  public AppRestSecurityConfig(
      @Nullable SplibApiKeyExpectedValueProvider apiKeyExpectedValueProvider) {
    super(apiKeyExpectedValueProvider);
  }
}
```

- `/api/key/**` を使用しない場合は `null` を渡す（あるいは `SplibApiKeyExpectedValueProvider` の Bean を
  登録しない）ことで、そのプレフィックスへのリクエストはすべて拒否されるようになります。
  Provider の実装方法は
  [API キー認証](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=ja) を参照してください。
- これ以上の設定なしに、`/api/public/**`（許可）・`/api/key/**`（API キー必須）・
  それ以外の `/api/**`（拒否）が動作するようになります。

## 4. （任意）独自アクションで例外を処理する

未捕捉の例外が `SplibRestExceptionHandler` に到達した際に、任意の副作用（アラートメール送信など）を
実行したい場合は `SplibExceptionHandlerAction` を実装した Bean を登録してください。
[例外処理](/public/showMarkdown/page?id=rest/exception-handling&lang=ja) を参照してください。
