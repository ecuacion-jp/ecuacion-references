# command-api セットアップ

## 1. WAR ファイルのダウンロード

以下の Maven リポジトリから WAR ファイルをダウンロードします。

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/
```

URL の例（バージョン `x.x.x` は実際のバージョンに置き換えてください）:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-command-api/x.x.x/ecuacion-tool-command-api-x.x.x.war
```

## 2. 起動する

### 単独で起動する場合（推奨）

WAR は任意のディレクトリに配置してください。WAR には Tomcat が組み込まれているため、外部のアプリケーションサーバは不要です。

```bash
java -jar ecuacion-tool-command-api-x.x.x.war
```

起動後、`http://localhost:8080` で API にアクセスできます。

### 既存の Tomcat 等にデプロイする場合

WAR ファイルを Tomcat 等のアプリケーションサーバにデプロイすることもできます。

バージョンをコンテキストパスに含めたくない場合は、ファイル名を変更してからデプロイすると便利です。

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # Tomcat のバージョン並行デプロイ機能を使う場合
```

どちらの方法で起動しても、スクリプトの登録方法や設定ファイルの配置ルールは共通です（詳細は[設定ファイル](/public/showMarkdown/page?id=command-api/config&lang=ja)を参照）。

## システム要件

- JDK 21 以上
- Linux または macOS（Windows は未サポート。スクリプトの実行に `Runtime.exec` を使用するため）
