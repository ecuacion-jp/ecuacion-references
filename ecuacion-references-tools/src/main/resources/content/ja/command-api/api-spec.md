# command-api API仕様

## エンドポイント

### スクリプト実行

```
GET /ecuacion-tool-command-api/api/public/executeScript
```

### クエリパラメータ

| パラメータ | 必須 | 説明 |
| --- | --- | --- |
| `scriptId` | ○ | `ecuacion-tool-command-api.properties` で定義したスクリプト ID |
| `parameter` | — | スクリプトに渡すパラメータ（カンマ区切りで複数指定） |

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

URL が正しくない場合に返ります。

### HTTP 400

`scriptId` に指定したスクリプト ID が `ecuacion-tool-command-api.properties` に登録されていない場合に返ります。

### HTTP 500

以下の場合に返ります。

**`ecuacion-tool-command-api.properties` が CLASSPATH 上に見つからない場合:**

```json
{
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "'ecuacion-tool-command-api.properties' not found on classpath.",
    "instance": "/ecuacion-tool-command-api/api/public/executeScript"
}
```

**スクリプトファイルが見つからない場合:**

```json
{
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "scriptFilePath '/path/to/script/directory/sayHello.sh' not found.",
    "instance": "/ecuacion-tool-command-api/api/public/executeScript"
}
```

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

---

## ecuacion-tool-command-api.properties の設定

CLASSPATH ディレクトリに配置するプロパティファイルです。

### スクリプトの登録

```properties
script.<スクリプトID>=<スクリプトの絶対パス>
```

例:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

### 環境変数の使用

スクリプトのパスに環境変数を使用できます（`${ENV_VAR}` 形式）。

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

### 生存確認エンドポイント

以下のエンドポイントでサーバが正常に動作しているかを確認できます。

```
GET /ecuacion-tool-command-api/api/public/alive
```

レスポンス（HTTP 200）:

```json
{
    "result": "alive"
}
```
