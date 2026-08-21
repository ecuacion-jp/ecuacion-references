[セットアップ](page?id=command-api/setup&lang=ja) が完了していることを前提とします。

### 1. スクリプトの準備

Linux/macOS の場合、任意のディレクトリに `sayHello.sh` を作成します。

```bash
#!/bin/bash

echo "Hello!"
touch /path/to/script/directory/touch.file
```

スクリプトファイルにアプリケーションの実行ユーザで実行権限を付与します。

```bash
chmod +x /path/to/script/directory/sayHello.sh
```

Windows の場合は代わりに `sayHello.bat` を作成します（実行権限の付与は不要です）。

```bat
@echo off
echo Hello!
type nul > C:\path\to\script\directory\touch.file
```

### 2. スクリプトを properties に登録する

WAR と同じディレクトリに `ecuacion-tool-command-api-scripts.properties` を作成し、以下を追記します（配置ルールの詳細は[設定ファイル](page?id=command-api/config&lang=ja#ecuacion-tool-command-api-scripts-properties)を参照）。

```properties
script.say-hello=GET:/path/to/script/directory/sayHello.sh
```

（Windows の場合は `.bat` ファイルのパスを指定します。例: `script.say-hello=GET:C:\\path\\to\\script\\directory\\sayHello.bat`）

先頭の `GET:` は、このスクリプトを `GET` で呼び出せるようにする指定です。詳細は[設定ファイル](page?id=command-api/config&lang=ja#許可するhttpメソッドの指定)を参照

### 3. このクイックスタート用にアクセスを許可する

デフォルトでは `api/public/execute` へのアクセスは無効化されています（[アクセス制御](page?id=command-api/access-control&lang=ja#アクセス制御)を参照）。このローカルでのクイックスタートでは、WAR と同じディレクトリに `application.properties` を作成し、以下を追記してください（配置ルールの詳細は[設定ファイル](page?id=command-api/config&lang=ja#application-properties)を参照）。

```properties
jp.ecuacion.tool.command-api.api-key-required=false
```

（本番環境ではこの設定は行わず、`jp.ecuacion.tool.command-api.api-key-required=true` を明示的に設定してください。詳細は[API仕様](page?id=command-api/api-spec&lang=ja)を参照）

### 4. アプリを起動する

WAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

### 5. API を呼び出す

以下の URL にアクセスします。

```
http://localhost:8080/api/public/execute?scriptId=script.say-hello
```

成功すると以下のような JSON レスポンスが返ります。

```json
{
    "returnCode": "0",
    "stdout": "Hello!",
    "stderr": ""
}
```

`stdout` に `Hello!` が含まれ、`/path/to/script/directory/touch.file` が作成されていれば動作確認完了です。
