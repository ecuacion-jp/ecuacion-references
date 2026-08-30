`ecuacion-splib-web` は、組み込みのコントローラーを1つ、`ConfigController` を `/ecuacion-splib/admin/config` 配下に提供しており、[組み込み管理者認証](page?id=web/security/builtin-admin&lang=ja)で保護されています。2つのアクションはどちらも副作用を持つため、`permitAll` なパスではなく `/ecuacion-splib/admin/**` 配下に置かれています。

## `ConfigController`

```
GET  /ecuacion-splib/admin/config/page
POST /ecuacion-splib/admin/config/action?action=clearPropertiesCache
POST /ecuacion-splib/admin/config/action?action=systemError
```

### プロパティキャッシュのクリア

`PropertiesFileUtil` 経由で読み込んだプロパティファイルのキャッシュをクリアし、アプリを再起動せずに `application.properties` の変更を反映できるようにします。`spring-cloud-context` がクラスパス上にある場合は、Spring自身の `Environment`（`@Value`/`@ConfigurationProperties` を支えるキャッシュ）もあわせてリフレッシュします。仕組みの詳細、複数の `spring.config.name` を使う構成における既知の制限を含む、は **core** メニュー配下の[application.properties](page?id=core/config/application-properties&lang=ja)を参照してください。このアクションは、`ecuacion-splib-rest` 側の `POST /api/ecuacion-splib/key/clearPropertiesCache` に相当するWeb版で、両者は内部的に同じ `SplibPropertiesCacheClearer` を共有しています。

### 意図的なシステムエラーの発生

単純な `RuntimeException` を投げます。これにより、実際のバグを起こすことなく、システムエラー時の挙動（例外処理・ロギングなど。[例外処理](page?id=web/exception-handling&lang=ja)を参照）を確認できます。
