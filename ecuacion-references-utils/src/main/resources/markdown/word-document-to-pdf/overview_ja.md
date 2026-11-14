`ecuacion-util-word-document-to-pdf` は、Word 文書（`.docx`。`.docm`・`.dotx`・`.dotm` も可。マクロは実行しない）から PDF を生成するユーティリティです。
Apache POI で文書を読み込み、Apache PDFBox で PDF を出力します。Microsoft Word は不要です。

契約書や利用規約など文章中心の文書を対象とし、Word が書き出した PDF と極力同じ見え方になるようにレイアウトします。
行・ページの区切り位置と各行の位置は、Mac 版 Word が書き出した PDF と突き合わせて検証しています。

## 主な特徴

- 段落・文字の書式、スタイル、章番号・箇条書き（例: `第1条`、`1.1`、`(1)`）
- 表、ヘッダー・フッター、ページ番号、セクションごとのページ設定
- 目次・相互参照：ページ番号を PDF のレイアウトから計算し直し、見出しへのリンクを張る
- 見出しを PDF のしおり（アウトライン）として出力
- 日本語の組版規則（禁則、句読点のぶら下げ、約物の詰め）
- OS のシステムフォントを自動検索、または任意の TTF フォントファイルを指定（`PdfGenerateOptions` で設定）
- パスワード付き Word ファイルの読み込み、出力 PDF へのパスワード設定

図形・テキストボックス・グラフ・脚注・段組み・縦書きには対応していません。
[対応機能](/public/showMarkdown/page?id=word-document-to-pdf/features&lang=ja) と
[注意事項](/public/showMarkdown/page?id=word-document-to-pdf/notes&lang=ja) を参照してください。

## 公開 API

| クラス | 役割 |
| --- | --- |
| `WordToPdfUtil` | PDF 生成のメインユーティリティ（`static` メソッド） |
| `PdfGenerateOptions` | フォント・パスワードなどのオプション（Builder パターン） |
| `PdfGenerateException` | PDF 生成時の例外の共通スーパークラス（`ecuacion-util-pdf-common` に属する） |
