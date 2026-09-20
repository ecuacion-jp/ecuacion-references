[セットアップ](page?id=web/setup&lang=ja)で追加した依存関係がある前提で、このページでは必要な設定クラスを組み込み、実際のセキュリティポリシーでアプリが起動することを確認します。確認対象には `ecuacion-splib-web` 自身が持つ組み込み管理画面を使うので、アプリ独自のコントローラーやページはまだ不要です。

## 1. `ecuacion-splib-web` の設定クラスを有効化する

アプリの設定クラスから `jp.ecuacion.splib.web.config` をコンポーネントスキャンします。これにより、後述の[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)で動作確認に使う組み込みコントローラーなどを登録する `SplibWebConfig` を含む一式が読み込まれます。

```java
@Configuration
@ComponentScan(basePackages = "jp.ecuacion.splib.web.config")
public class AppConfig {
}
```

## 2. `SplibWebSecurityConfig` を継承する

どのアプリケーションも `SplibWebSecurityConfig` を継承したBeanが必要です。これがないと、[概要](page?id=web/overview&lang=ja)で説明したフェイルクローズなフォールバックにより、すべてのリクエストが拒否されます。アプリ自体のログインをまだ持っていない場合は、`SplibWebSecurityConfigForNoLogin` を使うのが、実際に機能する（許可的ではありますが）セキュリティポリシーを最短で用意する方法です。

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfigForNoLogin {
}
```

アプリ側で本格的な認証を用意する準備ができたら、フォームログイン・OAuth2・ロール／権限ルールを含む完全な `SplibWebSecurityConfig` の使い方は[フォームログイン・アクセス制御](page?id=web/security/form-login&lang=ja)を参照してください。

## 3. 組み込み管理者用の資格情報を設定する

`ecuacion-splib-web` 自身が持つ管理画面（[組み込み管理者認証](page?id=web/security/builtin-admin&lang=ja)を参照）を使うには、次の2つのプロパティのうちどちらか一方を設定する必要があります。

```properties
jp.ecuacion.splib.web.builtin-admin-login.password-plain=change-me
```

## 4. 動作確認する

アプリケーションをローカルで起動し、組み込み管理者ログイン画面を開きます。

```
http://localhost:8080/ecuacion-splib/public/adminLogin/page
```

ユーザー名 `ecuacion-splib` と、手順3で設定したパスワードでログインします。ログインに成功すると `/ecuacion-splib/admin/config/page` に遷移します。このページでできることは[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)を参照してください。
