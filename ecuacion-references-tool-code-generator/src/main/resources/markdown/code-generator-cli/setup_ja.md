## 1. JAR のダウンロード

[GitHub Releases](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/releases) から
最新の `ecuacion-tool-code-generator-cli-x.x.x.jar` をダウンロードします。

JAR は任意のディレクトリに配置してください。

## 2. DB項目定義書（Excel）の準備

[GitHub リポジトリの `excel-format/`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator/tree/main/excel-format) からテンプレートをダウンロードし、
プロジェクト名に合わせてリネームします。

JAR と同じディレクトリに `excel-format/` ディレクトリを作成し、
リネームしたファイルを配置します。

```
/path/to/workdir/
  ecuacion-tool-code-generator-cli-x.x.x.jar
  excel-format/
    db-column-definitions_fmt-v5.0.0-ja_myproject.xlsx
```

## システム要件

- JDK 21 以上
