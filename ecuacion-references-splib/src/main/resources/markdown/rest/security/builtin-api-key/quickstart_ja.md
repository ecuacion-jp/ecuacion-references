[クイックスタート](page?id=rest/quickstart&lang=ja) の手順で `AppRestSecurityConfig` を用意していれば、
`/api/ecuacion-splib/key/**` を使うのに追加のコードは不要です。プロパティを1つ設定するだけで動かせます。

## 1. キーを設定する

`/api/key/**` と異なり、ここではアプリケーション実装の Provider Bean は不要です。期待値は
`application.properties` から直接読み込まれます。次のどちらか一方だけを設定してください。

```properties
jp.ecuacion.splib.rest.builtin-api-key.password-plain=your-api-key-here
# または
jp.ecuacion.splib.rest.builtin-api-key.password-bcrypt=$2a$10$...
```

`password-bcrypt` にはキーの bcrypt ハッシュを設定します。これにより生の値は
`application.properties` に平文のままでは残りません。生成方法は
[認証処理](page?id=rest/security/builtin-api-key/authentication&lang=ja) の「比較モード」を参照してください。

どちらも設定していない場合、`/api/ecuacion-splib/key/**` へのリクエストはすべて拒否されます。これらの
組み込みエンドポイントを使わないアプリケーションにとって安全なデフォルトです。

## 2. 呼び出してみる

アプリケーションをローカルで起動し、curl で叩いてみます。`X-Api-Key` ヘッダーを付けない場合や
違う値を送った場合は `401` が返ることも確認してみてください。

```
curl -X POST -H "X-Api-Key: your-api-key-here" http://localhost:8080/api/ecuacion-splib/key/clearPropertiesCache
```

成功すると、本文なしの `200` が返ります。この組み込みコントローラー自体が何をするかは
[概要](page?id=rest/security/builtin-api-key/overview&lang=ja) を参照してください。
