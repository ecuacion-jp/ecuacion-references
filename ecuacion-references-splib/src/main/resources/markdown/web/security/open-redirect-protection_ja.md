一部の例外ハンドラは、エラー発生後にユーザーを「元いたページ」に戻す必要があります。例えば[例外処理](page?id=web/exception-handling&lang=ja)にある、`SplibGeneralController` の外側で発生した `ViolationException` の処理（フォームがないため、再描画すべき自分自身のページを持たない）です。その時点でアプリが持っている元のページの記録は、クライアントが送ってくる `Referer` ヘッダーしかないため、これをリダイレクト先として使います。しかし、クライアントから渡された値をそのまま `Location` ヘッダーに使うのは典型的なオープンリダイレクトの穴（CWE-601）になるため、そのまま信用することは決してありません。`RefererRedirectUtil.toSameOriginRedirectTarget` が、まずそこから同一オリジンのパスだけを取り出します。

## 何をしているか

`Referer` の値からパス（とクエリ文字列があればそれ）だけを取り出し、スキームやホストは決して使いません。スキームやホストこそが、攻撃者がリダイレクト先を外部サイトに向けるために操作できる部分だからです。

```java
String target = RefererRedirectUtil.toSameOriginRedirectTarget(referer);
return new ModelAndView("redirect:" + target);
```

## スキーム・ホストを除くだけでは不十分な理由

`java.net.URI` は、通常の `Referer: https://evil.example/x` からスキームとホストを取り除き、`/x` だけを残すはずです。単独の「きれいな」`//host/path` という値についても、`URI` は `//` の後ろの部分をauthorityとして解析するため、同様に成立します。問題は、それでもなお攻撃者が完全に制御できる `Referer` が存在することです。**パス部分自体**の先頭にさらに `//` が付いているケース、例えば `https://attacker.example//evil.example/x`（あるいはスキームなしの `////evil.example/x`）です。これは攻撃者がそのままホストできるURLで、被害者をそこへ誘導するリンクを踏ませれば、ブラウザはそれをそのまま `Referer` として送信します。この場合 `URI` は `attacker.example` をauthorityとして扱い、パスは `//evil.example/x` のまま変わりません。パスにスラッシュが含まれること自体は許されており、`URI` はそれを再解釈しないためです。

これを `"redirect:"` の後ろに連結すると、その値はそのままSpringの `RedirectView` に渡り、結果として返される `Location: //evil.example/x` というレスポンスヘッダーは、あらゆるブラウザによって**プロトコル相対**の絶対URL、つまり同じスキームで別のホスト、として解釈されます。その結果、ユーザーはアプリに戻るのではなく `evil.example` に送られてしまいます。

## チェック内容

取り出したリダイレクト先は、`/` がちょうど1つだけで始まっていなければなりません。`//` で始まる場合（あるいは、そもそも `/` で始まっていない場合）は、その入力を破棄し、信用する代わりに `/` を返します。
