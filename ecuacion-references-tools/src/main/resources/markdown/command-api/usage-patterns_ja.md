このページでは、[クイックスタート](page?id=command-api/quickstart&lang=ja)を踏まえた `api/public/executeScript` ・ `api/key/executeScript` の利用パターンを説明します。

## パラメータを渡す

スクリプトにパラメータを渡すには `parameter` クエリパラメータを使用します。

```
http://localhost:8080/api/public/executeScript?scriptId=script.say-hello&parameter=param1,param2
```

上記のリクエストでは `sayHello.sh param1 param2`（Windows の場合は `sayHello.bat param1 param2`）として実行されます。

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

リクエストが失敗した場合は HTTP ステータスコードとレスポンスボディでエラー内容を確認できます（詳細は[API仕様](page?id=command-api/api-spec&lang=ja)を参照）。

詳細なログはアプリケーションのログファイル（またはコンソール）で確認できます。
