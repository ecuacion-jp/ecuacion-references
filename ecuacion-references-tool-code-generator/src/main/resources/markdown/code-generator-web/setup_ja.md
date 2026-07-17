## 1. WAR のダウンロード

[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/releases) から
最新の `ecuacion-tool-code-generator-web-x.x.x.war` をダウンロードします。

WAR は任意のディレクトリに配置してください。WAR には Tomcat が組み込まれているため、
外部のアプリケーションサーバは不要です。

## 2. 作業ディレクトリの設定（任意）

Web モジュールはアップロードされた Excel とコード生成物を一時的にサーバ上に保存します。
デフォルトの保存先は `./app-work` です。

変更したい場合は、WAR と同じディレクトリに `application-profile.properties` を作成して
以下を記載します。

```properties
app.work-root-dir=/path/to/work/directory
```

## システム要件

- JDK 21 以上
