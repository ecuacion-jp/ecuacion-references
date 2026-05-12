# MailUtil

`MailUtil`（`jp.ecuacion.lib.core.util.MailUtil`）はメール送信のユーティリティクラスです。

---

## メソッド一覧

| メソッド | 説明 |
| --- | --- |
| `sendErrorMail(throwable)` | システムエラー発生時に管理者へ通知メールを送信 |
| `sendErrorMail(throwable, message)` | 追加メッセージ付きでエラーメールを送信 |
| `sendWarnMail(content, mailToList)` | 警告メールを送信 |
| `sendTextMail(toList, ccList, title, content)` | テキスト形式メールを送信 |
| `sendHtmlMail(toList, ccList, title, content)` | HTML 形式メールを送信 |

```java
// エラーメール（送信先は application.properties で設定）
MailUtil.sendErrorMail(throwable);
MailUtil.sendErrorMail(throwable, "バッチ処理中にエラーが発生しました");

// 任意の宛先にテキスト・HTML メールを送信
MailUtil.sendTextMail(List.of("admin@example.com"), null, "件名", "本文");
MailUtil.sendHtmlMail(null, List.of("cc@example.com"), "件名", "<b>本文</b>");
```

---

## application.properties の設定

プレフィックスはすべて `jp.ecuacion.lib.core.mail.` です。以下の表ではプレフィックスを省略します。

### 必須

| キー（プレフィックス省略） | 説明 | 例 |
| --- | --- | --- |
| `smtp.server` | SMTP サーバーのホスト名 | `smtp.gmail.com` |
| `smtp.port` | SMTP ポート番号 | `465`、`587` |
| `smtp.sender` | 送信元メールアドレス | `noreply@example.com` |
| `smtp.password` | SMTP パスワード（Gmail の場合はアプリパスワード） | `xxxx xxxx xxxx xxxx` |
| `smtp.authentication` | SMTP 認証の有無（`true` / `false`） | `true` |
| `smtp.checks-certificate` | SSL 証明書の検証有無（`true` / `false`） ※1 | `true` |

※1 `smtp.ssl-enabled=false` の場合も設定が必要です（値は `false` を指定）。
SSL 有効時（`smtp.ssl-enabled=true`）のみ実際に効果があります。

### 任意

| キー（プレフィックス省略） | 説明 | デフォルト |
| --- | --- | --- |
| `smtp.ssl-enabled` | SSL/TLS の有効化（`true` / `false`） | `false` |
| `smtp.bounce-address` | バウンスメールの受信アドレス | 未設定（バウンス設定なし） |
| `debug` | Jakarta Mail のデバッグログ出力（`true` / `false`） | `false` |

### sendErrorMail / sendWarnMail 使用時に必須

| キー（プレフィックス省略） | 説明 | 例 |
| --- | --- | --- |
| `title-prefix` | メール件名の先頭に付与する文字列 | `[MyApp: staging]` |
| `address-csv-on-system-error` | `sendErrorMail` の送信先（カンマ区切りで複数可）※2 | `admin@example.com,ops@example.com` |

※2 `sendWarnMail` / `sendTextMail` / `sendHtmlMail` では送信先を引数で指定するため不要です。

---

## 設定例

```properties
# --- SMTP サーバー接続（必須）---
jp.ecuacion.lib.core.mail.smtp.server=smtp.gmail.com
jp.ecuacion.lib.core.mail.smtp.port=465
jp.ecuacion.lib.core.mail.smtp.sender=noreply@example.com
jp.ecuacion.lib.core.mail.smtp.password=xxxx xxxx xxxx xxxx
jp.ecuacion.lib.core.mail.smtp.authentication=true
jp.ecuacion.lib.core.mail.smtp.checks-certificate=true

# --- SSL（任意、ポート 465 使用時は true を推奨）---
jp.ecuacion.lib.core.mail.smtp.ssl-enabled=true

# --- sendErrorMail / sendWarnMail 使用時 ---
jp.ecuacion.lib.core.mail.title-prefix=[MyApp: staging]
jp.ecuacion.lib.core.mail.address-csv-on-system-error=admin@example.com
```
