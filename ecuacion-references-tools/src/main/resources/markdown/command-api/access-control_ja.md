## アクセス制御

`executeScript` へのアクセスを制御するプロパティが2つあります（実際のHTTP挙動については[APIスペック](page?id=command-api/api-spec&lang=ja)を参照）。どちらも埋め込みの`application.properties`には意図的に設定されていません。未設定のままだと、デフォルトに静かにフォールバックするのではなく、起動時に警告ログが出力されます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true`（未設定時に適用されるデフォルト）: `api/public/executeScript` へのアクセスは拒否（403）されます。APIを呼び出すには `api/key/executeScript`（`X-Api-Key` ヘッダによる認証が必須）を使用してください。`false`: `api/public/executeScript` が有効になります。信頼できる内部ネットワークでのみ使用してください。（どちらのエンドポイントでも、スクリプトごとに許可するHTTPメソッドはHTTPメソッドのプレフィックスで制御されます。詳細は[設定ファイル](page?id=command-api/config&lang=ja#許可するhttpメソッドの指定)を参照。） |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `api/key/executeScript` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイルのパス。[設定ファイル](page?id=command-api/config&lang=ja)のスクリプトパスと同様に `${ENV_VAR}` 展開に対応しています。省略可能 — 未設定時のデフォルトは下記の[ecuacion-tool-command-api-key.txt](#ecuacion-tool-command-api-key.txt)を参照。 |

例:

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

---

## ecuacion-tool-command-api-key.txt

`api/key/executeScript` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイル（[アクセス制御](#アクセス制御)参照）は、以下の優先順位で解決されます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `jp.ecuacion.tool.command-api.api-key-file-path` で指定したパス |
| 2 | カレントディレクトリの `config/ecuacion-tool-command-api-key.txt` |
| 3（低） | カレントディレクトリ直下の `ecuacion-tool-command-api-key.txt` |

優先度2・3は、`api-key-file-path` を一切設定しなくても使える、手軽な検証・ローカル用途向けのゼロコンフィグなデフォルトです。

> **Note:** `application.properties` / `ecuacion-tool-command-api.properties` と異なり、優先度2・3は JVM の作業ディレクトリ（`user.dir`）を基準にした単純なファイル存在チェックであり、Spring Boot の `spring.config.location` / `classpath:` 検索は経由しません。単独起動（`java -jar`）の場合は説明通りに動作しますが、既存の Tomcat 等にデプロイする場合（[設定ファイル](page?id=command-api/config&lang=ja)を参照）は `user.dir` が Tomcat 自身の作業ディレクトリになり、WAR や `app-conf` オーバーレイディレクトリとは無関係なため、このデフォルトは確実には機能しません。その場合は `api-key-file-path` を明示的に設定してください。

ファイルの内容はリクエストのたびに読み込まれ（前後の空白・改行はトリムされます）、ファイルの内容を差し替えるだけでアプリを再起動せずにキーをローテーションできます。

### カスタム ecuacion-tool-command-api-key.txt を使う場合

**方法 1 — `config/` サブディレクトリに配置:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── config/
    └── ecuacion-tool-command-api-key.txt
```

**方法 2 — WAR と同じディレクトリに直接配置:**

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
└── ecuacion-tool-command-api-key.txt
```

**方法 3 — パスを明示（本番環境ではこちらを推奨）:**

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

本番環境では、キーがアプリのデプロイ物と一緒にバンドル・バックアップ・上書きされないよう、デプロイディレクトリの外（シークレット用ボリュームや、より厳格なパーミッションの場所など）を指定するこの方法を推奨します。

### 比較方式: 平文 vs. bcrypt

デフォルトでは、ファイル内の全行が平文として比較されます。生のキーをアプリが読み取れる場所に一切保持したくない場合は、以下のプロパティを設定するとbcryptハッシュとして扱われます。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `PLAIN`（デフォルト）: 全行を平文キーとして直接比較します。`BCRYPT`: 全行をbcryptハッシュとして扱い、`BCryptPasswordEncoder.matches`で比較します。認識できない値が設定された場合は起動時に警告ログを出力し、`PLAIN`として扱います。 |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=BCRYPT
```

> **Note:** このモードは**ファイル全体**に適用されます（全行が平文、または全行がbcryptハッシュのいずれかである必要があります）。同一ファイル内で平文キーとbcryptハッシュを混在させることはサポートしていません。

キーのbcryptハッシュを生成する例:

```bash
htpasswd -nbBC 10 dummy "my-plain-key" | sed 's/^dummy://'
```

### 設定例

このファイルの中身は共有シークレットの値そのものだけです。キー名も `properties` 形式も不要です。1行につき1つのキーを書けます。複数行書いた場合、どの行のキーが提示されても受理されます。呼び出し元ごとに1つ発行しておけば、該当行を削除するだけで他のキーに影響を与えずに失効できます。

空行、および `#` で始まる行（前後の空白を除いた上で判定）はコメント行として無視されます。どのキーが誰向けかを書き添えておけば、失効させたいキーを探しやすくなります。

```
# key for client A
04f1befd704277c4b76afd01d655e6f1e8e36af9f74abe3a010d539ed3ac88cf

# key for client B
dcef325238aed9023681c8971d6df53080c536d0643692f9cad5a465118d5e79
```

キーの値自体は十分に長いランダムな値を推奨します。`PLAIN`（デフォルト）の場合は値をそのまま1行追記し、`BCRYPT` の場合は先にbcryptハッシュ化してから追記します（ハッシュ化コマンドの例は上記「比較方式: 平文 vs. bcrypt」を参照）。`X-Api-Key` ヘッダに設定すべき値は、ハッシュ化前の元の値です。

いずれのモードでも、ファイルの各行の値（前後の空白・改行はトリムされます）のいずれかと一致する `X-Api-Key` ヘッダを持つリクエストのみが `api/key/executeScript` を呼び出せます。
