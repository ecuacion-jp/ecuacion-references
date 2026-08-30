## 1. JAR ファイルのダウンロード

[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tools/releases) から最新の `ecuacion-tool-housekeep-files-x.x.x.jar` をダウンロードします。

JAR は任意のディレクトリに配置してください。

## 2. Excel 設定ファイルのダウンロード

以下のサンプルファイルをダウンロードして、設定の雛形として使用します。

```
https://github.com/ecuacion-jp/ecuacion-tools/tree/main/ecuacion-tool-housekeep-files/sample
```

ファイル名: `housekeep-files(fmt-v1.3.0)_sample-1.xlsx`

## 3. ログ設定（任意）

Logback の設定ファイルは以下の優先順位で読み込まれます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dlogging.config=...` で指定したパス |
| 2（低） | カレントディレクトリの `config/logback-spring.xml` |

> **Note:** 優先度2の「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。JAR と同じディレクトリに `cd` してから起動する運用（下記）であれば、実質的に「JAR と同じディレクトリの `config/`」と同じ意味になります。

**方法 1 — `config/` サブディレクトリに配置（推奨）:**

```
/your-work-dir/
├── ecuacion-tool-housekeep-files-x.x.x.jar
└── config/
    └── logback-spring.xml
```

```bash
cd /your-work-dir
java -jar ecuacion-tool-housekeep-files-x.x.x.jar
```

**方法 2 — パスを明示:**

```bash
java -Dlogging.config=file:/path/to/logback-spring.xml \
     -jar ecuacion-tool-housekeep-files-x.x.x.jar
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
