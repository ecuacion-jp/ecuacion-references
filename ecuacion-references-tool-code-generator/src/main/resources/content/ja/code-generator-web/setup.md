# code-generator-web セットアップ

## 1. リポジトリのクローン

[code-generator-batch セットアップ](/public/ja/article?id=code-generator-batch/setup) の手順と同様に、
リポジトリをクローンしてビルドします。

```bash
git clone https://github.com/ecuacion-jp/ecuacion-tool-code-generator.git
cd ecuacion-tool-code-generator
mvn clean install -DskipTests
```

## 2. 一時ファイル保存ディレクトリの設定

Web モジュールはアップロードされた Excel とコード生成物を一時的にサーバ上に保存します。
その保存先を `app.work-root-dir` プロパティで指定します。

アプリケーションサーバの CLASSPATH ディレクトリに `application-profile.properties` を作成して
以下を記載します。

```properties
app.work-root-dir=/path/to/work/directory
```

### ローカル開発時の設定

`ecuacion-tool-code-generator-web/src/envs/local/resources/` ディレクトリにある
`application-profile.properties` を編集します。

```properties
app.work-root-dir=/tmp/code-generator-work
```

## 3. アプリケーションサーバへのデプロイ（本番環境）

`ecuacion-tool-code-generator-web` モジュールのビルド成果物（WAR ファイル）を
Tomcat 等にデプロイします。

```bash
cd ecuacion-tool-code-generator/ecuacion-tool-code-generator-web
mvn package
```

WAR ファイルは `target/ecuacion-tool-code-generator-web-x.x.x.war` に生成されます。

## システム要件

- JDK 21 以上
- Maven 3.x
- Tomcat 等の Java アプリケーションサーバ（サーバデプロイ時）
