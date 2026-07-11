# command-api 概要

`ecuacion-tool-command-api` は、Web API 経由でサーバ上のシェルスクリプトを実行する WAR モジュールです。
単独の実行可能 WAR として起動するほか、既存の Tomcat 等のアプリケーションサーバにデプロイして使うこともできます。

## 主な特徴

- HTTP GET リクエスト 1 つでサーバ上のスクリプトを実行
- 実行可能なスクリプトは `ecuacion-tool-command-api.properties` で事前に登録（セキュリティ対策）
- スクリプトのパスに環境変数の参照（`${ENV_VAR}` 形式）に対応
- スクリプトへのパラメータ渡しに対応
- レスポンスにスクリプトの終了コードを返す

## 用途

- 他システムからの HTTP 呼び出しによるサーバ上のバッチ処理のトリガー
- ファイルの生成・配置など、Web アプリ経由では実行できないサーバ操作の起動

## モジュール取得先

WAR ファイルは以下から取得できます。

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/
```

## ドキュメント

- [セットアップ](/public/showMarkdown/page?id=command-api/setup&lang=ja)
- [クイックスタート](/public/showMarkdown/page?id=command-api/quickstart&lang=ja)
- [設定ファイル](/public/showMarkdown/page?id=command-api/config&lang=ja)
- [API仕様](/public/showMarkdown/page?id=command-api/api-spec&lang=ja)
