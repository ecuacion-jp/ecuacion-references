## 1. JAR ファイルのダウンロード

[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tools/releases) から
最新の `ecuacion-tool-housekeep-db-x.x.x.jar` をダウンロードします。

JAR は任意のディレクトリに配置してください。

## 2. Excel 設定ファイルのダウンロード

以下のサンプルファイルをダウンロードして、設定の雛形として使用します。

```
https://github.com/ecuacion-jp/ecuacion-tools/tree/main/ecuacion-tool-housekeep-db/local-test
```

ファイル: `housekeep-db(fmt-v1.3.0-ja)_sample.xlsx`（日本語版）または `housekeep-db(fmt-v1.3.0-en)_sample.xlsx`（英語版）

## 3. ログ設定（任意）

ログ出力をカスタマイズする場合は、`logback-spring.xml` を作成して CLASSPATH ディレクトリに配置します。

CLASSPATH ディレクトリの指定方法:

```
java -jar ecuacion-tool-housekeep-db-x.x.x.jar --classpath=/path/to/classpath/directory excelPath=/path/to/settings.xlsx
```

`logback-spring.xml` の記述例:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="log-dir" value="/path/to/logs/directory" />
    <property name="loglevel-spring" value="INFO" />
    <include resource="logback-spring-appenders.xml" />
    <include resource="logback-spring-appenders-local.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-security" value="INFO" />
    <property name="loglevel-sql" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-for-local.xml" />
</configuration>
```

## システム要件

- JDK 21 以上
- PostgreSQL（現時点では他の DB は非対応）
