# code-generator-web クイックスタート

[セットアップ](/public/ja/article?id=code-generator-web/setup) が完了していることを前提とします。

## ローカルで起動して使う

### 1. アプリを起動する

WAR を配置したディレクトリで以下を実行します。

```bash
java -jar ecuacion-tool-code-generator-web-x.x.x.war
```

起動後、ブラウザで `http://localhost:8080` にアクセスします。

### 2. Excel をアップロードする

画面に表示されるファイル選択ボタンから DB項目定義書（xlsx）を選択し、
「Download」ボタンをクリックします。

アップロードできるのは `.xlsx` 形式のファイルのみです。

### 3. ZIP をダウンロードする

コード生成が完了すると `source.zip` のダウンロードが開始されます。

ZIP ファイルの中に生成された Java ソースファイルが含まれます。

```
source.zip
  <SYSTEM_NAME>/
    src/
      base/
        java/
          <パッケージパス>/base/
            entity/
            record/
            repository/
            ...
```

### 4. 生成コードをプロジェクトに取り込む

ZIP を展開し、`src/base/java/` 以下を対象プロジェクトの `src/base/java/` に配置します。

---

## エラーの確認方法

アップロードした Excel のフォーマットに問題がある場合は、画面上部にエラーメッセージが表示されます。
エラーメッセージを確認して DB項目定義書の設定を修正してください。

詳細なログはアプリケーションのログファイル（またはコンソール）で確認できます。
