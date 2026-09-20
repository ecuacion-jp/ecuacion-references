## アクセス制御

`execute` へのアクセスを制御するプロパティが2つあります（実際のHTTP挙動については[APIスペック](page?id=command-api/api-spec&lang=ja)を参照）。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-required` | boolean | `true`（未設定時に適用されるデフォルト）: `api/public/execute` へのアクセスは拒否（403）されます。APIを呼び出すには `api/key/execute`（`X-Api-Key` ヘッダによる認証が必須）を使用してください。<br>`false`: `api/public/execute` が有効になります。信頼できる内部ネットワークでのみ使用してください。（どちらのエンドポイントでも、スクリプトごとに許可するHTTPメソッドはHTTPメソッドのプレフィックスで制御されます。詳細は[設定ファイル](page?id=command-api/config&lang=ja#許可するhttpメソッドの指定)を参照。） |
| `jp.ecuacion.tool.command-api.api-key-file-path` | String | `api/key/execute` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイルのパス。[設定ファイル](page?id=command-api/config&lang=ja)のスクリプトパスと同様に `${ENV_VAR}` 展開に対応しています。省略可能 — 未設定時のデフォルトは下記の[ecuacion-tool-command-api-keys.txt](#ecuacion-tool-command-api-keys-txt)を参照。 |

例:（`application.properties` に追記）

```properties
jp.ecuacion.tool.command-api.api-key-required=true
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

---

## ecuacion-tool-command-api-keys.txt

`api/key/execute` の `X-Api-Key` ヘッダの値と照合する共有シークレットが書かれたファイル（[アクセス制御](#アクセス制御)参照）は、以下の優先順位で解決されます。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `jp.ecuacion.tool.command-api.api-key-file-path` で指定したパス |
| 2 | カレントディレクトリの `config/ecuacion-tool-command-api-keys.txt` |
| 3（低） | カレントディレクトリ直下の `ecuacion-tool-command-api-keys.txt` |

優先度2・3は、`api-key-file-path` を一切設定しなくても使える、手軽な検証・ローカル用途向けのゼロコンフィグなデフォルトです。

```
/your-work-dir/
├── ecuacion-tool-command-api-x.x.x.war
├── ecuacion-tool-command-api-keys.txt   ← 優先度3
└── config/
    └── ecuacion-tool-command-api-keys.txt   ← 優先度2（こちらが優先）
```

特定のパスを明示したい場合は、`application.properties` に以下を追記します。

```properties
jp.ecuacion.tool.command-api.api-key-file-path=${HOME}/secrets/command-api-key.txt
```

本番環境では、キーがアプリのデプロイ物と一緒にバンドル・バックアップ・上書きされないよう、デプロイディレクトリの外（シークレット用ボリュームや、より厳格なパーミッションの場所など）を明示的に指定することを推奨します。

> **Note:** 既存の Tomcat 等にデプロイする場合（[設定ファイル](page?id=command-api/config&lang=ja)を参照）は `user.dir` が Tomcat 自身の作業ディレクトリになり、WAR や `app-conf` オーバーレイディレクトリとは無関係なため、このデフォルトは確実には機能しません。その場合は `api-key-file-path` を明示的に設定してください。

ファイルの内容はリクエストのたびに読み込まれ（前後の空白・改行はトリムされます）、ファイルの内容を差し替えるだけでアプリを再起動せずにキーをローテーションできます。

### 比較方式: 平文 vs. bcrypt

デフォルトでは、ファイル内の全行がbcryptハッシュとして比較されます。代わりに平文キーを使いたい場合（**非推奨** — 生のキーがファイルアクセス権を持つ相手には誰でも読める状態で保管されることになります）は、`application.properties` に以下のプロパティを設定してください。

| プロパティ | 型 | 説明 |
| --- | --- | --- |
| `jp.ecuacion.tool.command-api.api-key-comparison-mode` | String | `BCRYPT`（デフォルト）: 全行をbcryptハッシュとして扱い、`BCryptPasswordEncoder.matches`で比較します。<br>`PLAIN`: 全行を平文キーとして直接比較します。認識できない値が設定された場合は例外がスローされ、修正するまで `api/key/execute` へのリクエストがすべて失敗します。 |

```properties
jp.ecuacion.tool.command-api.api-key-comparison-mode=PLAIN
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
$2y$10$grqMx6p/rxba/w46AociPeuyEjQ978WNGcukQmvU/y3j4GQ.MY8lu

# key for client B
$2y$10$wfghrHt4Y708ET1T359xk.igcZL2Ep9RjS3dwG8olNCOZsmLCI23W
```

キーの値自体は十分に長いランダムな値を推奨します。`BCRYPT`（デフォルト）の場合は先にbcryptハッシュ化してから追記し（ハッシュ化コマンドの例は上記「比較方式: 平文 vs. bcrypt」を参照）、`PLAIN` の場合は値をそのまま1行追記します。`X-Api-Key` ヘッダに設定すべき値は、ハッシュ化前の元の値です。

いずれのモードでも、ファイルの各行の値（前後の空白・改行はトリムされます）のいずれかと一致する `X-Api-Key` ヘッダを持つリクエストのみが `api/key/execute` を呼び出せます。
