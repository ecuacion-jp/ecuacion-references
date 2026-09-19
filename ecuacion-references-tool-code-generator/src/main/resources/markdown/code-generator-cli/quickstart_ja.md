[セットアップ](page?id=code-generator-cli/setup&lang=ja) が完了していることを前提とします。

## 手順

### 1. DB項目定義書の最低限の設定

[DB項目定義書 クイックスタート](page?id=excel-format/quickstart&lang=ja) を参照し、実行前の最低限の設定を行ってください。
この内容は `code-generator-cli` と `code-generator-web` で共通です。

### 2. CLI を実行する

JAR を配置したディレクトリで、`input-file` プロパティに読み込む Excel ファイルを指定して以下を実行します
（複数ファイルを指定する場合はカンマ区切りで指定します）。

```bash
java -jar ecuacion-tool-code-generator-cli-x.x.x.jar \
     --jp.ecuacion.tool.code-generator.input-file=excel-format/db-column-definitions_fmt-v5.0.0-ja_myproject.xlsx
```

### 3. 出力を確認する

実行後、以下のディレクトリに Java ソースおよびリソースファイルが出力されます。

```
products/<SYSTEM_NAME>/
```

出力されるディレクトリ構造の例:

```
products/my-project/
  src/
    main/
      java/
        jp/example/myapp/base/
          entity/
          record/
          repository/
          bl/
          enums/
          converter/
          datatype/
      resources/
        item_names_base.properties
        messages_base.properties
```
