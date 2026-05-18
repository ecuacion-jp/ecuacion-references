# MailUtil

`MailUtil` (`jp.ecuacion.lib.core.util.MailUtil`) is a utility class for sending emails.

---

## Method List

| Method | Description |
| --- | --- |
| `sendErrorMail(throwable)` | Sends a notification email to the administrator when a system error occurs |
| `sendErrorMail(throwable, message)` | Sends an error email with an additional message |
| `sendWarnMail(content, mailToList)` | Sends a warning email |
| `sendTextMail(toList, ccList, title, content)` | Sends an email in plain text format |
| `sendHtmlMail(toList, ccList, title, content)` | Sends an email in HTML format |

```java
// Error email (recipient configured in application.properties)
MailUtil.sendErrorMail(throwable);
MailUtil.sendErrorMail(throwable, "An error occurred during batch processing");

// Send text/HTML email to arbitrary recipients
MailUtil.sendTextMail(List.of("admin@example.com"), null, "Subject", "Body");
MailUtil.sendHtmlMail(null, List.of("cc@example.com"), "Subject", "<b>Body</b>");
```

---

## application.properties Configuration

All keys have the prefix `jp.ecuacion.lib.core.mail.`. The prefix is omitted in the table below.

### Required

| Key (prefix omitted) | Description | Example |
| --- | --- | --- |
| `smtp.server` | SMTP server hostname | `smtp.gmail.com` |
| `smtp.port` | SMTP port number | `465`, `587` |
| `smtp.sender` | Sender email address | `noreply@example.com` |
| `smtp.password` | SMTP password (for Gmail, use an app password) | `xxxx xxxx xxxx xxxx` |
| `smtp.authentication` | Whether SMTP authentication is used (`true` / `false`) | `true` |
| `smtp.checks-certificate` | Whether to verify SSL certificates (`true` / `false`) *1 | `true` |

*1 Required even when `smtp.ssl-enabled=false` (specify `false` as the value).
Only takes effect when SSL is enabled (`smtp.ssl-enabled=true`).

### Optional

| Key (prefix omitted) | Description | Default |
| --- | --- | --- |
| `smtp.ssl-enabled` | Enable SSL/TLS (`true` / `false`) | `false` |
| `smtp.bounce-address` | Address to receive bounce emails | Not set (no bounce configuration) |
| `debug` | Enable Jakarta Mail debug logging (`true` / `false`) | `false` |

### Required When Using sendErrorMail / sendWarnMail

| Key (prefix omitted) | Description | Example |
| --- | --- | --- |
| `title-prefix` | String prepended to the email subject | `[MyApp: staging]` |
| `address-csv-on-system-error` | Recipients for `sendErrorMail` (comma-separated for multiple) *2 | `admin@example.com,ops@example.com` |

*2 Not needed for `sendWarnMail` / `sendTextMail` / `sendHtmlMail`, as recipients are specified as arguments.

---

## Configuration Example

```properties
# --- SMTP server connection (required) ---
jp.ecuacion.lib.core.mail.smtp.server=smtp.gmail.com
jp.ecuacion.lib.core.mail.smtp.port=465
jp.ecuacion.lib.core.mail.smtp.sender=noreply@example.com
jp.ecuacion.lib.core.mail.smtp.password=xxxx xxxx xxxx xxxx
jp.ecuacion.lib.core.mail.smtp.authentication=true
jp.ecuacion.lib.core.mail.smtp.checks-certificate=true

# --- SSL (optional; recommended to set true when using port 465) ---
jp.ecuacion.lib.core.mail.smtp.ssl-enabled=true

# --- When using sendErrorMail / sendWarnMail ---
jp.ecuacion.lib.core.mail.title-prefix=[MyApp: staging]
jp.ecuacion.lib.core.mail.address-csv-on-system-error=admin@example.com
```
