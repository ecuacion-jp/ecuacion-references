以下のパスは単独起動（ルートコンテキスト）を前提としています。既存の Tomcat 等にデプロイする場合は、コンテキストパス（例: `/ecuacion-tool-command-api`）がパスの前に付きます。

## エンドポイント

### スクリプト実行

```
GET  /api/public/executeScript
POST /api/public/executeScript
```

デフォルトでは `GET` は拒否され、`POST` は一致する `apiKey` が必要です。詳細は下記の[アクセス制御](#アクセス制御)を参照してください。

### パラメータ

| パラメータ | 必須 | 説明 |
| --- | --- | --- |
| `scriptId` | ○ | `ecuacion-tool-command-api.properties` で定義したスクリプト ID |
| `parameter` | — | スクリプトに渡すパラメータ（カンマ区切りで複数指定） |
| `apiKey` | POST時のみ、条件付き必須 | サーバ側に配置した api-key ファイルの内容と照合する共有シークレット。`jp.ecuacion.tool.command-api.allow-insecure-access=true` の場合を除き必須。`GET` では無視されます。 |

### レスポンス

**成功時（HTTP 200）**

```json
{
    "returnCode": "0"
}
```

`returnCode` はシェルスクリプトの終了コード（`$?` の値）です。

スクリプト実行自体は成功したが、スクリプト内でエラーが発生した場合も HTTP 200 が返ります。
終了コードの値でスクリプトの成否を確認してください。

---

## エラーレスポンス

### HTTP 403 / 404

URL が正しくない場合、または `jp.ecuacion.tool.command-api.allow-insecure-access` が `true` でない状態で `GET` リクエストが来た場合に返ります（[アクセス制御](#アクセス制御)を参照）。

### HTTP 401

`POST` リクエストで `apiKey` が未指定、サーバ側の api-key ファイルの内容と一致しない、または api-key ファイル自体が未設定・読み込み不可の場合に返ります。原因の切り分けを応答内容から行えないよう、いずれの場合も同一のレスポンスになります（設定不備とキー不一致の区別を攻撃者にさせないため）。原因の切り分けはサーバ側のログで行ってください。

### HTTP 400

以下の、リクエスト側に起因する場合に返ります。

- `scriptId` の値が正規表現 `^[a-zA-Z0-9.\-_]*$` に一致しない場合
- `scriptId` に指定したスクリプト ID が `ecuacion-tool-command-api.properties` に登録されていない場合

### HTTP 500

以下の、サーバ側の設定に起因する場合に返ります。

- `ecuacion-tool-command-api.properties` に登録されたスクリプトファイルパスが正規表現 `^[a-zA-Z0-9.\-_/${}]*$` に一致しない場合（設定ミス）
- 登録されたスクリプトファイルが実際には存在しない場合

### エラーレスポンスボディの形式

400 / 500 いずれの場合も、レスポンスボディは以下の形式（[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) 形式の ProblemDetail）です。

```json
{
    "type": "problemDetail.type.org.springframework.web.server.ResponseStatusException",
    "title": "problemDetail.title.org.springframework.web.server.ResponseStatusException",
    "status": 500,
    "detail": "problemDetail.org.springframework.web.server.ResponseStatusException",
    "instance": "/api/public/executeScript"
}
```

> **Note:** 現状、`title` / `detail` はエラー内容によらず上記の固定文言が返り、原因ごとの具体的なメッセージはボディに含まれません。原因の切り分けは `status` の値と、サーバ側のログ（`scriptId` / `scriptFilePath` の値を出力）で行ってください。

---

## セキュリティ

### スクリプトの事前登録

実行できるスクリプトは `ecuacion-tool-command-api.properties` に登録したものだけです。
リクエストで任意のスクリプトパスを指定することはできません。

ただし、登録するスクリプト自体に危険な操作が含まれている場合はその限りではありません。
登録するスクリプトの内容については利用者自身が責任を持って管理してください。

### 入力値のバリデーション

`scriptId` パラメータの値は正規表現 `^[a-zA-Z0-9.\-_]*$` でバリデーションされます。

スクリプトファイルパスは正規表現 `^[a-zA-Z0-9.\-_/${}]*$` でバリデーションされます。

### アクセス制御

デフォルトでは `GET` は無効化されており、`POST` は一致する `apiKey` が必要です。`apiKey` は**単純な共有シークレット**であり、サーバ側に配置したファイルの内容と照合されます。非対称鍵（公開鍵・秘密鍵のペア）ではなく、クライアントが送信する値が秘密鍵として扱われることもありません。

`jp.ecuacion.tool.command-api.allow-insecure-access=true` を設定すると `GET` が許可され、`POST` の `apiKey` 検証も省略されます。信頼できる内部ネットワークでのみ使用してください。プロパティの詳細は[設定ファイル](/public/showMarkdown/page?id=command-api/config&lang=ja)を参照してください。

---

## スクリプトの登録・設定ファイル

`ecuacion-tool-command-api.properties` へのスクリプト登録方法、配置場所やログ設定については
[設定ファイル](/public/showMarkdown/page?id=command-api/config&lang=ja)を参照してください。

---

## 生存確認エンドポイント

以下のエンドポイントでサーバが正常に動作しているかを確認できます。

```
GET /api/public/aliveCheck
```

レスポンス（HTTP 200）:

```json
{
    "returnCode": "0"
}
```
