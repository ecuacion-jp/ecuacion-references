`ecuacion-tool-command-api` は、Web API 経由でサーバ上のシェルスクリプトを実行する WAR モジュールです。
単独の実行可能 WAR として起動するほか、既存の Tomcat 等のアプリケーションサーバにデプロイして使うこともできます。

## 主な特徴

- HTTP GET リクエスト 1 つでサーバ上のスクリプトを実行
- 実行可能なスクリプトは `ecuacion-tool-command-api-scripts.properties` で事前に登録（セキュリティ対策）
- スクリプトのパスに `${VAR_NAME}` 形式の変数参照に対応（application.properties・OS 環境変数・JVM システムプロパティ等から解決）
- スクリプトへのパラメータ渡しに対応
- レスポンスにスクリプトの終了コードを返す

## 用途

- 他システムからの HTTP 呼び出しによるサーバ上のバッチ処理のトリガー
