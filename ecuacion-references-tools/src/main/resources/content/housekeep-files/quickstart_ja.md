# housekeep-files クイックスタート

ここでは、ローカルのファイルを別ディレクトリに移動する最もシンプルな例を通じて
ツールの基本的な使い方を説明します。

## 前提

- JAR ファイルと Excel 設定ファイルのサンプルを[セットアップ](/public/showMarkdown/page?id=housekeep-files/setup&lang=ja)に従って準備済みであること

## 手順

### 1. テスト用ファイルの作成

```bash
mkdir -p /tmp/hkf-test/from
mkdir -p /tmp/hkf-test/to
touch /tmp/hkf-test/from/sample.txt
```

### 2. Excel 設定ファイルの編集

サンプルの Excel ファイルを開き、以下の 3 つのシートを設定します。

#### 基礎情報設定シート

| 項目 | 値 |
| --- | --- |
| env-name | my-system |

#### タスク設定シート

| タスクID | タスク名 | 処理パターン日本語名 | 処理パターン | 接続先サーバ | 元パス | 元パスがディレクトリ | 元パス処理実施対象経過期間単位 | 元パス処理実施対象経過期間値 | 元パス存在なし時処理 | 先パス | 先パスがディレクトリ | 先パス存在時上書き | 先パス存在時処理 | options |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| task-1 | サンプル移動 | 移動 | MOVE | （空白） | /tmp/hkf-test/from/sample.txt | false | DAY | 0 | ERROR | /tmp/hkf-test/to/ | true | true | IGNORE | （空白） |

**ポイント:**
- `処理パターン`: `MOVE`（移動）を指定
- `元パス処理実施対象経過期間値`: `0` にすると、経過日数に関わらず全ファイルが対象になります
- `先パスがディレクトリ`: `true` にすると、先パスをディレクトリとして扱い、元ファイル名でそのまま配置します

#### パス設定シート

今回はパス変数を使わないため、空のままで構いません。

### 3. ツールを実行する

```bash
java -jar ecuacion-tool-housekeep-files-x.x.x.jar excelPath=/path/to/your-settings.xlsx
```

`/tmp/hkf-test/from/sample.txt` が `/tmp/hkf-test/to/sample.txt` に移動されれば成功です。

---

## パス変数を使う

パスが長い場合や共通部分を再利用したい場合は、**パス設定シート**にパス変数を登録し、
タスク設定シートの元パス・先パスで `${VAR_NAME}` 形式で参照できます。

#### パス設定シート

| パス変数名 | パス値 |
| --- | --- |
| BASE_DIR | /tmp/hkf-test |

#### タスク設定シートの元パス・先パス

```
${BASE_DIR}/from/sample.txt   # 元パス
${BASE_DIR}/to/               # 先パス
```

パス変数名は大文字・数字・アンダースコアのみ使用できます（例: `BASE_DIR`, `LOG_PATH`）。
