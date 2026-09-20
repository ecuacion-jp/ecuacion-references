`/api/ecuacion-splib/public/**` プレフィックス配下で公開されています。これは `ecuacion-splib` 自身の組み込みエンドポイント用に予約されたパスで、アプリケーション側の
[Public エンドポイント](page?id=rest/security/public-endpoints&lang=ja)（`/api/public/**`）とは区別されていますが、`/api/public/**` と同様に認証なしで到達可能です。

`ecuacion-splib-rest` は、このプレフィックス配下に組み込みコントローラーを 1 つ提供しています。

## AliveCheckController

```
GET  /api/ecuacion-splib/public/aliveCheck
POST /api/ecuacion-splib/public/aliveCheck
```

`GET`・`POST` の両方を受け付けます（`HEAD` は Spring MVC が `GET` に付随して自動的に処理します）。
これにより、監視ツールやアップタイムチェックがメソッド制限で弾かれることがないようにしています。

リクエストパラメータは定義されていません。レスポンスは以下の小さな JSON ボディとともに HTTP `200` を返します。

```json
{"status": "OK"}
```

アプリケーション固有のエンドポイントとは独立に、アプリケーションが起動していることを手軽に確認できるエンドポイントとして利用できます。
