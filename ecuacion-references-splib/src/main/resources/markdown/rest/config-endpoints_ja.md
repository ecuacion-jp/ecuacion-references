`ecuacion-splib-rest` は、運用テスト用の組み込みコントローラーを 2 つ提供しています。
どちらも `/api/ecuacion/public/**` 配下にマッピングされています。

```
POST /api/ecuacion/public/clearPropertiesCache
POST /api/ecuacion/public/systemError
```

- `ClearPropertiesCacheController` は `PropertiesFileUtil` が読み込むプロパティファイルの
  キャッシュをクリアします。これにより、アプリを再起動せずに `application.properties` の変更を
  反映できます。
- `SystemErrorController` は意図的に `RuntimeException` をスローします。これにより、
  実際のバグを起こすことなくシステムエラー時の挙動（例外処理・ログ出力など）を
  テストできます。

[Alive Check エンドポイント](page?id=rest/alive-check-endpoint&lang=ja) とは異なり、この 2 つは
副作用を伴うため、`jp.ecuacion.splib.rest.ecuacion-config-endpoints.enabled` を
application.properties で明示的に `true` に設定しない限り、HTTP `403` で拒否されます。
本番環境では未設定（または `false`）のままにし、これらの操作を公開しても問題ない環境でのみ
有効にしてください。
