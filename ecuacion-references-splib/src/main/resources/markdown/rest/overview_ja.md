`ecuacion-splib-rest` は REST API を構築するための ecuacion-splib モジュールです。
`ecuacion-splib-core` の上に構築されており、REST API に必要な要素 —
URL プレフィックスベースのセキュリティ規約、API キー認証の仕組み、共通の例外ハンドラー — を追加します。

## 提供する機能

- **エンドポイントプレフィックスによるセキュリティ規約** — すべてのエンドポイントは
  `/api/public/**`・`/api/key/**`・`/api/**` のいずれかのプレフィックス配下に置かれ、
  それぞれに固定のセキュリティポリシーが適用されます。
  [Public エンドポイント](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=ja) と
  [API キー認証](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=ja) を参照してください。
- **API キー認証** — マシン間通信向けのヘッダーベース認証（`X-Api-Key`）。
  キーの照合方法はプラガブルで、平文比較とハッシュ比較の 2 モードを選べます。
- **共通の例外処理** — 未捕捉の例外や専用の `HttpStatusException` を統一的に HTTP レスポンスへ変換します。
  [例外処理](/public/showMarkdown/page?id=rest/exception-handling&lang=ja) を参照してください。

## URL プレフィックスの規約

| プレフィックス | セキュリティポリシー | CSRF |
| --- | --- | --- |
| `/api/public/**` | 常に許可（`permitAll`） | 無効 |
| `/api/key/**` | 有効な `X-Api-Key` ヘッダーが必須 | 無効 |
| `/api/**`（それ以外） | 常に拒否（`denyAll`） | 対象外 |

これら 3 つのポリシーは、アプリケーション側が継承する抽象クラス `SplibRestSecurityConfig` によって設定されます。
[セットアップ](/public/showMarkdown/page?id=rest/setup&lang=ja) を参照してください。

## 依存関係

`ecuacion-splib-rest` は `ecuacion-splib-core` に依存し、`spring-boot-starter-web-services`
（JAX-WS を含まない Spring MVC 用スターター）と `spring-boot-starter-security` を取り込みます。
Tomcat 自体は提供しないため、WAR 化した `ecuacion-splib-web` アプリケーションと同様に、
アプリケーション側で `spring-boot-starter-tomcat` を `provided` スコープで追加してください。
