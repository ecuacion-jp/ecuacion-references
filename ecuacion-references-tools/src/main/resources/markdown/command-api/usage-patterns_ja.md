このページでは、[クイックスタート](page?id=command-api/quickstart&lang=ja)を踏まえた `api/public/execute` ・ `api/key/execute` の利用パターンを説明します。

## パラメータを渡す

スクリプトにパラメータを渡すには `parameters` クエリパラメータを使用します。

```
http://localhost:8080/api/public/execute?scriptId=script.say-hello&parameters=param1,param2
```

上記のリクエストでは `sayHello.sh param1 param2`（Windows の場合は `sayHello.bat param1 param2`）として実行されます。

カンマ区切りで複数のパラメータを渡せます。カンマ自体をパラメータ値として渡す方法は現在未対応です。

---

## パスに変数参照を使用する

スクリプトのパスに `${VAR_NAME}` 形式の変数参照を使用できます。

```properties
script.say-hello=${USER_HOME}/script/directory/sayHello.sh
```

`application.properties` や OS 環境変数などで `USER_HOME` が設定されていれば、実行時に展開されます。
