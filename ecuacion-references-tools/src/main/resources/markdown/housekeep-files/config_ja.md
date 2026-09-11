このページでは、`ecuacion-tool-housekeep-files` が使用する2つの設定ファイル — `application.properties` ・ `logback-spring.xml` — とその配置方法について説明します。

## application.properties

本ファイルの読み取りはhousekeep-files独自の仕組みではなく、Spring Bootの機能です。`application.yml` / `application.yaml` でも全く同じように動作します。網羅的な説明は[Spring Boot公式リファレンス](https://docs.spring.io/spring-boot/reference/features/external-config.html)を参照してください。配置場所については下記の[ファイルの配置](#ファイルの配置)を参照してください。

### 設定できる項目

| プロパティ | 必須 | デフォルト | 説明 |
| --- | --- | --- | --- |
| `jp.ecuacion.tool.housekeep-files.excel-path` | ○ | — | 実行するExcel設定ファイルのパス |
| `jp.ecuacion.tool.housekeep-files.system-name` | — | —<br>（未設定時は非表示） | ジョブ開始・終了ログ、警告メールの件名に表示するシステム名。<br>未設定の場合はその部分が省略されます |
| `jp.ecuacion.tool.housekeep-files.sftp.strict-host-key-checking` | — | `true`<br>（チェック有効） | `false` にするとSFTP接続時のホスト鍵検証を無効化します。<br>中間者攻撃を検知できなくなるため、使い捨てのローカル検証など以外では<br>無効化しないでください |
| `jp.ecuacion.tool.housekeep-files.sftp.connect-timeout-millis` | — | `30000`<br>（30秒） | SFTPセッション・チャネルの接続タイムアウト（ミリ秒）。応答しないサーバによってバッチが無期限にハングするのを防ぎます |
| `jp.ecuacion.tool.housekeep-files.unzip.max-total-bytes` | — | `10737418240`<br>（10GiB） | `UNZIP_*` タスク1件が書き込む展開後合計サイズの上限（バイト）。小さいアーカイブが巨大なサイズに展開されてディスクを圧迫する「zip爆弾」対策です。上限を超える場合はタスクが失敗します |

```properties
jp.ecuacion.tool.housekeep-files.excel-path=/path/to/your-settings.xlsx
```

### パス変数の使用

タスク設定シートの「元パス」「先パス」で使用する `${VAR_NAME}` 形式のパス変数は、
`application.properties` で定義できます（`application.yml` 等、Spring Boot が解決できる
設定源であれば OS 環境変数・JVM システムプロパティ・コマンドライン引数でも構いません）。

```properties
BASE_DIR=/data/myapp
```

タスク設定シートでは `${BASE_DIR}` のように参照します。

> **Note:** `DATE` / `DATETIME` / `TIMESTAMP` / `HOSTNAME` は組み込み変数として予約されており、
> 同名のプロパティを設定しても組み込み変数の値が優先されます。

---

## logback-spring.xml

Logback の設定ファイルです。もちろん自由に設定できますが、splib が appenders / loggers 用の
built-in な include リソースを用意しているため、それを使うと以下のように端的に書けます。

### 設定例

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="logback-spring-app-common.xml" />

    <property name="log-dir" value="/path/to/logs/directory" />
    <include resource="logback-spring-appenders-batch.xml" />

    <property name="loglevel-jp.ecuacion" value="INFO" />
    <property name="loglevel-root" value="INFO" />
    <include resource="logback-spring-loggers-batch-to-console-and-files.xml" />
</configuration>
```

---

## ファイルの配置

`application.properties` の配置ルールは、housekeep-files独自ではなく素のSpring Bootの外部設定機能です。`logback-spring.xml` も*ほぼ*同じですが、こちらはSpring Boot自体には同等の探索機能が無いため、`ecuacion-splib-core` が拡張として提供しています。いずれも配置ルールの考え方は共通で、システムプロパティによるパス指定・`config`サブディレクトリ・カレントディレクトリの3段階です（後者2つはいずれもアプリの起動元のカレントディレクトリ基準で解決されます）。

| ファイル | 優先度1（高） | 優先度2 | 優先度3（低） |
| --- | --- | --- | --- |
| `application.properties` | `-Dspring.config.location=...` で指定したパス | `config` サブディレクトリ | 同じディレクトリ |
| `logback-spring.xml` | `-Dlogging.config=...` で指定したパス | `config` サブディレクトリ | 同じディレクトリ |

例として `application.properties` の場合（`logback-spring.xml` も同じ考え方で、ファイル名を読み替えるだけです）:

```
/your-work-dir/
├── ecuacion-tool-housekeep-files-x.x.x.jar
├── application.properties   ← 優先度3
└── config/
    └── application.properties   ← 優先度2（こちらが優先）
```

`java -jar` は `/your-work-dir` から実行する必要があります。「同じディレクトリ」「`config` サブディレクトリ」はいずれも、JARファイルが置かれている場所ではなく、アプリを起動したカレントディレクトリを基準に解決されます。

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/config/ \
     -jar ecuacion-tool-housekeep-files-x.x.x.jar
```

> **Note:** `-Dspring.config.location` は**ディレクトリ**を指定するものです。単一ファイルを指定するとそのファイルだけが読み込まれます。`logback-spring.xml` は専用のシステムプロパティ（上の表の `-Dlogging.config`）を使い、`-Dspring.config.location` の影響は受けません。
