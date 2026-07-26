`/api/public/**` 配下にマッピングされたエンドポイントは常に到達可能です（`permitAll`、認証不要）。
このフィルターチェーンは `SplibRestSecurityConfig` が登録する 3 つのチェーンのうち最初、
`@Order(8)` で実行されます。

## `/api/public/**` は公開して問題ない範囲に留める

`/api/public/**` は認証なしで到達可能（`permitAll`）です。誰でもどこからでも直接呼び出せるため、
**公開しても問題のない情報・操作のみ**をこの配下に置いてください。

書き込みを伴うエンドポイント（副作用のあるもの）が「公開しても問題ない」となるケースはほぼないため、
実質的にはこのプレフィックス配下は読み取り専用（GET/HEAD のみ）になります。`ecuacion-splib-rest`
はこれを強制する仕組みを持たないため、書き込みが必要なエンドポイントは、
[Key エンドポイント](page?id=rest/security/api-key/overview&lang=ja) 配下の
`/api/key/**` に置くか、
[独自エンドポイントのセキュリティ](page?id=rest/security/custom-endpoints&lang=ja) で
説明する独自のセキュリティ設定の配下に置いてください。

## `/api/ecuacion/public/**` は `ecuacion-splib` 自身のエンドポイント用に予約されています

同じフィルターチェーンは `/api/ecuacion/public/**` も許可しており、ポリシーは `permitAll` で同一です。
このプレフィックスは `ecuacion-splib` 自身が提供するエンドポイント用に予約されています。組み込みの
[Config エンドポイント](page?id=rest/config-endpoint&lang=ja)（`GET /api/ecuacion/public/config`）が
その例で、これにより `/api/public/**` はアプリケーション独自の名前空間として保たれます。
