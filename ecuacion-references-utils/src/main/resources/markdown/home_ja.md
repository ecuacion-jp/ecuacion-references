こちらは ecuacion-utils（モジュール名：`ecuacion-util-xxx`）のチュートリアルページです。
Excel ファイルの読み書きや PDF 生成など、実務でよく使うユーティリティ機能の使い方をまとめています。

## ecuacion-utils とは

ecuacion-utils は、アプリケーション開発で必要となる汎用ユーティリティ機能を提供するライブラリ群です。
現在は Excel 操作に特化した 2 つのモジュールで構成されています。

## モジュール構成

| モジュール名 | 役割 |
| --- | --- |
| `ecuacion-util-excel-table` | Excel ファイルのテーブルデータを読み書きするユーティリティ |
| `ecuacion-util-excel-report-to-pdf` | Excel ファイルを PDF に変換するユーティリティ |

詳細はナビゲーションの各ページを参照してください。

---

## セットアップ

`pom.xml` に必要なモジュールを追加します。

```xml
<dependency>
    <groupId>jp.ecuacion.util</groupId>
    <artifactId>ecuacion-util-excel-table</artifactId>
    <version>（バージョン）</version>
</dependency>
```

各モジュールで追加が必要な依存など、詳細は以下のページを参照してください。

- `ecuacion-util-excel-table`: [セットアップ](page?id=excel-tables/setup&lang=ja)
- `ecuacion-util-excel-report-to-pdf`: [セットアップ](page?id=excel-report-to-pdf/setup&lang=ja)
