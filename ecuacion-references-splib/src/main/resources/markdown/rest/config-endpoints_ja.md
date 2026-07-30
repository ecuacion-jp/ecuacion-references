`ecuacion-splib-rest` は、運用テスト用の組み込みコントローラーを 2 つ提供しています。
どちらも `/api/ecuacion-splib/key/**` 配下にマッピングされています。

```
POST /api/ecuacion-splib/key/clearPropertiesCache
POST /api/ecuacion-splib/key/systemError
```

- `ClearPropertiesCacheController` は `PropertiesFileUtil` が読み込むプロパティファイルの
  キャッシュをクリアします。これにより、アプリを再起動せずに `application.properties` の変更を
  反映できます。
- `SystemErrorController` は意図的に `RuntimeException` をスローします。これにより、
  実際のバグを起こすことなくシステムエラー時の挙動（例外処理・ログ出力など）を
  テストできます。

[Alive Check エンドポイント](page?id=rest/alive-check-endpoint&lang=ja) とは異なり、この 2 つは
副作用を伴うため `/api/ecuacion-splib/public/**` には置かれていません。代わりに有効な
`X-Api-Key` ヘッダーが必須で、[API キー認証](page?id=rest/security/api-key/overview&lang=ja)
と同じ仕組みで認証されますが、照合に使うキーセットは独立して登録・ローテーションされます。詳しくは
[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja) を
参照してください。アプリケーション側で `application.properties` に
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` または `...password-bcrypt` を
設定するまでは、この 2 つを含め `/api/ecuacion-splib/key/**` へのリクエストはすべて拒否されます。
