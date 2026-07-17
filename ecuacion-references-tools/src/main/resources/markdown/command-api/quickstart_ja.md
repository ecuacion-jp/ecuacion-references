[セットアップ](/public/showMarkdown/page?id=command-api/setup&lang=ja) が完了していることを前提とします。

## 単独で起動して使う

### 1. アプリを起動する

WAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

### 2. スクリプトの準備

任意のディレクトリに `sayHello.sh` を作成します。

```bash
#!/bin/bash

touch /path/to/script/directory/touch.file
echo "Touch done."
```

スクリプトファイルにアプリケーションの実行ユーザで実行権限を付与します。

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

### 3. スクリプトを properties に登録する

WAR と同じディレクトリに `ecuacion-tool-command-api.properties` を作成し、以下を追記します（配置ルールの詳細は[設定ファイル](/public/showMarkdown/page?id=command-api/config&lang=ja)を参照）。

```properties
script.say-hello=/path/to/script/directory/sayHello.sh
```

**書式**: `script.<スクリプトID>=<スクリプトのフルパス>`

スクリプト ID はリクエスト時の `scriptId` パラメータに対応します。プロパティファイルを変更したら、アプリケーションを再起動して反映します。

### 4. API を呼び出す

以下の URL にアクセスします。

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello
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
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello&parameter=param1,param2
```

上記のリクエストでは `sayHello.sh param1 param2` として実行されます。

カンマ区切りで複数のパラメータを渡せます。カンマ自体をパラメータ値として渡す方法は現在未対応です。

---

## 環境変数をパスに使用する

スクリプトのパスに環境変数を使用できます。

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

アプリケーションの実行環境に `USER_HOME` 環境変数が設定されていれば、起動時に展開されます。

---

## エラーの確認方法

リクエストが失敗した場合は HTTP ステータスコードとレスポンスボディでエラー内容を確認できます（詳細は[API仕様](/public/showMarkdown/page?id=command-api/api-spec&lang=ja)を参照）。

詳細なログはアプリケーションのログファイル（またはコンソール）で確認できます。
