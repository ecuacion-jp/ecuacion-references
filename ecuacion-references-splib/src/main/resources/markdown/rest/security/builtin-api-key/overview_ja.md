`/api/ecuacion-splib/key/**` 配下にマッピングされたエンドポイントは、
[API キー認証](page?id=rest/security/api-key/overview&lang=ja) と同様に有効な `X-Api-Key`
ヘッダーが必須です。ただしこのプレフィックスは、`ecuacion-splib` 自身の組み込みエンドポイントのうち
副作用のあるもの専用に予約されています。現時点では
[Config エンドポイント](page?id=rest/config-endpoints&lang=ja)
（`ClearPropertiesCacheController`、`SystemErrorController`）がこれに該当します。
このフィルターチェーンは `@Order(10)` で実行され、`/api/key/**`（9）の後、
残りすべてを拒否する `/api/**` のルール（11）より前に評価されます。

## なぜキーセットを分けているか

`jp.ecuacion.splib.rest.builtin-api-key.*` は `jp.ecuacion.splib.rest.api-key.*` とは別の
プロパティの名前空間です。これにより、`ecuacion-splib` 自身の運用系エンドポイントを守るキーを、
アプリケーションが `/api/key/**` で独自に使っているキーとは独立して発行・ローテーション
できるようにしています。

## リクエストヘッダー

`/api/key/**` と同一です：`X-Api-Key`（必須）と `X-Api-Key-Id`（任意。ここでは複数クライアント向けの
キーではなく固定の単一キーなので、認証後のプリンシパル名としてログ用途に引き継がれるだけです）。
詳しくは [API キー認証](page?id=rest/security/api-key/overview&lang=ja) を参照してください。

## キーの設定

`/api/key/**` と異なり、ここではアプリケーション実装の Provider Bean は不要です。期待値は
`application.properties` から直接読み込まれます。次のどちらか一方だけを設定してください。

```properties
jp.ecuacion.splib.rest.builtin-api-key.password-plain=your-api-key-here
# または
jp.ecuacion.splib.rest.builtin-api-key.password-bcrypt=$2a$10$...
```

`password-bcrypt` にはキーの bcrypt ハッシュを設定します。これにより生の値は
`application.properties` に平文のままでは残りません。どちらも
`jp.ecuacion.splib.core.util.SplibHashedPropertyResolver` を通じてリクエストのたびに都度読み直される
ため、`PropertiesFileUtil` のキャッシュをクリアすれば（[Config エンドポイント](page?id=rest/config-endpoints&lang=ja)
参照）再起動なしで変更が反映されます。

- **どちらも未設定の場合：** `/api/ecuacion-splib/key/**` へのリクエストはすべて拒否されます。
  これらの組み込みエンドポイントを使わないアプリケーションにとって安全なデフォルトです。
- **どちらか一方のみ設定されている場合：** 送られてきた `X-Api-Key` を、設定に応じて
  平文または bcrypt で比較します。
- **両方設定されている場合：** これは `application.properties` を管理する側にしか起こりえない
  設定ミスなので（外部からの呼び出しでは発生し得ない）、下記の通り別扱いで報告されます。

## 拒否時の挙動・認証成功時

ヘッダー欠落や誤った（あるいは存在しない）キーの場合は、`/api/key/**` と同様に汎用的な `401` を
返します（比較には `MessageDigest.isEqual` による定数時間比較を使用）。「該当キーなし」と
「キー相違」を呼び出し元が区別できないようにするためです。一方、
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` と `...password-bcrypt` の**両方**が
設定されている場合はこれとは異なり、該当する2つのプロパティキー名を明示した `500` を返します。
この状態は `application.properties` を管理する側にしか起こりえないためです。

認証に成功すると `ROLE_BUILTIN_API_KEY` 権限で認証されます（`/api/key/**` では `ROLE_API_KEY`）。

## CSRF について

`/api/key/**` と同じ理由で無効化されています。`X-Api-Key` はブラウザが自動付与するアンビエントな
資格情報ではないためです。詳しくは [概要](page?id=rest/overview&lang=ja) を参照してください。
