`ecuacion-splib-web` は自身の管理画面（現時点では[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)）を持っており、`SplibBuiltinAdminSecurityConfig` という、アプリ自身が使っている `UserDetailsService` やログインとは独立した、具象クラスかつ自動登録されるセキュリティ設定によって保護されています。

抽象クラスでありサブクラス化が必要な `SplibWebSecurityConfigForAdmin`（[フォームログイン・アクセス制御](page?id=web/security/form-login&lang=ja)を参照）とは異なり、このクラスはアプリ側のコードを一切必要とせず、下記の資格情報プロパティを設定するだけで使えます。

## URLプレフィックス

`SplibBuiltinAdminSecurityConfig` は `@Order(22)` で独自の `SecurityFilterChain` を登録し、`/ecuacion-splib/public/adminLogin/**`、`/ecuacion-splib/admin/**`、`/ecuacion-splib/adminLogout` のみにマッチします。`/ecuacion-splib/public/adminLogin/**`（ログインページ自体）は `permitAll` で、`/ecuacion-splib/admin/**` 配下のそれ以外はすべて認証が必要です。

## 資格情報プロパティ

`application.properties` に、以下のどちらか一方を設定します。

| プロパティ | 意味 |
| --- | --- |
| `jp.ecuacion.splib.web.builtin-admin-login.password-plain` | パスワードそのもの（平文）。 |
| `jp.ecuacion.splib.web.builtin-admin-login.password-bcrypt` | パスワードのbcryptハッシュ。ユーザーの保存済みパスワードと同じ方法（例：`new BCryptPasswordEncoder().encode(rawPassword)`）で生成します。 |

固定ユーザー名は常に `ecuacion-splib`（`SplibBuiltinAdminSecurityConfig.BUILTIN_ADMIN_USERNAME`）です。両プロパティとも、起動時にキャッシュされるのではなく、ログイン試行のたびに再解決されます。そのため、[運用エンドポイント](page?id=web/operational-endpoints&lang=ja)のキャッシュクリアアクションで値を変更した場合、再起動なしで即座に反映されます。

## フェイルクローズ（安全側に倒す）設計

どちらのプロパティも設定されていない場合、ログインは一切できません。これはパスワードが間違っている場合と区別がつかず、この機能を使わないアプリケーションにとっての安全なデフォルト動作です。逆に**両方**設定されている場合は、`application.properties` を制御できる人しか起こしえない設定ミスなので、区別して表示されます。ログインページには、単純なパスワード入力ミスと誤解されないよう、通常の資格情報エラー（`?error`）とは別の「資格情報の設定ミス」専用メッセージ（`?credentialMisconfigured`）が表示されます。

これは、`ecuacion-splib-rest` 側の同等の組み込みAPIキー層（`/api/ecuacion-splib/key/**`）が、自身の資格情報プロパティがどちらも設定されていない場合にすべてのリクエストを拒否するのと同じ考え方です。

## ログイン・ログアウト

| アクション | URL |
| --- | --- |
| ログインページ | `GET /ecuacion-splib/public/adminLogin/page` |
| ログイン送信 | `POST /ecuacion-splib/public/adminLogin/action`（`builtinAdminLogin.username`/`builtinAdminLogin.password`） |
| ログアウト | `POST /ecuacion-splib/adminLogout` |

ログインに成功すると `/ecuacion-splib/admin/config/page` に遷移し、`ROLE_BUILTIN_ADMIN` 権限が付与されます。これは `ecuacion-splib-web` 自身のコントローラーが内部的に使うだけのもので、アプリ側で確認することは想定されていません。
