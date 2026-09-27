このページでは、`ecuacion-tool-code-generator-web` が使用する2つの設定ファイル — `application.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはcode-generator-web独自の仕組みではなく、Spring Bootの機能です。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

追加の設定は `application.properties` に記述してください。作成するファイルには、変更したい設定項目だけを記述すれば十分です。記述しなかった項目は、以下に記載のデフォルト値のまま動作します。

#### アプリ設定

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `work-dir` | 一時作業ファイルのベースディレクトリ | `./app-work` |

> **Note:** コード生成処理（`/public/sourceDownload/action`）は認証なしで誰でも実行できるエンドポイントです。アプリ自体にはリクエストのレート制限・同時実行数の制限がないため、インターネットに公開する場合はリバースプロキシ／WAF 側でレート制限を設定してください。

#### メール通知（エラー発生時）

システムエラー発生時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` /
`jp.ecuacion.splib.mail.*` の各プロパティ・デフォルト値・設定例（Gmail含む）は
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=ja)
を参照してください。

---

## logback-spring.xml

Logbackの設定ファイルです。配置方法は下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>./logs/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>./logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <logger name="org.springframework" level="WARN" />
    <logger name="jp.ecuacion" level="INFO" />

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>

</configuration>
```

---

## ファイルの配置

この2つのファイルをどこに置くかは、`ecuacion-tool-code-generator-web` の起動方法によって異なります。

### 単独で起動する場合

#### オプションを指定しない場合

2つのファイルを、アプリを起動したカレントディレクトリの直下、またはその `config` サブディレクトリに置いてください。オプションの指定は不要です。

```
/your-work-dir/
├── ecuacion-tool-code-generator-web-x.x.x.war
└── config/
    ├── application.properties
    └── logback-spring.xml
```

> **Note:** 「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用であれば、実質的に「WAR と同じディレクトリ」基準になります。

両方の場所にファイルがある場合、`application.properties` はキー単位でマージされ（重複したキーは `config` サブディレクトリ側が優先）、`logback-spring.xml` は `config` サブディレクトリ側だけが使われます。

#### 任意のディレクトリに置く場合

`jp.ecuacion.tool.code-generator.app-conf-dir` システムプロパティを指定してください（[既存の Tomcat 等にデプロイする場合](#既存の-tomcat-等にデプロイする場合)と同じプロパティです）。指定したディレクトリに置いた2つのファイルがどちらも読み込まれます。

```bash
java -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir \
     -jar ecuacion-tool-code-generator-web-x.x.x.war
```

上記の配置場所は、どれか1つだけを使ってください。混在させると、どのファイルの値が有効になっているか分かりにくくなります。

> **Note:** Spring Boot 標準の `-Dspring.config.location` も使えますが、推奨しません。Spring Boot のデフォルトの探索場所を丸ごと置き換えるため `jp.ecuacion.tool.code-generator.app-conf-dir` が効かなくなり、また `logback-spring.xml` は対象外なので `-Dlogging.config` でファイルを別途指定する必要があります。

### 既存の Tomcat 等にデプロイする場合

`${catalina.base}/app-conf/ecuacion-tool-code-generator/` に置いた `application.properties` と `logback-spring.xml` は、どちらも自動的に読み込まれます。前者はWAR自身が Spring Boot の `spring.config.import` でこの場所を宣言しており、後者は `ecuacion-splib-core` の `SplibEnvironmentPostProcessor` が同じディレクトリを見に行きます。Tomcat側での設定（`setenv.sh`、`context.xml` など）は一切不要です。

```
${CATALINA_HOME}/
└── app-conf/
    └── ecuacion-tool-code-generator/
        ├── application.properties
        └── logback-spring.xml
```

このディレクトリは事前に用意しておく必要はありません。存在しない場合は何も読み込まれず、WAR に埋め込まれた設定がそのまま使われます。デフォルトのパスにアプリ名が含まれているため、複数の ecuacion 製アプリを同じ Tomcat に同居させてもデフォルトのままでは設定ファイルが混ざりません。

`jp.ecuacion.tool.code-generator.app-conf-dir` システムプロパティにより、WAR を作り直さずにデプロイごとにこのディレクトリを差し替えることもできます（両ファイルまとめて差し替わります）。`${CATALINA_HOME}/bin/setenv.sh` で `CATALINA_OPTS` として指定するのが一般的です。

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.code-generator.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

> **Note:** `logging.config` が別の方法（`setenv.sh` で指定した、Tomcatプロセス全体に効く `-Dlogging.config` など）で既に設定されている場合はそちらが優先され、このディレクトリの `logback-spring.xml` は無視されます。
