`ecuacion-splib-rest` は、運用テスト用の組み込みコントローラーを 2 つ提供しています。
どちらも `/api/ecuacion-splib/key/**` 配下にマッピングされています。

```
POST /api/ecuacion-splib/key/clearPropertiesCache
POST /api/ecuacion-splib/key/systemError
```

- `ClearPropertiesCacheController` は `PropertiesFileUtil` が読み込むプロパティファイルのキャッシュをクリアします。これにより、アプリを再起動せずに `application.properties` の変更を反映できます。加えて、`spring-cloud-context` がクラスパス上にある場合は
  `ContextRefresher` も呼び出し、Spring 自身が保持する `Environment`（`@Value` /
  `@ConfigurationProperties` などが参照するプロパティキャッシュ）のリフレッシュも試みます。
  `spring-cloud-context` が無い場合はこの部分は無視され、その旨が INFO ログに出力されます。

  **既知の制限（`spring-cloud-context` 5.0.1 時点）**：この `ContextRefresher` によるリフレッシュは、
  `application.properties`自体（`spring.config.name`の1番目＝プライマリ）の変更しか確実には反映しません。Spring Bootのexecutable WAR（`java -jar xxx.war`）・外部Tomcatへの通常デプロイ・フラットなクラスパスでの起動のいずれでも同じように動作することを確認済みなので、クラスローダーやパッケージング形式の問題ではありません。一方、`spring.config.name`に複数の名前を指定した場合（例：`spring.config.name=application,my-app`）の**2番目以降**のファイル（例：`my-app.properties`）への変更は、デプロイ形態によらず一貫して`ContextRefresher`に再読込されません。これは
  `ContextRefresher`が再読込したプロパティソースを実行中の`Environment`にマージし直す処理が、
  プライマリ以外の設定名に対してはうまく機能していないためと考えられ、`ecuacion-splib`側では回避できません。
- `SystemErrorController` は意図的に `RuntimeException` をスローします。これにより、
  実際のバグを起こすことなくシステムエラー時の挙動（例外処理・ログ出力など）をテストできます。

[Alive Check エンドポイント](page?id=rest/alive-check-endpoint&lang=ja) とは異なり、この 2 つは副作用を伴うため `/api/ecuacion-splib/public/**` には置かれていません。代わりに有効な
`X-Api-Key` ヘッダーが必須で、[API キー認証](page?id=rest/security/api-key/overview&lang=ja)
と同じ仕組みで認証されますが、照合に使うキーセットは独立して登録・ローテーションされます。詳しくは
[組み込み Key エンドポイント](page?id=rest/security/builtin-api-key/overview&lang=ja) を参照してください。アプリケーション側で `application.properties` に
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` または `...password-bcrypt` を設定するまでは、この 2 つを含め `/api/ecuacion-splib/key/**` へのリクエストはすべて拒否されます。
