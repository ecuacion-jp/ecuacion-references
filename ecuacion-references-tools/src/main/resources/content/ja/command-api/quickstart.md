# command-api クイックスタート

ここでは、シンプルなシェルスクリプトを Web API 経由で実行する例を説明します。

## 前提

- [セットアップ](/public/ja/article?id=command-api/setup)が完了していること

## 手順

### 1. スクリプトの準備

任意のディレクトリに `sayHello.sh` を作成します。

```bash
#!/bin/bash

touch /path/to/script/directory/touch.file
echo "Touch done."
```

スクリプトファイルにアプリケーションサーバの実行ユーザで実行権限を付与します。

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

### 2. スクリプトを properties に登録する

`ecuacion-tool-command-api.properties` に以下を追記します。

```properties
script.say-hello=/path/to/script/directory/sayHello.sh
```

**書式**: `script.<スクリプトID>=<スクリプトのフルパス>`

スクリプト ID はリクエスト時の `scriptId` パラメータに対応します。

### 3. アプリケーションサーバを再起動する

properties ファイルの変更を反映するためにアプリケーションサーバを再起動します。

### 4. API を呼び出す

以下の URL にアクセスします。

```
http://yourdomain.com/ecuacion-tool-command-api/api/public/executeScript?scriptId=script.say-hello
```

成功すると以下のような JSON レスポンスが返ります。

```json
{
    "returnCode": "0"
}
```

`/path/to/script/directory/touch.file` が作成されていれば動作確認完了です。

---

## パラメータを渡す

スクリプトにパラメータを渡すには `parameter` クエリパラメータを使用します。

```
http://yourdomain.com/ecuacion-tool-command-api/api/public/executeScript?scriptId=script.say-hello&parameter=param1,param2
```

上記のリクエストでは `sayHello.sh param1 param2` として実行されます。

カンマ区切りで複数のパラメータを渡せます。カンマ自体をパラメータ値として渡す方法は現在未対応です。

---

## 環境変数をパスに使用する

スクリプトのパスに環境変数を使用できます。

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

アプリケーションサーバに `USER_HOME` 環境変数が設定されていれば、起動時に展開されます。
