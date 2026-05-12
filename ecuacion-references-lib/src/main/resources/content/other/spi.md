# SPI（Service Provider Interface）

## 概要

`jp.ecuacion.lib.core.spi` パッケージは、**Java 9 モジュールシステム（JPMS）使用時**に
`PropertiesFileUtil` が各種 `.properties` ファイルを読み込めるようにするための
Service Provider Interface（SPI）を提供します。

通常のクラスパスベースのアプリ（`module-info.java` なし）では不要です。

---

## 必要になる場面

`module-info.java` を使った Java モジュールシステム環境では、`ResourceBundle` の読み込みに
SPI の登録が必要になります。アプリの各モジュールが独自の `.properties` ファイルを持つ場合、
そのモジュールに対応する SPI 実装を登録する必要があります。

---

## SPI インターフェースの種類

各プロパティファイル種別に対応したインターフェースが用意されています。

| SPI インターフェース | 対応ファイル |
| ------------------ | ------------ |
| `MessagesProvider` | messages.properties |
| `MessagesBaseProvider` | messages_base.properties |
| `MessagesCoreProvider` | messages_core.properties |
| `ItemNamesProvider` | item_names.properties |
| `EnumNamesProvider` | enum_names.properties |
| `ApplicationProvider` | application.properties |
| `ApplicationBaseProvider` | application_base.properties |
| （他多数） | 各ファイル種別のサフィックスに対応 |

---

## module-info.java の設定

アプリモジュールが `.properties` ファイルを持つ場合、以下の2点が必要です。

### 1. SPI 実装クラスの作成

`AbstractPropertiesFileProviderImpl` を継承した実装クラスを作成します。

```java
// 例: messages.properties を持つモジュールの場合
public class AppMessagesProvider extends AbstractPropertiesFileProviderImpl
    implements MessagesProvider {
}
```

### 2. module-info.java への登録

```java
module your.app.module {
    requires jp.ecuacion.lib.core;

    // SPI プロバイダーを登録
    provides jp.ecuacion.lib.core.spi.MessagesProvider
        with your.app.AppMessagesProvider;

    // ライブラリがリソースファイルにアクセスできるよう open する
    opens your.app.resources;
}
```

`opens` 宣言がないと、ライブラリがリソースファイルを読み込めないため注意してください。

---

## 通常のアプリ開発への影響

`module-info.java` を使わないアプリでは、SPI を意識する必要はありません。
`PropertiesFileUtil` は自動的にクラスパス上の `.properties` ファイルを検索します。
