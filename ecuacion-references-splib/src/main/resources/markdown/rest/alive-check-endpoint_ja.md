`ecuacion-splib-rest` は、`/api/ecuacion/public/aliveCheck` にマッピングされた組み込みコントローラー
`AliveCheckController` を 1 つ提供しています。

```
GET /api/ecuacion/public/aliveCheck
```

`/api/ecuacion/public/**` プレフィックス配下にあります。これは `ecuacion-splib` 自身の組み込み
エンドポイント用に予約されたパスで、アプリケーション側の
[Public エンドポイント](page?id=rest/security/public-endpoints&lang=ja)（`/api/public/**`）
とは区別されていますが、`/api/public/**` と同様に認証なしで到達可能です。
リクエストパラメータもレスポンスボディも定義されておらず、空のボディで HTTP `200` を返すだけです。
アプリケーション固有のエンドポイントとは独立に、アプリケーションが起動していること、
`/api/ecuacion/public/**` のフィルターチェーンが正しく組まれていることを手軽に確認できるエンドポイントとして利用できます。
