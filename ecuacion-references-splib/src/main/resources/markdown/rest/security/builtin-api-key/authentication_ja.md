[概要](page?id=rest/security/builtin-api-key/overview&lang=ja) で説明した `/api/ecuacion-splib/key/**`
の認証は、`application.properties` に設定した1つのキーによって決まります。

## なぜキーセットを分けているか

`jp.ecuacion.splib.rest.builtin-api-key.*` は `jp.ecuacion.splib.rest.api-key.*` とは別のプロパティの名前空間です。これにより、`ecuacion-splib` 自身の運用系エンドポイントを守るキーを、
アプリケーションが `/api/key/**` で独自に使っているキーとは独立して発行・ローテーションできるようにしています。

## リクエストヘッダー

`/api/key/**` と同一です：`X-Api-Key`（必須）と `X-Api-Key-Id`（任意。ここでは複数クライアント向けのキーではなく固定の単一キーなので、認証後のプリンシパル名としてログ用途に引き継がれるだけです）。
詳しくは [API キー認証](page?id=rest/security/api-key/overview&lang=ja) を参照してください。

## 比較モード

`password-plain`・`password-bcrypt` のどちらを設定するかで、平文比較か bcrypt 比較かが決まります。
それぞれの違いや bcrypt ハッシュの生成方法は、[Key エンドポイント 認証処理](page?id=rest/security/api-key/authentication&lang=ja#比較モード)
の「比較モード」を参照してください（`/api/key/**` と仕組みは共通です）。

`jp.ecuacion.splib.core.util.SplibHashedPropertyResolver` を通じてリクエストのたびに都度読み直されるため、`PropertiesFileUtil` のキャッシュをクリアすれば（[概要](page?id=rest/security/builtin-api-key/overview&lang=ja)
の組み込みコントローラー参照）再起動なしで変更が反映されます。

- **どちらも未設定の場合：** `/api/ecuacion-splib/key/**` へのリクエストはすべて拒否されます。
  これらの組み込みエンドポイントを使わないアプリケーションにとって安全なデフォルトです。
- **どちらか一方のみ設定されている場合：** 送られてきた `X-Api-Key` を、設定に応じて平文または bcrypt で比較します。
- **両方設定されている場合：** これは `application.properties` を管理する側にしか起こりえない設定ミスなので（外部からの呼び出しでは発生し得ない）、下記の通り別扱いで報告されます。

## 拒否時の挙動・認証成功時

ヘッダー欠落、送信元IPのロックアウト中（下記[レート制限](#レート制限)を参照）、誤った（あるいは存在しない）キーのいずれも、`/api/key/**` と同様に汎用的な `401` を返します（比較には `MessageDigest.isEqual` による定数時間比較を使用）。「該当キーなし」「キー相違」「レート制限中」を呼び出し元が区別できないようにするためです。一方、
`jp.ecuacion.splib.rest.builtin-api-key.password-plain` と `...password-bcrypt` の**両方**が設定されている場合はこれとは異なり、該当する2つのプロパティキー名を明示した `500` を返します。
この状態は `application.properties` を管理する側にしか起こりえないためです。

認証に成功すると `ROLE_BUILTIN_API_KEY` 権限で認証されます（`/api/key/**` では `ROLE_API_KEY`）。

## レート制限

`/api/key/**` と同じ送信元IPごとのロックアウト機構を共有しています。仕組み（リバースプロキシに関する注意点含む）は
[レート制限（ブルートフォース対策）](page?id=rest/security/api-key/authentication&lang=ja#レート制限ブルートフォース対策)
を参照してください。ここではこのエンドポイント自身の `jp.ecuacion.splib.rest.builtin-api-key` プレフィックスを使うため、`/api/key/**` とは独立してロックアウトが管理されます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.splib.rest.builtin-api-key.rate-limit.max-failures` | int | ロックアウトまでにウィンドウ内で許容する不一致回数。デフォルト: `10`。 |
| `jp.ecuacion.splib.rest.builtin-api-key.rate-limit.window-seconds` | long | 上記カウントが適用される時間窓（秒）。デフォルト: `60`。 |
| `jp.ecuacion.splib.rest.builtin-api-key.rate-limit.lockout-seconds` | long | ロックアウト発動後、送信元IPがロックアウトされ続ける秒数。デフォルト: `300`。 |
