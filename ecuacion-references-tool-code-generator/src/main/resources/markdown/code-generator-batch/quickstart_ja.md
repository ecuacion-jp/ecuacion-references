[セットアップ](/public/showMarkdown/page?id=code-generator-batch/setup&lang=ja) が完了していることを前提とします。

## 手順

### 1. DB項目定義書の最低限の設定

`excel-format/` ディレクトリ内の Excel ファイルを開き、
**「各種設定」シート**の以下の 2 項目を最低限設定します。

| 行 | 項目名 | 説明 | 例 |
| --- | --- | --- | --- |
| 8 | `SYSTEM_NAME` | プロジェクト識別名。生成ソースの親フォルダ名になる | `my-project` |
| 9 | `BASE_PACKAGE` | 生成コードの Java パッケージ共通部分 | `jp.example.myapp` |

その他の設定やシートの詳細は [DB項目定義書（Excel）の仕様](/public/showMarkdown/page?id=excel-format/overview&lang=ja) を参照してください。

### 2. バッチを実行する

JAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-code-generator-batch-x.x.x.jar
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
          repositoryimpl/
          bl/
          enums/
          converter/
          datatype/
      resources/
        messages_project/
          base/
            messages_base.properties
            messages_base_ja.properties
            messages_base_en.properties
```

### 4. 生成コードをプロジェクトに取り込む

出力された `src/main/java/` 以下を、対象プロジェクトの `src/main/java/` に配置します。
同様に、出力された `src/main/resources/` 以下も、対象プロジェクトの `src/main/resources/` に配置します。

---

## 複数ファイルを同時に処理する場合

`excel-format/` ディレクトリに複数の Excel ファイルを置くことができます。
バッチ実行時にディレクトリ内の全 xlsx ファイルが処理され、各 `SYSTEM_NAME` に対応した出力が生成されます。

## ログの確認

バッチ実行中のログはコンソールに出力されます。
エラーが発生した場合は、エラーメッセージで Excel のどの設定が問題かを確認できます。
