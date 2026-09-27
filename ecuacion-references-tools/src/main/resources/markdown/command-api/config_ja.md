このページでは、`ecuacion-tool-command-api` が使用する3つの設定ファイル — `application.properties` ・ `ecuacion-tool-command-api-scripts.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはcommand-api独自の仕組みではなく、Spring Bootの機能です。`application.yml` / `application.yaml` でも全く同じように動作します。網羅的な説明は[Spring Boot公式リファレンス](https://docs.spring.io/spring-boot/reference/features/external-config.html)を参照してください。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### アクセス制御

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `api/public/execute` を無効化し、代わりに `api/key/execute` と有効な `X-Api-Key` ヘッダを必須にするかどうか。デフォルト: `true`。 |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `X-Api-Key` と照合する共有シークレットが書かれたファイルのパス。 |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | api-key ファイルの各行を平文（`PLAIN`）またはbcryptハッシュ（`BCRYPT`）のどちらとして比較するか。デフォルト: `BCRYPT`。 |

各プロパティの詳細、およびapi-keyファイル自体の管理方法については[アクセス制御](page?id=command-api/access-control&lang=ja)を参照してください。

#### スクリプト実行

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.script-timeout-seconds` | long | スクリプトの実行を待つ最大秒数。超過すると強制終了され、`504 Gateway Timeout` を返します。デフォルト: `60`。 |
| `jp.ecuacion.tool.command-api.script-max-output-bytes` | long | レスポンスに含める標準出力・標準エラー出力の上限バイト数（stdout・stderrそれぞれ独立に適用）。超過した時点以降の行は破棄され、レスポンスの `stdoutTruncated` / `stderrTruncated` が `true` になります。スクリプト自体は最後まで実行されます。デフォルト: `1048576`（1MiB）。 |

ハングするスクリプト（またはハングさせるパラメータ）でワーカースレッドが専有され続けるのを防ぐための設定です。また、大量の出力（大きなファイルの `cat` や無限出力ループ等）でJVMヒープを圧迫するのも防ぎます。

#### メール通知（エラー発生時）

システムエラー発生時に `SplibMailUtil` で管理者へメール通知します。`spring.mail.*` / `jp.ecuacion.splib.mail.*` の各プロパティ・デフォルト値・設定例（Gmail含む）は
[SplibMailUtil](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=core/util/mail-util&lang=ja)を参照してください。

---

## ecuacion-tool-command-api-scripts.properties

スクリプト登録用の設定ファイルです。配置ルールは `application.properties` と同じです（下記の[ファイルの配置](#ファイルの配置)を参照）。

### 設定できる項目

#### スクリプトの登録

`ecuacion-tool-command-api-scripts.properties` に以下の形式でスクリプトを登録します。

```properties
<スクリプトID>=<スクリプトの絶対パス>
```

例:

```properties
script.say-hello=/opt/scripts/sayHello.sh
script.daily-batch=/opt/scripts/dailyBatch.sh
```

#### 許可するHTTPメソッドの指定

スクリプト定義の値の先頭に `GET:` / `POST:` / `ALL:`（大文字小文字を区別しない）を付けると、`api/public/execute`（有効化されている場合）・`api/key/execute` の両方で、そのスクリプトを呼び出せるHTTPメソッドを制限できます。プレフィックスを省略した場合は `POST` のみ許可されます。

```properties
script.say-hello=GET:/opt/scripts/sayHello.sh
script.daily-batch=POST:/opt/scripts/dailyBatch.sh
script.status-check=ALL:/opt/scripts/statusCheck.sh
script.legacy-job=/opt/scripts/legacyJob.sh
```

| スクリプトID | 許可されるメソッド |
| --- | --- |
| `script.say-hello` | `GET` のみ |
| `script.daily-batch` | `POST` のみ |
| `script.status-check` | `GET` ・ `POST` いずれも |
| `script.legacy-job` | `POST` のみ（プレフィックス省略時のデフォルト） |

> **Note:** このプレフィックスは `api/public/execute` と `api/key/execute` の両方に同じルールで適用されます。両エンドポイントの違いはこのメソッド制限ではなく、`X-Api-Key` ヘッダによる認証が必須かどうかだけです。

#### 変数参照の使用

スクリプトのパスに `${VAR_NAME}` 形式の変数参照を使用できます。値は `application.properties`・
OS 環境変数・JVM システムプロパティなど、Spring Boot の `Environment` が解決できるあらゆる設定源
から取得されます。

```properties
script.say-hello=${SCRIPT_DIR}/sayHello.sh
```

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

この3つのファイルをどこに置くかは、`ecuacion-tool-command-api` の起動方法によって異なります — 起動方法自体については[起動方法](page?id=command-api/launch-patterns&lang=ja)を参照してください。

> **Note:** `application.properties` の変更は、`ecuacion-splib-rest` の `clearPropertiesCache` エンドポイント（詳細は[運用系エンドポイント](https://references.ecuacion.jp/ecuacion-references-splib/public/showMarkdown/page?id=rest/operational-endpoints&lang=ja)を参照）を呼ぶことでアプリを再起動せずに反映できます。一方 `ecuacion-tool-command-api-scripts.properties` の変更はこのエンドポイントでは反映**されません**（`ContextRefresher` は `spring.config.name` の1番目＝プライマリしか確実にはリロードせず、このファイルは2番目以降の設定名として登録されているため）— スクリプト登録の変更を反映するにはアプリの再起動が必要です。

### 単独で起動する場合

#### オプションを指定しない場合

3つのファイルを、アプリを起動したカレントディレクトリの直下、またはその `config` サブディレクトリに置いてください。オプションの指定は不要です。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    ├── application.properties
    ├── ecuacion-tool-command-api-scripts.properties
    └── logback-spring.xml
```

> **Note:** 「カレントディレクトリ」は、`java -jar` を実行した際のカレントディレクトリ（`user.dir`）です。WAR と同じディレクトリに `cd` してから起動する運用であれば、実質的に「WAR と同じディレクトリ」基準になります。

両方の場所にファイルがある場合、2つの `.properties` ファイルはキー単位でマージされ（重複したキーは `config` サブディレクトリ側が優先）、`logback-spring.xml` は `config` サブディレクトリ側だけが使われます。

#### 任意のディレクトリに置く場合

`jp.ecuacion.tool.command-api.app-conf-dir` システムプロパティを指定してください（[既存の Tomcat 等にデプロイする場合](#既存の-tomcat-等にデプロイする場合)と同じプロパティです）。指定したディレクトリに置いた3つのファイルがすべて読み込まれます。

```bash
java -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir \
     -jar ecuacion-tool-command-api-x.x.x.war
```

上記の配置場所は、どれか1つだけを使ってください。混在させると、どのファイルの値が有効になっているか分かりにくくなります。

> **Note:** Spring Boot 標準の `-Dspring.config.location` も使えますが、推奨しません。Spring Boot のデフォルトの探索場所を丸ごと置き換えるため `jp.ecuacion.tool.command-api.app-conf-dir` が効かなくなり、また `logback-spring.xml` は対象外なので `-Dlogging.config` でファイルを別途指定する必要があります。使う場合は**ディレクトリ**（末尾 `/`）を指定してください。単一ファイルを指定するとそのファイルだけが読み込まれ、`ecuacion-tool-command-api-scripts.properties` は読み込まれません。

### 既存の Tomcat 等にデプロイする場合

`${catalina.base}/app-conf/ecuacion-tool-command-api/` に置いた `application.properties`・`ecuacion-tool-command-api-scripts.properties`・`logback-spring.xml` は、すべて自動的に読み込まれます。前者2つはWAR自身が Spring Boot の `spring.config.import` でこの場所を宣言しており、`logback-spring.xml` は `ecuacion-splib-core` の `SplibEnvironmentPostProcessor` が同じディレクトリを見に行きます。Tomcat側での設定（`setenv.sh`、`context.xml` など）は一切不要です。

```
${CATALINA_HOME}/
└── app-conf/
    └── ecuacion-tool-command-api/
        ├── application.properties
        ├── ecuacion-tool-command-api-scripts.properties
        └── logback-spring.xml
```

このディレクトリは事前に用意しておく必要はありません。存在しない場合は何も読み込まれず、WAR に埋め込まれた設定がそのまま使われます。デフォルトのパスにアプリ名が含まれているため、複数の ecuacion 製アプリを同じ Tomcat に同居させてもデフォルトのままでは設定ファイルが混ざりません。

`jp.ecuacion.tool.command-api.app-conf-dir` システムプロパティにより、WAR を作り直さずにデプロイごとにこのディレクトリを差し替えることもできます（3ファイルまとめて差し替わります）。`${CATALINA_HOME}/bin/setenv.sh` で `CATALINA_OPTS` として指定するのが一般的です。

```bash
CATALINA_OPTS="$CATALINA_OPTS -Djp.ecuacion.tool.command-api.app-conf-dir=/path/to/config/dir"
export CATALINA_OPTS
```

> **Note:** `logging.config` が別の方法（`setenv.sh` で指定した、Tomcatプロセス全体に効く `-Dlogging.config` など）で既に設定されている場合はそちらが優先され、このディレクトリの `logback-spring.xml` は無視されます。
