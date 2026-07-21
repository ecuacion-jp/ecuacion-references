`SplibApiKeyComparisonMode` は、
[`SplibApiKeyExpectedValueProvider`](/public/showMarkdown/page?id=rest/security/api-key/overview&lang=ja)
が返す値と、クライアントが提示する `X-Api-Key` ヘッダーの値をどう比較するかを選択します。
アプリケーション全体に対して以下のように設定します。

```properties
jp.ecuacion.splib.rest.api-key.mode=PLAIN
```

単一のアプリケーションは一貫して 1 つのモードを使うことを前提としており、エンドポイント単位・キー単位での
切り替えはできません。デフォルトは `PLAIN` です。

## `PLAIN`

Provider はキーそのものを返します。提示された値と（定数時間で）直接比較されます。

## `HASH`

Provider はキーそのものではなく、キーの小文字16進表記の SHA-256 ダイジェストを返します。
これにより、生のキーがアプリケーションから読み取り可能な場所に一切保存されなくなります。
提示されたヘッダー値も同じ方法でハッシュ化してから比較されます。

保存すべき値を求めるには、コマンドラインで生のキーをハッシュ化します。

```bash
# macOS
echo -n "your-api-key-here" | shasum -a 256

# Linux
echo -n "your-api-key-here" | sha256sum

# クロスプラットフォーム（OpenSSL）
echo -n "your-api-key-here" | openssl dgst -sha256
```

いずれのコマンドでも `-n` は必須です。付けないと `echo` が末尾に改行を付加してしまい、
その改行までハッシュ化されるため、提示されたキーと一致しないダイジェストになってしまいます。
