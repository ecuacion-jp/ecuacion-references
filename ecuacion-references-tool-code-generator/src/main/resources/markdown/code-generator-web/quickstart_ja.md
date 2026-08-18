[セットアップ](page?id=code-generator-web/setup&lang=ja) が完了していることを前提とします。

## ローカルで起動して使う

### 1. DB項目定義書の最低限の設定

[DB項目定義書 クイックスタート](page?id=excel-format/quickstart&lang=ja) を参照し、アップロード前の最低限の設定を行ってください。
この内容は `code-generator-cli` と `code-generator-web` で共通です。

### 2. アプリを起動する

WAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

起動後、ブラウザで `http://localhost:8080` にアクセスします。

### 3. Excel をアップロードする

画面に表示されるファイル選択ボタンから DB項目定義書（xlsx）を選択し、
「Download」ボタンをクリックします。

アップロードできるのは `.xlsx` 形式のファイルのみです。

### 4. ZIP をダウンロードする

コード生成が完了すると `source.zip` のダウンロードが開始されます。

ZIP ファイルの中に生成された Java ソースファイルおよびリソースファイルが含まれます。

```
source.zip
  <SYSTEM_NAME>/
    src/
      main/
        java/
          <パッケージパス>/base/
            entity/
            record/
            repository/
            ...
        resources/
          item_names_base.properties
          messages_base.properties
          （各種設定シートで追加言語を設定した場合、messages_base_en.properties のような
          _<言語> 付きファイルも追加で生成される）
```

### 5. 生成コードをプロジェクトに取り込む

ZIP を展開し、`src/main/java/` 以下を対象プロジェクトの `src/main/java/` に配置します。
同様に、`src/main/resources/` 以下も対象プロジェクトの `src/main/resources/` に配置します。

---

## エラーの確認方法

アップロードした Excel のフォーマットに問題がある場合は、画面上部にエラーメッセージが表示されます。
エラーメッセージを確認して DB項目定義書の設定を修正してください。

詳細なログはアプリケーションのログファイル（またはコンソール）で確認できます。
