ここでは、ローカルのファイルを別ディレクトリに移動する最もシンプルな例を通じてツールの基本的な使い方を説明します。

## 前提

- JAR ファイルと Excel 設定ファイルのサンプルを[セットアップ](page?id=housekeep-files/setup&lang=ja)に従って準備済みであること

## 手順

### 1. テスト用ファイルの作成

JAR を配置したディレクトリに移動し、そのディレクトリからの相対パスで作成します。

**Linux / macOS:**

```bash
mkdir -p hkf-test/from hkf-test/to
touch hkf-test/from/sample.txt
```

**Windows（PowerShell）:**

```powershell
New-Item -ItemType Directory -Force hkf-test/from, hkf-test/to
New-Item -ItemType File hkf-test/from/sample.txt
```

### 2. Excel 設定ファイルの編集

サンプルの Excel ファイルを開き、タスク設定シートを設定します（サーバ認証設定シートは SFTP を使わないため空のままで構いません）。

#### タスク設定シート

基本情報:

| タスクID | タスク名 | 処理パターン日本語名 | 処理パターン | 接続先サーバ |
| --- | --- | --- | --- | --- |
| task-1 | サンプル移動 | 移動 | MOVE | （空白） |

元パス関連:

| 元パス | 元パスディレクトリ | 元パス実施保留日数 | 元パス存在なし時処理 |
| --- | --- | --- | --- |
| hkf-test/from/sample.txt | false | 0 | ERROR |

先パス関連:

| 先パス | 先パスディレクトリ | 先パス存在時上書き | 先パス存在時処理 |
| --- | --- | --- | --- |
| hkf-test/to/ | true | true | IGNORE |

**ポイント:**
- `処理パターン`: `MOVE`（移動）を指定
- `元パス実施保留日数`: `0` にすると、経過日数に関わらず全ファイルが対象になります
- `先パスディレクトリ`: `true` にすると、先パスをディレクトリとして扱い、元ファイル名でそのまま配置します
- 元パス・先パスの相対パスは、`java -jar` を実行するカレントディレクトリ（手順 1 で `hkf-test` を作成したディレクトリ）が基準になります

### 3. application.properties の設定

JAR と同じ場所に `application.properties` を作成（または編集）し、先ほど設定した Excel ファイルのパスを指定します。

```properties
jp.ecuacion.tool.housekeep-files.excel-path=/path/to/your-settings.xlsx
```

### 4. ツールを実行する

```bash
java -jar ecuacion-tool-housekeep-files-x.x.x.jar
```

`hkf-test/from/sample.txt` が `hkf-test/to/sample.txt` に移動されれば成功です。
