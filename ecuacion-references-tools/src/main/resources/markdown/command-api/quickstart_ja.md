[セットアップ](page?id=command-api/setup&lang=ja) が完了していることを前提とします。

## 単独で起動して使う

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

WAR と同じディレクトリに `ecuacion-tool-command-api.properties` を作成し、以下を追記します（配置ルールの詳細は[設定ファイル](page?id=command-api/config&lang=ja)を参照）。

```properties
script.say-hello=GET:/path/to/script/directory/sayHello.sh
```

（Windows の場合は `.bat` ファイルのパスを指定します。例: `script.say-hello=GET:C:\\path\\to\\script\\directory\\sayHello.bat`）

**書式**: `script.<スクリプトID>=[GET:|POST:|ALL:]<スクリプトのフルパス>`

先頭の `GET:` は、このスクリプトを `GET` で呼び出せるようにする指定です（プレフィックスを省略すると `POST` のみ許可になり、後述の手順5のGET呼び出しが失敗します。詳細は[設定ファイル](page?id=command-api/config&lang=ja)を参照）。スクリプト ID はリクエスト時の `scriptId` パラメータに対応します。

### 3. このクイックスタート用にアクセスを許可する

デフォルトでは `api/public/executeScript` への `GET` アクセスは無効化されています（[アクセス制御](page?id=command-api/config&lang=ja#アクセス制御)を参照）。このローカルでのクイックスタートでは、`application.properties`（配置場所は[設定ファイル](page?id=command-api/config&lang=ja)を参照）に以下を追記してください。

```properties
jp.ecuacion.tool.command-api.api-key-required=false
```

（本番環境ではこの設定は行わず、`jp.ecuacion.tool.command-api.api-key-required=true` を明示的に設定した上で、代わりに `X-Api-Key` ヘッダを付けて `api/key/executeScript` を呼び出してください。未設定のままだと起動時に警告ログが出力されるため、`true` を明示することをおすすめします。詳細は[API仕様](page?id=command-api/api-spec&lang=ja)を参照）

### 4. アプリを起動する

WAR を配置したディレクトリで以下を実行します。ここまでの手順で配置した `sayHello.sh` ・ `ecuacion-tool-command-api.properties` ・ `application.properties` は、起動時に読み込まれます（アプリ起動後にこれらのファイルを追加・変更した場合は、反映のためにアプリの再起動が必要です）。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

### 5. API を呼び出す

以下の URL にアクセスします。

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello
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

---

パラメータの渡し方、スクリプトパスへの環境変数の使用、エラーの確認方法など、APIの他の呼び出し方については[利用パターン](page?id=command-api/usage-patterns&lang=ja)を参照してください。
