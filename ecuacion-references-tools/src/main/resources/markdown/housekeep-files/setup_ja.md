# housekeep-files セットアップ

## 1. JAR ファイルのダウンロード

以下の Maven リポジトリから JAR ファイルをダウンロードします。

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/
```

URL の例（バージョン `x.x.x` は実際のバージョンに置き換えてください）:

```
https://maven-repo.ecuacion.jp/public/jp/ecuacion/tool/ecuacion-tool-housekeep-files/x.x.x/ecuacion-tool-housekeep-files-x.x.x.jar
```

## 2. Excel 設定ファイルのダウンロード

以下のサンプルファイルをダウンロードして、設定の雛形として使用します。

```
https://github.com/ecuacion-jp/ecuacion-tools/tree/main/ecuacion-tool-housekeep-files/sample
```

ファイル名: `housekeep-files(fmt-v1.3.0)_sample-1.xlsx`

## 3. ログ設定（任意）

ログ出力をカスタマイズする場合は、`logback-spring.xml` を作成して CLASSPATH ディレクトリに配置します。

CLASSPATH ディレクトリの指定方法:

```
java -jar ecuacion-tool-housekeep-files-x.x.x.jar --classpath=/path/to/classpath/directory excelPath=/path/to/settings.xlsx
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
- Linux または macOS（Windows は未サポート）
