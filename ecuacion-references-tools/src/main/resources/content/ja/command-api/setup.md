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

## 2. アプリケーションサーバへのデプロイ

WAR ファイルを Tomcat 等のアプリケーションサーバにデプロイします。

バージョンをコンテキストパスに含めたくない場合は、ファイル名を変更してからデプロイすると便利です。

```
ecuacion-tool-command-api.war           # → /ecuacion-tool-command-api
ecuacion-tool-command-api##x.x.x.war   # Tomcat のバージョン並行デプロイ機能を使う場合
```

## 3. CLASSPATH の設定

アプリケーションサーバの環境変数に `CLASSPATH` を追加します。

Tomcat の場合は `${CATALINA_HOME}/bin/setenv.sh` を作成（または編集）します。

```bash
CLASSPATH=/path/to/classpath/directory
export CLASSPATH
```

## 4. ログ設定ファイルの作成

CLASSPATH ディレクトリに `logback-spring-ecuacion-tool-command-api.xml` を作成します。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xml>
<configuration>

    <!-- appenders -->
    <property name="log-dir" value="/path/to/logs/directory" />
    <property name="loglevel-spring" value="INFO" />
    <include resource="logback-spring-appenders.xml" />
    <include resource="logback-spring-appenders-local.xml" />

    <!-- loggers -->
    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-security" value="INFO" />
    <property name="loglevel-sql" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-for-local.xml" />
    <include resource="logback-spring-loggers-web-for-local.xml" />

</configuration>
```

## 5. プロパティファイルの作成

CLASSPATH ディレクトリに `ecuacion-tool-command-api.properties` を作成します（中身は空で構いません）。
実行するスクリプトの登録はこのファイルで行います（詳細は[クイックスタート](/public/ja/article?id=command-api/quickstart)を参照）。

## システム要件

- JDK 21 以上
- Linux または macOS（Windows は未サポート）
- Tomcat 等の Java アプリケーションサーバ
