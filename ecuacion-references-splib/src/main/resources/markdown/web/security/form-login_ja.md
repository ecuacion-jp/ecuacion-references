`SplibWebSecurityConfig` とそのバリエーションは、`HttpSecurity` の設定、フォームログイン・ログアウト・ロール/権限ベースの `authorizeHttpRequests` ルール、を手書きせずに済ませるために、アプリケーション側で継承する抽象クラスです。

## 4つのバリエーション

| クラス | 用途 | ログインURLのプレフィックス |
| --- | --- | --- |
| `SplibWebSecurityConfig` | アプリ自身のエンドユーザー向けログイン（フォームログイン、任意でOAuth2ソーシャルログイン） | `/public/**` |
| `SplibWebSecurityConfigForNoLogin` | 上記と同じだが、ログインを一切持たないアプリ向け（`isLoginEnabled()` が `false` に固定される） | `/public/**` |
| `SplibWebSecurityConfigForAdmin` | 上記のエンドユーザーログインとは独立した、アプリ自身の管理者用ログイン | `/public/admin*/**`、`/admin/**` |
| `SplibWebSecurityConfigForSwitchUser` | 上記のいずれかを使っている、ログイン済みの管理者/サポート担当者によるなりすまし（switch user） | （`/admin/switchUser`、`/account/exitUser` を追加） |

これら4つは互いに独立しています。アプリは通常、最初の2つのいずれか一方を継承し、必要に応じて独立した管理者ログインやなりすましログインが必要であれば `SplibWebSecurityConfigForAdmin` や `SplibWebSecurityConfigForSwitchUser` をさらに追加します。いずれも自動的には登録されません。それぞれ単なる `abstract class` であり、アプリ側でサブクラス化して `@Configuration`（Spring Securityを最初に有効化するクラスには `@EnableWebSecurity` も）を付与します。

## `SplibWebSecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfig {

  public AppSecurityConfig() {
    super(null, null, null); // OAuth2を使う場合はここにBeanを渡す。「ソーシャルログイン」節を参照
  }

  @Override
  protected String getDefaultSuccessUrl() {
    return "/home/page";
  }

  @Override
  protected String getLoginNeededPage() {
    return "/public/login/page";
  }

  @Override
  protected String getAccessDeniedPage() {
    return "/public/login/page?accessDenied";
  }

  @Override
  protected List<AuthorizationBean> getRoleInfo() {
    return List.of(new AuthorizationBean("/admin/**", "ADMIN"));
  }

  @Override
  protected List<AuthorizationBean> getAuthorityInfo() {
    return List.of();
  }
}
```

フォームログインは `POST /public/login/action`（ユーザー名/パスワードのパラメータ名は `login.username`/`login.password`）に、ログアウトは `POST /public/logout` に紐づけられます。`/public/**`、`/ecuacion-splib/public/**`、静的リソースは常に `permitAll` で、それ以外のパスは `getRoleInfo()`/`getAuthorityInfo()` が返す `AuthorizationBean` のいずれかに一致しない限り `denyAll` になります。

### 予約されたロール `ACCOUNT_FULL_ACCESS`

`getRoleInfo()` が返す内容に加えて、`/account/**` には予約ロール `ACCOUNT_FULL_ACCESS` が自動的に付与されます。グループ管理者やパワーユーザーのロールに、サブパスを一つひとつ列挙することなくこの領域への全アクセス権を与えたい場合に便利です。

### ソーシャルログイン（Google / Apple）

OAuth2ソーシャルログインを有効にするには、`SplibOauth2UserHandler` のBeanを登録し、上記コンストラクタに `SplibOauth2AuthSuccessHandler`（Appleの場合はさらに `SplibAppleClientSecretService`）を渡します。クライアント登録自体はSpring Security標準の `spring.security.oauth2.client.*` プロパティで設定します。`ecuacion-splib-web` はこの用途に独自のプロパティを追加していません。AppleのAuthorizationコールバックは `POST`（`response_mode=form_post`）であるため、`SplibWebSecurityConfig` はこのコールバックのためだけに `/login/oauth2/code/*` をCSRF対象から除外しています。それ以外のパスはCSRF保護がかかったままです（[CSRF・トランザクショントークン](page?id=web/security/csrf-and-transaction-token&lang=ja)を参照）。

## `SplibWebSecurityConfigForNoLogin`

ログインを一切持たないアプリケーション（このリファレンスサイト自体もこれに該当します）では、代わりにこちらを継承します。`getDefaultSuccessUrl()` と `getLoginNeededPage()` は `final` で固定されており（呼び出されることはありません）、実装が必要なのは `getAccessDeniedPage()` のみです（オーバーライドしない場合、`jp.ecuacion.splib.web.home-page` プロパティの値がデフォルトで使われます）。`getRoleInfo()`/`getAuthorityInfo()` はデフォルトで `null`（ロールベースのアクセス制御なし）ですが、ログイン不要のアプリでもロール/権限で制御したいページがあればオーバーライドできます。

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibWebSecurityConfigForNoLogin {
}
```

## `SplibWebSecurityConfigForAdmin`

（前述の `ecuacion-splib` 組み込み管理者ログインとは別に、[組み込み管理者認証](page?id=web/security/builtin-admin&lang=ja)を参照）アプリ自身の管理画面向けに、独立した2つ目のログインが必要な場合に使います。`@Order(21)` で独自の `SecurityFilterChain` を登録し（`/public/admin*/**` と `/admin/**` のみにマッチ）、フォームログインは `POST /public/adminLogin/action`（`adminLogin.username`/`adminLogin.password`）、ログアウトは `POST /public/adminLogout` に紐づきます。前述の `ACCOUNT_FULL_ACCESS` と同様、`/admin/**` には予約ロール `ADMIN_FULL_ACCESS` が自動的に付与されます。

## `SplibWebSecurityConfigForSwitchUser`

なりすまし（impersonation）のためにSpring Securityの `SwitchUserFilter` を追加します。例えば、サポート担当の管理者が問題を再現するために特定のエンドユーザーとしてログインする、といった用途です。両方のアクションは意図的に `POST` 限定になっています。これによりCSRF保護がかかり、対象のユーザー名がURL・アクセスログ・`Referer` ヘッダーに残らないようにしています。

```java
@Configuration
public class AppSwitchUserConfig extends SplibWebSecurityConfigForSwitchUser {

  public AppSwitchUserConfig(UserDetailsService userDetailsService) {
    super(userDetailsService);
  }

  @Override
  protected String getSwitchingUserDonePagePath() {
    return "/account/dashboard/page";
  }

  @Override
  protected String getSwitchingUserFailurePagePath() {
    return "/admin/dashboard/page";
  }

  @Override
  protected String getExitingUserDonePagePath() {
    return "/admin/dashboard/page";
  }
}
```

- `POST /admin/switchUser`（パラメータ `switchUser.username`）は指定したユーザーへのなりすましを開始し、成功時は `getSwitchingUserDonePagePath()`、失敗時は `getSwitchingUserFailurePagePath()` にリダイレクトします。
- `POST /account/exitUser` はなりすましを終了して元のユーザーに戻り、結果によらず `getExitingUserDonePagePath()` にリダイレクトします。

## Secure Cookie属性とTLS終端リバースプロキシ

`ecuacion-splib-web` は `server.servlet.session.cookie.secure=true` の設定も、`ForwardedHeaderFilter`
の登録（あるいは `server.forward-headers-strategy` の有効化）も、自身では一切行いません。

TLS終端をリバースプロキシ（nginx、ALBなど）側で行い、アプリケーション自体はそのプロキシからの
平文HTTPしか受け取らない、という一般的な構成では、両方とも未設定のままだと次の2点が起きます。

- セッションCookieが `Secure` 属性なしで発行されるため、平文経路（SSLストリッピング、混在コンテンツ、
  クライアント側の設定ミスなど）に到達してしまった場合にそのまま送信されるのを防げません。
- `HttpServletRequest#isSecure()` やリダイレクトURLの生成は、（HTTPSだった）本来のクライアント〜
  プロキシ間の接続ではなく、平文HTTPであるプロキシ〜アプリケーション間の接続を見てしまいます。

このようなプロキシ配下にアプリケーションをデプロイする場合は、以下の2点を自分で設定してください。

```properties
server.servlet.session.cookie.secure=true
server.forward-headers-strategy=framework
```

リバースプロキシ側でも `X-Forwarded-Proto` ヘッダー（またはそれに相当するもの）を設定するよう
構成する必要があります。`server.forward-headers-strategy` は、このヘッダーが実際に存在し
信頼される場合にのみ効果を持ちます。
