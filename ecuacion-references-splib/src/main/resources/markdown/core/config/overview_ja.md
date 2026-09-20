`PropertiesFileUtil`（`jp.ecuacion.lib.core.util.PropertiesFileUtil`）は、`application.properties`・
`messages.properties`・`ValidationMessages.properties` の3系統で使われる、ecuacion独自の
`*.properties` 読み込みユーティリティです。`ecuacion-lib` 自体は Spring に依存していないため、
この3系統それぞれで Spring Boot との統合のされ方が異なります。「Spring本来の方式」と「PropertiesFileUtil独自の方式」のどちらがどこまで適用されるかは、ファイル種別によって違います。このページで3系統の全体像をまとめ、以降のページでそれぞれの詳細を説明します。

## 3系統の比較

| ファイル種別 | 解決の主体 | `PropertiesFileUtil` を直接呼んだ場合（`getApplication`/`getMessage` 等） | Spring経由（`@Value`、`Environment`、`MessageSource`、Bean Validation） |
| --- | --- | --- | --- |
| `application.properties` | 常にSpring | SpringのEnvironmentに委譲され、結果は同一 | ここが本来の情報源 |
| `messages.properties` | デフォルトは`PropertiesFileUtil`、設定でSpringに切替可 | 設定値に関わらず常に`PropertiesFileUtil`自身の解決 | 設定で切替可能 — [messages.properties](page?id=core/config/messages&lang=ja) 参照 |
| `ValidationMessages.properties` | 常に`PropertiesFileUtil` | 常に`PropertiesFileUtil`自身の解決 | Springに委ねる窓口自体が存在しない |

## なぜ違うのか

`application.properties`は`PropertiesFileUtil.getApplication()`を直接呼んだ場合も含めて完全にSpringに委譲されるのに、`messages.properties`のSpring切替設定はSpring自身の
`MessageSource` beanにしか効かず`PropertiesFileUtil.getMessage()`は影響を受けない、
というのは一見矛盾しているように見えます。この違いは、「Springに委譲することでそれぞれが何を得て何を失うか」という点に起因します。

- **`application.properties`**: そもそもecuacionモジュール自身がapplication.propertiesを持つ設計にはなっておらず、`messages.properties`が使う`.default`/`.base`モジュール横断上書きの対象外です。SpringのEnvironmentは、classpath上のファイルに加えて外部ファイル・環境変数・プロファイル・`-D`システムプロパティまでカバーする上位互換の情報源です。
  丸ごと委譲してもコストは小さく（唯一の注意点である`#{...}`相互参照については
  [application.properties](page?id=core/config/application-properties&lang=ja)を参照）、
  得るものの方が大きいため、設定を設けず無条件で適用しています。
- **`messages.properties`**: `PropertiesFileUtil`の`.default`/`.base`モジュール横断上書きや`#{fileKind:key}`相互参照は、ecuacion自身の組み込みメッセージも含めて実際に使われている機能です。Spring標準の`ResourceBundleMessageSource`はどちらも理解しません。
  丸ごと委譲すると本当に機能を失うため、オプトイン設定とし、しかもSpring向けの窓口である
  `MessageSource` bean（Thymeleafのメッセージ式、Spring MVC自身のメッセージ解決など、
  「素のSpring Boot挙動」が本当に意味を持つ範囲）だけに絞っています。
  `PropertiesFileUtil.getMessage()`の直接呼び出しは、設定値に関わらず影響を受けません。
- **`ValidationMessages.properties`**: `PropertiesFileUtil`自身の解決は、Jakarta Bean
  Validation本来のメッセージ補間が提供する機能に対してすでに上位互換です（詳細は[ValidationMessages.properties](page?id=core/config/validation-messages&lang=ja)
  を参照）。そのため、そもそも切替設定を用意する必要がありません。

## 関連ページ

- [application.properties](page?id=core/config/application-properties&lang=ja)
- [messages.properties](page?id=core/config/messages&lang=ja)
- [ValidationMessages.properties](page?id=core/config/validation-messages&lang=ja)
