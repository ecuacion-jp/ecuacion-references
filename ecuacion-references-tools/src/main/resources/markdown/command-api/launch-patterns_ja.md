`ecuacion-tool-command-api` の2通りの起動方法と、それぞれで何が変わるかを説明します。

## 単独で起動する場合

WAR は任意のディレクトリに配置してください。WAR には Tomcat が組み込まれているため、外部のアプリケーションサーバは不要です。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

起動後、`http://localhost:8080` で API にアクセスできます。

> **Note:** 本WARは `spring-boot-maven-plugin` を用いて生成されているため、`--server.port=8081` などのSpring Boot標準の起動オプションを使用できます。

## 既存の Tomcat 等にデプロイする場合

WAR ファイルを Tomcat 等のアプリケーションサーバにデプロイすることもできます。WAR には `app-conf/ecuacion-tool-command-api` ディレクトリへの classpath があらかじめ通るよう `META-INF/context.xml` が同梱されているため、このディレクトリに設定ファイルを置くだけで認識されます（詳細は[設定ファイル](page?id=command-api/config&lang=ja)を参照）。

バージョンをコンテキストパスに含めたくない場合は、ファイル名を変更してからデプロイすると便利です。

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # Tomcat のバージョン並行デプロイ機能を使う場合
```
