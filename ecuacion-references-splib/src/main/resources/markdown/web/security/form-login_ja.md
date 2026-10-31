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

### 未ログイン時の移動先

ログインが必要なページに未ログインでアクセスされたとき、デフォルトでは `getAccessDeniedPage()` にリダイレクトします。移動先を変えたい場合（例えばログイン画面に移したい場合）は、`getAuthenticationEntryPoint()` をオーバーライドして独自の `AuthenticationEntryPoint` を返します。元のリクエストは、entry point が呼ばれる前にSpring Securityの `RequestCache` に保存されるため、entry point を差し替えても、OAuth2ログイン後には元のページに戻ります。

```java
@Override
protected AuthenticationEntryPoint getAuthenticationEntryPoint() {
  return (request, response, authException) -> response
      .sendRedirect(request.getContextPath() + "/public/login/page");
}
```

### ソーシャルログイン（Google / Apple）

OAuth2ソーシャルログインを有効にするには、`SplibWebSecurityConfig` の代わりに `SplibOauth2WebSecurityConfig` を継承し、そのコンストラクタに `SplibOauth2AuthSuccessHandler`・`SplibAppleClientSecretService`（Appleを使わない場合は `null`）・`ClientRegistrationRepository` を渡します。あわせて `SplibOauth2UserHandler` のBeanを登録します。コンストラクタに渡すsplibのBean（`SplibOauth2AuthSuccessHandler`・`SplibAppleClientSecretService`）と `SplibOauth2RegistrationConfirmService` は `SplibOauth2WebSecurityConfig` が登録するため、`jp.ecuacion.splib.web.oauth2` パッケージをコンポーネントスキャンする必要はありません。`SplibAppleClientSecretService` は `ecuacion.oauth2.apple.team-id` を設定したときだけ登録されます。クライアント登録自体はSpring Security標準の `spring.security.oauth2.client.*` プロパティで設定します。`ecuacion-splib-web` はこの用途に独自のプロパティを追加していません。AppleのAuthorizationコールバックは `POST`（`response_mode=form_post`）であるため、`SplibOauth2WebSecurityConfig` はこのコールバックのためだけに `/login/oauth2/code/*` をCSRF対象から除外しています。それ以外のパスはCSRF保護がかかったままです（[CSRF・トランザクショントークン](page?id=web/security/csrf-and-transaction-token&lang=ja)を参照）。

```java
@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends SplibOauth2WebSecurityConfig {

  public AppSecurityConfig(SplibOauth2AuthSuccessHandler oauth2SuccessHandler,
      @Nullable SplibAppleClientSecretService appleClientSecretService,
      ClientRegistrationRepository clientRegistrationRepository) {
    super(oauth2SuccessHandler, appleClientSecretService, clientRegistrationRepository);
  }

  // getDefaultSuccessUrl() など、SplibWebSecurityConfig と同じメソッドを実装する
}
```

#### 新規登録の前に確認画面を挟む

`SplibOauth2UserHandler` の `findOrCreateUser` は、利用者が見つからなければその場で登録する前提です。登録する前に確認画面（利用規約への同意など）を挟みたい場合は、代わりに `SplibOauth2UserHandlerWithRegistrationConfirm` を実装します。

```java
@Component
public class Oauth2UserHandlerImpl implements SplibOauth2UserHandlerWithRegistrationConfirm {

  @Override
  public @Nullable UserDetails findUser(String email, String name, String provider) {
    // 見つからなければ null を返す（登録はしない）
  }

  @Override
  public String getRegistrationConfirmPage() {
    return "/public/registrationConfirm/page";
  }
}
```

`findUser` が `null` を返すと、利用者は**ログイン済みにならず**、プロバイダから受け取った情報（メールアドレス・名前・プロバイダ）が `SplibOauth2PendingRegistration` としてセッションにだけ保持された状態で、`getRegistrationConfirmPage()` にリダイレクトされます。Appleは名前を初回の認証のときにしか返しませんが、この名前も保持される情報に含まれます。この時点では未ログインなので、確認画面は `/public/**` 配下など、ログインなしで開けるURLにしてください。

確認画面とその後の処理では、`SplibOauth2RegistrationConfirmService` を使います。

| メソッド | 用途 |
| --- | --- |
| `getPendingRegistration(request)` | 保持されている情報を取得する。<br>セッションが切れた等で無い場合は `null`。 |
| `discardPendingRegistration(request)` | 保持されている情報を捨てる（「やり直す」が押されたときなど）。 |
| `loginAfterRegistration(request, response, userDetails)` | アプリが登録を終えた後に呼び、その利用者をログイン済みにする。<br>保持されていた情報は捨てられ、<br>通常のログインと同じく、セッションIDの付け替えとCSRFトークンの再発行が行われる。<br>戻り値はリダイレクト先のURL（ログイン前に覚えておいたURL、無ければ `getDefaultSuccessUrl()`）。 |

```java
@PostMapping("action")
public String register(HttpServletRequest request, HttpServletResponse response) {
  SplibOauth2PendingRegistration pending = service.getPendingRegistration(request);
  // pending が null なら、ログイン画面へ戻す

  UserDetails userDetails = // pending の情報で利用者を登録する
  return "redirect:" + service.loginAfterRegistration(request, response, userDetails);
}
```

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
- `POST /account/exitUser` はなりすましを終了して元のユーザーに戻り、`getExitingUserDonePagePath()` にリダイレクトします。

### リクエストごとに移る画面を決める

開始した画面によって移る画面を変えたい場合は、`HttpServletRequest` を引数に取る同名のメソッドをオーバーライドします。いずれもリクエストのたびに呼ばれ、既定の実装は引数なしのメソッドを呼びます。

| メソッド | 呼ばれるとき |
| --- | --- |
| `getSwitchingUserDonePagePath(request)` | なりすましの開始に成功したとき |
| `getSwitchingUserFailurePagePath(request)` | なりすましの開始に失敗したとき |
| `getExitingUserDonePagePath(request)` | なりすましを終了したとき |
| `onSwitchingUserDone(request, response, authentication)` | なりすましの開始に成功したとき（リダイレクトの直前）。<br>既定では何もしません。 |
| `onExitingUserDone(request, response, authentication)` | なりすましを終了したとき（リダイレクトの直前）。<br>既定では何もしません。 |

終了のリクエストには、どこから開始したかの情報がありません。そのため、開始時にセッションへ戻り先を覚えておき、終了時にそれを読みます（セッションは開始から終了まで引き継がれます）。`onXxxDone` はパスを決めるメソッドの後に呼ばれるので、パスを決めるのに使ったセッションの値はそこで消せます。

```java
@Override
protected String getSwitchingUserDonePagePath(HttpServletRequest request) {
  String id = request.getParameter("inquiryId");
  if (id == null || !id.matches("[0-9]+")) {
    return getSwitchingUserDonePagePath();
  }

  request.getSession().setAttribute("switchUserReturnPath", "/admin/inquiry/detail/page?id=" + id);
  return "/account/inquiry/detail/page?id=" + id;
}

@Override
protected String getExitingUserDonePagePath(HttpServletRequest request) {
  return request.getSession().getAttribute("switchUserReturnPath") instanceof String path ? path
      : getExitingUserDonePagePath();
}

@Override
protected void onExitingUserDone(HttpServletRequest request, HttpServletResponse response,
    Authentication authentication) {
  request.getSession().removeAttribute("switchUserReturnPath");
}
```

返すパスは、`/` で始まるアプリ内のパスでなければなりません。`//` や `/\` で始まるパスは外部のサイトへのURLになり得るため、`IllegalStateException` になります。リクエストのパラメータからパスを組み立てる場合は、上の例のように値を検証してください。

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
