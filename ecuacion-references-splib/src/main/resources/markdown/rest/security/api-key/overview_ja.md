`/api/key/**` 配下にマッピングされたエンドポイントは、有効な `X-Api-Key` ヘッダーが必須です。
このフィルターチェーンは `@Order(9)` で実行され、`/api/public/**`（8）の後、
残りすべてを拒否する `/api/**` のルール（10）より前に評価されます。

## リクエストヘッダー

| ヘッダー | 必須 | 意味 |
| --- | --- | --- |
| `X-Api-Key` | 必須 | API キー本体。 |
| `X-Api-Key-Id` | 任意 | 任意のキー識別子。Provider へそのまま渡されます。AWS のアクセスキー ID や HTTP Basic のユーザー名に近い位置づけで、「どのレコードの期待値と照合するか」を特定するために使います。単一の共有キーのみを扱う実装では無視して構いません。 |

## 照合ロジックの実装

`SplibApiKeyExpectedValueProvider` を実装した Bean を登録します。

```java
@Component
public class AppApiKeyExpectedValueProvider implements SplibApiKeyExpectedValueProvider {

  @Override
  public String getExpectedValue(@Nullable String apiKeyId, String presentedApiKey) {
    // application.properties の固定値、apiKeyId をキーにした DB 検索など、
    // アプリケーションに合った方法で期待値を取得する。
    // 該当なし（apiKeyId が未知など）の場合は null を返してリクエストを拒否する。
    return lookUpExpectedValue(apiKeyId);
  }
}
```

戻り値を平文として比較するか SHA-256 ハッシュとして比較するかは `jp.ecuacion.splib.rest.api-key.mode`
で制御されます。
[比較モード](/public/showMarkdown/page?id=rest/security/api-key/comparison-modes&lang=ja) を参照してください。

`SplibApiKeyExpectedValueProvider` の Bean が一つも登録されていない場合、`/api/key/**` へのリクエストは
すべて拒否されます。`/api/public/**` と異なり、このプレフィックスに「キー不要」というデフォルト動作はありません。

## 拒否時の挙動

`SplibApiKeyAuthenticationFilter` は以下のいずれの場合も同じ汎用的な `401` を返すため、
呼び出し側はどのケースに該当したかを区別できません。

- `X-Api-Key` ヘッダーが欠落または空。
- `SplibApiKeyExpectedValueProvider` の Bean が登録されていない。
- Provider が `null` を返した（例：`apiKeyId` が未知）。
- 提示されたキーが期待値と一致しない。

詳細はサーバー側のログにのみ出力され、提示されたキーの値自体はログに出力されません。
比較には `MessageDigest.isEqual`（定数時間比較）を用いており、タイミング攻撃を防いでいます。

## 認証成功時

照合に成功すると、`apiKeyId`（`X-Api-Key-Id` が送られていない場合は `"api-key-client"`）として、
`ROLE_API_KEY` 権限で認証されます。

## ここでも CSRF が無効化されている理由

通常の Cookie 認証エンドポイントと異なり、CSRF が悪用するのは「ブラウザが JavaScript に値を知らせることなく
自動的に付与する」*アンビエント*な資格情報（Cookie 等）です。`X-Api-Key` はアンビエントではありません。
クロスサイトのページはキーをあらかじめ知らない限りこのヘッダーを設定できず、知っているなら
被害者のブラウザを介さず直接 API を呼び出せてしまいます。そのため配下のエンドポイントが読み取り専用かどうかに関わらず、
ここでは CSRF 対策が守るべきものがありません。これは
[Public エンドポイント](/public/showMarkdown/page?id=rest/security/public-endpoints&lang=ja) において
「読み取り専用という規約があるからこそ」CSRF を無効化できる、という理屈とは対照的です。
