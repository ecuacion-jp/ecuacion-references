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

> **Warning:** `output-dir` は生成開始前に**再帰的に全削除**されます。既存の重要なディレクトリ（ホームディレクトリ等）を誤って指定しないよう注意してください。
