## application.properties

Spring Boot の外部設定ファイルは以下の優先順位で読み込まれます（上位が下位を上書き）。

| 優先度 | 場所 |
| --- | --- |
| 1（高） | `-Dspring.config.location=...` で指定したパス |
| 2 | JAR と同じディレクトリの `config/application.properties` |
| 3（低） | JAR と同じディレクトリの `application.properties` |

> **Note:** 作成する `application.properties` には、変更したい設定項目だけを記述すれば十分です。記述しなかった項目は、以下に記載のデフォルト値のまま動作します。

### カスタム application.properties を使う場合

JAR と同じディレクトリ、または `config/` サブディレクトリに配置します。

```
/your-work-dir/
├── ecuacion-tool-code-generator-cli-x.x.x.jar
├── application.properties          ← 埋め込み設定を上書き
└── config/
    └── application.properties      ← こちらでも可（優先度高）
```

特定のパスを明示したい場合はシステムプロパティで指定します。

```bash
java -Dspring.config.location=file:/path/to/your/application.properties \
     -jar ecuacion-tool-code-generator-cli-x.x.x.jar
```

### 設定できる項目

追加の設定は `application.properties` に記述してください。

#### 入出力ディレクトリ

| プロパティ | 説明 | デフォルト |
| --- | --- | --- |
| `input-dir` | Excel ファイルを置くディレクトリ。カンマ区切りで複数指定可能（例: `./dir1,./dir2`） | `./excel-format` |
| `output-dir` | 生成ソースの出力先 | `./products/` |

同じ `input-dir` に複数の Excel ファイルを直接置くこともできます。実行時にディレクトリ内の全 xlsx ファイルが処理され、
各 `SYSTEM_NAME` に対応した出力が生成されます。

> **Note:** `code-generator-web` と異なり、CLI の jar には失敗時のメール通知機能は組み込まれていません。
> `spring.mail.*` / `jp.ecuacion.splib.mail.*` の設定もここでは不要です。スクリプトやスケジューラから実行する場合は、
> プロセスの終了コード（成功時 `0` / 失敗時 `1`）を確認し、必要であれば呼び出し側のラッパースクリプトで
> 通知処理を実装してください。コンソールでのエラー表示については以下の「トラブルシューティング」を参照してください。

---

## トラブルシューティング

Excel の設定内容に不備があると実行が中断され、どの項目が問題かを示す簡潔な箇条書きのメッセージが表示されます。
想定外のエラーが発生した場合は簡潔なメッセージのみが表示されます。`--verbose` オプション付きで再実行すると、
スタックトレース全体も表示されます。
