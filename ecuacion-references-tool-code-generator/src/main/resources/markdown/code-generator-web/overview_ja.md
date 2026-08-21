`ecuacion-tool-code-generator-web` は、ブラウザから DB項目定義書（Excel）をアップロードすると生成された Java ソースコードを ZIP ファイルとしてダウンロードできる Web UI モジュールです。

## 動作の仕組み

1. ブラウザから DB項目定義書（xlsx）をアップロード
2. サーバ上の一時ディレクトリにファイルを保存
3. コード生成エンジンを実行
4. 生成物を `source.zip` として ZIP 化してクライアントに返す

※ ダウンロードした ZIP を展開し、`src/main/java/` および `src/main/resources/` 以下を対象プロジェクトに移動してコードとして使用します。
