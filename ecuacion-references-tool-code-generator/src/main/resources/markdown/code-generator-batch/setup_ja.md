## 1. JAR のダウンロード

[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/releases) から
最新の `ecuacion-tool-code-generator-batch-x.x.x.jar` をダウンロードします。

JAR は任意のディレクトリに配置してください。

## 2. DB項目定義書（Excel）の準備

[GitHub リポジトリの `excel-format/`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format) からテンプレートをダウンロードし、
プロジェクト名に合わせてリネームします。

JAR と同じディレクトリに `excel-format/` ディレクトリを作成し、
リネームしたファイルを配置します。

```
/path/to/workdir/
  ecuacion-tool-code-generator-batch-x.x.x.jar
  excel-format/
    db-definition-book-fmt-v4.11.0_myproject_ja.xlsx
```

DB項目定義書の編集方法は [DB項目定義書（Excel）の仕様](page?id=excel-format/overview&lang=ja) を参照してください。

## システム要件

- JDK 21 以上
