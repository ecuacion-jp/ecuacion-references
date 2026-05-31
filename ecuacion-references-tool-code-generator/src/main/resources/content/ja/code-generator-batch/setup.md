# code-generator-batch セットアップ

`code-generator-batch` は Maven で管理されるプロジェクトです。
公開 Maven リポジトリへの publish は行っていないため、リポジトリを直接クローンして使用します。

## 1. リポジトリのクローン

```bash
git clone https://github.com/ecuacion-jp/ecuacion-tool-code-generator.git
```

クローン先は、他の ecuacion リポジトリと同じ親ディレクトリに配置してください。
バッチモジュールは内部で親モジュール（`ecuacion-splib` など）を相対パスで参照しているため、
**他の ecuacion リポジトリも同じ親ディレクトリに配置**されている必要があります。

```
/path/to/dev/
  ecuacion-tool-code-generator/   ← クローン先
  ecuacion-splib/                 ← 別途クローン必要
  ecuacion-lib/                   ← 別途クローン必要
```

## 2. DB項目定義書（Excel）の準備

`ecuacion-tool-code-generator-batch/ecuacion-tool-code-generator-excel-format/` ディレクトリに
既存のサンプルファイルが含まれています。

新規プロジェクト向けのファイルを作成する場合は、既存の Excel ファイルをコピーしてリネームします。

```
DB項目定義書(fmt-v4.10.0)_cloud-server-manager.xlsx  ← コピー元の例
DB項目定義書(fmt-v4.10.0)_myproject.xlsx             ← リネーム後
```

DB項目定義書の編集方法は [DB項目定義書（Excel）の仕様](/public/ja/article?id=excel-format/overview) を参照してください。

## 3. ビルド

リポジトリのルートでプロジェクト全体をビルドします。

```bash
cd ecuacion-tool-code-generator
mvn clean install -DskipTests
```

## システム要件

- JDK 21 以上
- Maven 3.x
