[セットアップ](page?id=code-generator-cli/setup&lang=ja) が完了していることを前提とします。

## 手順

### 1. DB項目定義書の最低限の設定

[DB項目定義書 クイックスタート](page?id=excel-format/quickstart&lang=ja) を参照し、実行前の最低限の設定を行ってください。
この内容は `code-generator-cli` と `code-generator-web` で共通です。

### 2. CLI を実行する

JAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-code-generator-cli-x.x.x.jar
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
        （各種設定シートで追加言語を設定した場合、messages_base_en.properties のような
        _<言語> 付きファイルも追加で生成される）
```

### 4. 生成コードをプロジェクトに取り込む

出力された `src/main/java/` 以下を、対象プロジェクトの `src/main/java/` に配置します。
同様に、出力された `src/main/resources/` 以下も、対象プロジェクトの `src/main/resources/` に配置します。

具体的な取り込み例は、[qiita-data-viewer](https://github.com/ecuacion-jp/qiita-data-viewer) などをご参照ください。
