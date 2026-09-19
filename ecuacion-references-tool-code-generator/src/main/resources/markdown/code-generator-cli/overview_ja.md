`ecuacion-tool-code-generator-cli` は、DB項目定義書（Excel）を入力として
Spring Boot + JPA アプリケーションの **base モジュール** の Java ソースコードを自動生成するコマンドラインモジュールです。

## 動作の仕組み

1. 読み込む DB項目定義書（xlsx）を `input-file` プロパティで指定し、`java -jar` で実行する
2. `products/<SYSTEM_NAME>/` ディレクトリに Java ソースが出力される

※ 出力されたファイルは、対象プロジェクトの `src/main/java/` および `src/main/resources/` 以下に移動し、コードとして使用します。
