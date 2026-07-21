`/api/public/**` 配下にマッピングされたエンドポイントは常に到達可能です（`permitAll`、認証不要）。
このフィルターチェーンは `SplibRestSecurityConfig` が登録する 3 つのチェーンのうち最初、
`@Order(8)` で実行されます。

## CSRF が無効化されている理由

CSRF 対策が意味を持つのは、偽装されたクロスサイトリクエストが被害者に代わって状態を変更できてしまう場合です。
データを読み取るだけのリクエストには CSRF が守るべきものが存在しないため、このプレフィックスでは CSRF が無効化されています。

**この理屈は、`/api/public/**` 配下のすべてのエンドポイントが副作用を持たないことを前提としています。**
`ecuacion-splib-rest` はこれを強制する仕組みを持っていません。あくまでフレームワークが前提とするだけの規約であり、
自動的には検証されません。このプレフィックス配下に書き込みを行う `@PostMapping` を追加することも技術的には可能で、
その場合 `/api/public/**` は `permitAll`（ログイン中セッションから除外されているわけではない）であるため、
悪意あるサイトがログイン中ユーザーのブラウザ経由で CSRF トークンなしにその書き込みをトリガーできてしまいます。

**`/api/public/**` は読み取り専用（GET/HEAD のみ）に保ってください。** 書き込みが必要なエンドポイントは
[API キー認証](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=ja) 配下の `/api/key/**` に置くか、
[独自エンドポイントのセキュリティ](/public/showMarkdown/page?id=rest/security/custom-endpoints&lang=ja) で
説明する独自のセキュリティ設定の配下に置いてください。

## 例

組み込みの
[Config エンドポイント](/public/showMarkdown/page?id=rest/config-endpoint&lang=ja)
（`GET /api/public/ecuacion/config`）は、このプレフィックス配下にある読み取り専用エンドポイントの例です。
