`ecuacion-tool-command-api` の2通りの起動方法と、それぞれで何が変わるかを説明します。とにかく素早く動かしたい場合は、代わりに[クイックスタート](page?id=command-api/quickstart&lang=ja)を参照してください。

## 単独で起動する場合（推奨）

WAR は任意のディレクトリに配置してください。WAR には Tomcat が組み込まれているため、外部のアプリケーションサーバは不要です。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

起動後、`http://localhost:8080` で API にアクセスできます。

## 既存の Tomcat 等にデプロイする場合

WAR ファイルを Tomcat 等のアプリケーションサーバにデプロイすることもできます。

バージョンをコンテキストパスに含めたくない場合は、ファイル名を変更してからデプロイすると便利です。

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # Tomcat のバージョン並行デプロイ機能を使う場合
```

## 両者の違い

どちらの方法で起動しても、スクリプトの登録方法（`ecuacion-tool-command-api.properties` の書式）自体は共通です。ただし設定ファイルの配置ルールは起動方法によって異なります。単独起動の場合はWARと同じディレクトリに配置しますが、既存のTomcat等にデプロイする場合は「WARと同じディレクトリ」という概念がないため、`CLASSPATH` 環境変数で指定したディレクトリに配置します（詳細は[設定ファイル](page?id=command-api/config&lang=ja)を参照）。
