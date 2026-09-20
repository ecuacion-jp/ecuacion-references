`/api/key/**` 配下にマッピングされたエンドポイントは、有効な `X-Api-Key` ヘッダーが必須です。実際に動かすところまでの最短手順は
[クイックスタート](page?id=rest/security/api-key/quickstart&lang=ja) を参照してください。

## リクエストヘッダー

| ヘッダー | 必須 | 意味 |
| --- | --- | --- |
| `X-Api-Key` | 必須 | API キー本体。 |
| `X-Api-Key-Id` | 任意 | 任意のキー識別子。Provider へそのまま渡されます。<br>AWS のアクセスキー ID や HTTP Basic のユーザー名に近い位置づけで、「どのレコードの期待値と照合するか」を特定するために使います。<br>単一の共有キーのみを扱う実装では無視して構いません。 |

キーの照合ロジックの実装方法や、認証成功・失敗時の挙動については
[認証処理](page?id=rest/security/api-key/authentication&lang=ja) を参照してください。
