以下のパスは単独起動（ルートコンテキスト）を前提としています。既存の Tomcat 等にデプロイする場合は、コンテキストパス（例: `/ecuacion-tool-command-api`）がパスの前に付きます。

## エンドポイント

### スクリプト実行

```
GET  /api/public/executeScript   # jp.ecuacion.tool.command-api.api-key-required=false の場合のみ有効
POST /api/public/executeScript   # jp.ecuacion.tool.command-api.api-key-required=false の場合のみ有効
GET  /api/key/executeScript      # X-Api-Key ヘッダによる認証が必須
POST /api/key/executeScript      # X-Api-Key ヘッダによる認証が必須
```

`api/public/executeScript` はデフォルトで無効（403で拒否）です。`api/key/executeScript` は常に `X-Api-Key` ヘッダによる認証が必須です。

どちらのエンドポイントでも、`GET` / `POST` のどちらでアクセスできるかは、スクリプト定義の `GET:` / `POST:` / `ALL:` プレフィックス（[設定ファイル](page?id=command-api/config&lang=ja)を参照）で個別に制御します（プレフィックス省略時は `POST` のみ許可）。両エンドポイントの違いはこのメソッド制限ではなく、`X-Api-Key` による認証が必須かどうかだけです。

詳細は下記の[アクセス制御](#アクセス制御)を参照してください。

### パラメータ

| パラメータ | 必須 | 説明 |
| --- | --- | --- |
| `scriptId` | ○ | `ecuacion-tool-command-api.properties` で定義したスクリプト ID |
| `parameters` | — | スクリプトに渡すパラメータ（カンマ区切りで複数指定） |
| `X-Api-Key`（HTTPヘッダ） | `api/key/executeScript` では必須 | サーバ側に配置した api-key ファイルの内容と照合する共有シークレット。`api/public/executeScript` では使用されません。 |

### レスポンス

**成功時（HTTP 200）**

```json
{
    "returnCode": "0",
    "stdout": "...",
    "stderr": "..."
}
```

`returnCode` はシェルスクリプトの終了コード（`$?` の値）です。`stdout` / `stderr` はスクリプトの標準出力・標準エラー出力を改行区切りで結合したものです（出力がない場合は空文字列）。

スクリプト実行自体は成功したが、スクリプト内でエラーが発生した場合も HTTP 200 が返ります。
終了コードの値でスクリプトの成否を確認してください。

---

## エラーレスポンス

### HTTP 400

以下の、リクエスト側に起因する場合に返ります。

- `scriptId` の値が正規表現 `^[a-zA-Z0-9.\-_]*$` に一致しない場合
- `scriptId` に指定したスクリプト ID が `ecuacion-tool-command-api.properties` に登録されていない場合
- `parameters` の値が正規表現 `^[a-zA-Z0-9 ./:_=@\-]*$` に一致しない場合

### HTTP 401

`api/key/executeScript` へのリクエストで、以下の場合に返ります。原因の切り分けを応答内容から行えないよう、いずれの場合も同一のレスポンスになります（設定不備とキー不一致の区別を攻撃者にさせないため）。原因の切り分けはサーバ側のログで行ってください。

- `X-Api-Key` ヘッダが未指定の場合
- 提示された `X-Api-Key` の値がサーバ側の api-key ファイルの内容と一致しない場合
- api-key ファイル自体が未設定、または読み込み不可の場合

### HTTP 403

以下の場合に返ります（[アクセス制御](#アクセス制御)を参照）。

- `jp.ecuacion.tool.command-api.api-key-required` が `false` に設定されていない状態（デフォルト）で `api/public/executeScript` にリクエストが来た場合
- `api/public/executeScript`（有効化されている場合）または `api/key/executeScript`（有効な `X-Api-Key` を伴う場合）へのリクエストで、対象スクリプトの定義（`GET:` / `POST:` / `ALL:` プレフィックス）がそのHTTPメソッドを許可していない場合

### HTTP 404

URL が正しくない場合に返ります。

### HTTP 500

以下の、サーバ側の設定に起因する場合に返ります。

- `ecuacion-tool-command-api.properties` に登録されたスクリプトファイルパスが正規表現 `^[a-zA-Z0-9.\-_/${}]*$` に一致しない場合（設定ミス）
- 登録されたスクリプトファイルが実際には存在しない場合
- 登録されたスクリプトファイルに実行権限がない場合
- スクリプトファイルパス中の `${...}` 形式の環境変数参照が不正（波括弧の対応が取れていない）、または参照先の環境変数が未設定の場合
- OS がスクリプトの起動自体に失敗した場合（シバンの指定誤りなど、実行権限はあるのに起動できないケース）

### エラーレスポンスボディの形式

400 / 403 / 500 いずれの場合も、レスポンスボディは以下の形式（[RFC 9457](https://www.rfc-editor.org/rfc/rfc9457) 形式の ProblemDetail）です。

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

> **Note:** 401（[アクセス制御](#アクセス制御)を参照）はこの形式の対象外です。401 は Spring MVC に到達する前の Servlet フィルタ層で `HttpServletResponse.sendError()` により返されるため、上記の ProblemDetail 形式ではなく、Spring Boot標準のエラーページ形式（`timestamp` / `status` / `error` / `path` を含む JSON）になります。

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

`X-Api-Key` は**共有シークレット**方式であり、サーバ側に配置したファイルの内容と照合されます。非対称鍵（公開鍵・秘密鍵のペア）ではなく、クライアントが送信する値が秘密鍵として扱われることもありません。プロパティの詳細（`api-key-required`、`api-key-comparison-mode`）やキーファイルの管理方法については[アクセス制御](page?id=command-api/access-control&lang=ja)を参照してください。

---

## スクリプトの登録・設定ファイル

`ecuacion-tool-command-api.properties` へのスクリプト登録方法、配置場所やログ設定については
[設定ファイル](page?id=command-api/config&lang=ja)を参照してください。

---

## 生存確認エンドポイント

以下のエンドポイントでサーバが正常に動作しているかを確認できます。`ecuacion-splib-rest` が提供する共通エンドポイントで、command-api 固有のものではありません。

```
GET  /api/ecuacion-splib/public/aliveCheck
POST /api/ecuacion-splib/public/aliveCheck
```

`{"status": "OK"}` を本文とする HTTP 200 が返ります。
