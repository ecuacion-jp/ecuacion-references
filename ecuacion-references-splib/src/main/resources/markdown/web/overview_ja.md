`ecuacion-splib-web` は、サーバーサイドレンダリング（Thymeleaf）方式のWebアプリケーションを構築するためのecuacion-splibモジュールです。`ecuacion-splib-core` の上に、フォームベースのWebアプリに必要な要素、セッションベースの認証とロール／権限による認可、CSRF・二重送信対策、共通の例外ハンドラ、いくつかの組み込み運用機能を追加します。

> **このセクションの対象範囲。** このメニューの **web** 配下のページは、`ecuacion-splib-web` の内部挙動とセキュリティ機構、認証・CSRF・例外処理など、のみを扱います。UI側（コントローラー・フォーム・Thymeleaf/Bootstrapコンポーネント）を実際に触りながら学べるコンテンツは、Markdown記事集ではなくフレームワーク自体で構築された `ecuacion-splib-web` 専用のチュートリアルアプリ（別サイト）を参照してください。

## 提供する機能

| 機能 | 説明 |
| --- | --- |
| フォームログイン・アクセス制御 | セッションベースのフォームログイン、OAuth2（Google/Apple）ソーシャルログイン、ロール／権限ベースの `authorizeHttpRequests` ルールを、`SplibWebSecurityConfig`（またはログイン不要・管理者向け・なりすましログイン向けの各バリエーション）を継承することで構成できます。<br>[フォームログイン・アクセス制御](page?id=web/security/form-login&lang=ja) を参照。 |
| 組み込み管理者認証 | `ecuacion-splib` 自身が提供する `/ecuacion-splib/admin/**` 配下のページ（後述の運用エンドポイントなど）は、<br>アプリ自体のログインとは独立した、固定資格情報による専用ログインで保護されています。<br>[組み込み管理者認証](page?id=web/security/builtin-admin&lang=ja) を参照。 |
| CSRF・二重送信対策 | Spring Securityのセッションベースの標準CSRF保護がデフォルトで有効になっており、<br>その上にワンタイムのトランザクショントークンを重ねることで、Submitボタンの二重クリックのようなフォームの二重送信を検出します。<br>[CSRF・トランザクショントークン](page?id=web/security/csrf-and-transaction-token&lang=ja) を参照。 |
| オープンリダイレクト対策 | 「元のページに戻る」形でリダイレクトする例外ハンドラは、リダイレクト先を `Referer` ヘッダーから取り出しますが、<br>ブラウザを外部サイトへ送ることが絶対にないよう検証してから使用します。<br>[オープンリダイレクト対策](page?id=web/security/open-redirect-protection&lang=ja) を参照。 |
| 共通の例外処理 | `ViolationException`、`ConstraintViolationException`、その他の未捕捉の例外は、それぞれ異なる扱いでページ表示またはリダイレクトに変換されます。<br>[例外処理](page?id=web/exception-handling&lang=ja) を参照。 |
| 組み込み運用エンドポイント | プロパティキャッシュをクリアするアクションと、意図的にシステムエラーを発生させるアクションがあり、<br>いずれも上記の組み込み管理者ログインの配下にあります。<br>[運用エンドポイント](page?id=web/operational-endpoints&lang=ja) を参照。 |
| セキュリティに配慮したロギング | すべてのリクエストがパラメータ付きでDEBUGログに出力されますが、パスワードらしきパラメータはマスクされ、<br>セッションIDは末尾8文字のみが記録されます。<br>[ロギング](page?id=web/logging&lang=ja) を参照。 |

## フェイルクローズなデフォルト動作

`ecuacion-splib-web` は `spring-boot-starter-security` に無条件で依存しているため、これを取り込むアプリケーションはクラスパス上にSpring Securityを持つことになり、起動には何らかの `SecurityFilterChain` が必要です。2つの自動設定は、各アプリに明示的な選択を強いる代わりに、安全な挙動をデフォルトにしています。

- `SplibWebSecurityConfig` を継承したBeanが1つも登録されていない場合、`SplibWebSecurityAutoConfiguration` がすべてのリクエストを拒否する（`denyAll`）フォールバックのフィルタチェーンを提供し、INFOレベルでログに記録します。これが置き換わる本来の設定については [フォームログイン・アクセス制御](page?id=web/security/form-login&lang=ja) を参照してください。
- `SplibExceptionHandler` のBeanが登録されていない場合、`SplibWebExceptionHandlerAutoConfiguration` が `ViolationException` のみを処理する最小限のハンドラを提供します。これが置き換わる完全なハンドラについては [例外処理](page?id=web/exception-handling&lang=ja) を参照してください。

どちらも、何も起きない形で黙って通過させるのではなく、安全側・制限側の挙動にフォールバックします。そのため、設定が中途半端なアプリケーションは、無防備なページを黙って配信するのではなく、すべてのページがアクセス拒否になるという分かりやすい形で失敗します。

## 依存関係

`ecuacion-splib-web` は `ecuacion-splib-core` と `ecuacion-splib-ui` に依存し、`spring-boot-starter-webmvc`、`spring-boot-starter-security`、`spring-boot-starter-oauth2-client`、`spring-boot-starter-thymeleaf`（layout-dialectとSpring Security用Thymeleaf拡張を含む）を取り込みます。Tomcat自体は提供しないため、アプリケーション側で `spring-boot-starter-tomcat` を `provided` スコープで追加してください。
