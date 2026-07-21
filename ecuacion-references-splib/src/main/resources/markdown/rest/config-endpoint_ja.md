`ecuacion-splib-rest` は、`/api/public/ecuacion/config` にマッピングされた組み込みコントローラー
`ConfigController` を 1 つ提供しています。

```
GET /api/public/ecuacion/config
```

[Public エンドポイント](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=ja) で説明した
`/api/public/**` プレフィックス配下にあるため、認証なしで到達可能です。現在のバージョンでは
リクエストパラメータもレスポンスボディも定義されておらず、空のボディで HTTP `200` を返すだけです。
アプリケーション固有のエンドポイントとは独立に、アプリケーションが起動していること、
`/api/public/**` のフィルターチェーンが正しく組まれていることを手軽に確認できるエンドポイントとして利用できます。
