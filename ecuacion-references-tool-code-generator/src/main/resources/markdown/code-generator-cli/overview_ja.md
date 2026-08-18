`ecuacion-tool-code-generator-cli` は、DB項目定義書（Excel）を入力として
Spring Boot + JPA アプリケーションの **base モジュール** の Java ソースコードを自動生成する
コマンドラインモジュールです。

## 動作の仕組み

1. `excel-format/` ディレクトリに配置した DB項目定義書（xlsx）を読み込む
2. `java -jar` で実行する
3. `products/<SYSTEM_NAME>/` ディレクトリに Java ソースが出力される
