`ecuacion-splib-web` は自身の管理画面（現時点では[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)）を持っており、`SplibBuiltinAdminSecurityConfig` という、アプリ自身が使っている `UserDetailsService` やログインとは独立した、具象クラスかつ自動登録されるセキュリティ設定によって保護されています。

抽象クラスでありサブクラス化が必要な `SplibWebSecurityConfigForAdmin`（[フォームログイン・アクセス制御](page?id=web/security/form-login&lang=ja)を参照）とは異なり、このクラスはアプリ側のコードを一切必要とせず、下記の資格情報プロパティを設定するだけで使えます。

## HTTP Basic 認証

`SplibBuiltinAdminSecurityConfig` は `@Order(22)` で独自の `SecurityFilterChain` を登録し、`/ecuacion-splib/admin/**` のみにマッチして、HTTP Basic 認証で保護します。ログイン画面はありません。`/ecuacion-splib/admin/**` 配下のページを開くと、ブラウザ自身の認証ダイアログが表示されます。固定ユーザー名 `ecuacion-splib`（`SplibBuiltinAdminSecurityConfig.BUILTIN_ADMIN_USERNAME`）と、下記で設定したパスワードを入力します。

認証されたリクエストには `ROLE_BUILTIN_ADMIN` 権限が付与され、このチェーンはマッチするすべてのパスでこの権限を要求します。これは `ecuacion-splib-web` 自身が内部的に使うだけのもので、アプリ側で確認することは想定されていません。

## アプリ自身のログインとは無関係

認証はHTTPセッションから読み込まれることも、HTTPセッションに保存されることもないため、アプリ自身のログインとは互いに影響しません。

- アプリにログイン中のユーザーも、それだけでは入れず、未ログインのユーザーと同じく組み込み管理者の資格情報を求められます。
- ここで認証しても、アプリ自身のログインが置き換わったりログアウトされたりすることはなく、アプリ自身のページから組み込み管理者がログイン中のユーザーとして見えることもありません。
- アプリ自身のユーザーの資格情報（アプリの `UserDetailsService`）がここで試されることはありません。
- ログインのないアプリ（全ページが公開）でも同じように使えます。

ブラウザは閉じるまで資格情報を送り続けるため、ログアウトはありません。終了するにはブラウザを閉じてください。同じ理由で、CSRF対策は有効なままです。

## 資格情報プロパティ

`application.properties` に、以下のどちらか一方を設定します。

| プロパティ | 意味 |
| --- | --- |
| `jp.ecuacion.splib.web.builtin-admin-login.password-plain` | パスワードそのもの（平文）。 |
| `jp.ecuacion.splib.web.builtin-admin-login.password-bcrypt` | パスワードのbcryptハッシュ。<br>ユーザーの保存済みパスワードと同じ方法（例：`new BCryptPasswordEncoder().encode(rawPassword)`）で生成します。 |

両プロパティとも、起動時にキャッシュされるのではなく、リクエストのたびに再解決されます。そのため、[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)のキャッシュクリアアクションで値を変更した場合、再起動なしで即座に反映されます。

資格情報はリクエストのたびに送られるため、アプリケーションはHTTPSで提供してください。

## フェイルクローズ（安全側に倒す）設計

どちらのプロパティも設定されていない場合、認証は一切できません。これはパスワードが間違っている場合（`401`）と区別がつかず、この機能を使わないアプリケーションにとっての安全なデフォルト動作です。逆に**両方**設定されている場合は、`application.properties` を制御できる人しか起こしえない設定ミスなので、区別して扱われます。単純なパスワード入力ミスと誤解されないよう、リクエストには `500` が返り、両方のプロパティ名を含むエラーがログに出力されます。

これは、`ecuacion-splib-rest` 側の同等の組み込みAPIキー層（`/api/ecuacion-splib/key/**`）が、自身の資格情報プロパティがどちらも設定されていない場合にすべてのリクエストを拒否し、両方設定されている場合に `500` を返すのと同じ考え方です。
