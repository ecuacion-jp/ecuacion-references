`ecuacion-splib-rest` は REST API を構築するための ecuacion-splib モジュールです。
`ecuacion-splib-core` の上に構築されており、REST API に必要な要素 —
URL プレフィックスベースのセキュリティ規約、API キー認証の仕組み、共通の例外ハンドラー — を追加します。

## 提供する機能

- **エンドポイントプレフィックスによるセキュリティ規約** — すべてのエンドポイントは URL
  プレフィックスに応じて 4 つのセキュリティポリシーのいずれかに属します。詳細は下記の「URL プレフィックスの規約」表を参照してください。
  [Public エンドポイント](page?id=rest/security/public-endpoints&lang=ja)、
  [組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja)、
  [API キー認証](page?id=rest/security/api-key/overview&lang=ja) を参照してください。
- **API キー認証** — マシン間通信向けのヘッダーベース認証（`X-Api-Key`）。
  キーの照合方法はプラガブルで、平文比較とハッシュ比較の 2 モードを選べます。
  `ecuacion-splib` 自身の組み込みエンドポイントのうち副作用のあるものは、
  アプリケーション側の `/api/key/**` のキーとは別に、独立して登録されたキーセットを使用します。
- **共通の例外処理** — `ViolationException`・`ResponseStatusException`・それ以外の未捕捉の例外をそれぞれ異なる扱いで HTTP レスポンスへ変換します。
  [例外処理](page?id=rest/exception-handling&lang=ja) を参照してください。

## URL プレフィックスの規約

| プレフィックス | セキュリティポリシー |
| --- | --- |
| `/api/public/**` | 常に許可（`permitAll`） |
| `/api/key/**` | 有効な `X-Api-Key` ヘッダーが必須 — アプリケーション自身のキー |
| `/api/ecuacion-splib/public/**` | （ecuacion-splibにて使用）常に許可（`permitAll`）— `ecuacion-splib` 自身の組み込みエンドポイントのうち認証なしで公開しても安全なもの用に予約 |
| `/api/ecuacion-splib/key/**` | （ecuacion-splibにて使用）有効な `X-Api-Key` ヘッダーが必須 — `ecuacion-splib` 自身の組み込みエンドポイントのうち副作用のあるもの用 |
| `/api/**`（それ以外） | 常に拒否（`denyAll`） |

これら 4 つのポリシーは、アプリケーション側が継承する抽象クラス `SplibRestSecurityConfig` によって設定されます。
[クイックスタート](page?id=rest/quickstart&lang=ja) を参照してください。

## CSRF について

いずれのパスも CSRF 対策は無効化されています。CSRF 対策は、Cookie/セッションなどブラウザが自動付与する資格情報に紐づいた認可がある場合にのみ意味を持ちますが、`/api/public/**` は無認証、`/api/key/**` は
`X-Api-Key` ヘッダーによる非 ambient な（ブラウザが勝手に付与しない）認証であり、いずれもそうした前提を持ちません。

## 依存関係

`ecuacion-splib-rest` は `ecuacion-splib-core` に依存し、`spring-boot-starter-web-services`
（JAX-WS を含まない Spring MVC 用スターター）と `spring-boot-starter-security` を取り込みます。
Tomcat 自体は提供しないため、アプリケーション側で `spring-boot-starter-tomcat` を `provided` スコープで追加してください。
