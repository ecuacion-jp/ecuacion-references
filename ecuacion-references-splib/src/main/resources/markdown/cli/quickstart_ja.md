[セットアップ](page?id=cli/setup&lang=ja) で依存関係を追加済みであることを前提に、
このページでは最小限のアプリを1つ書いて実行します。

## 1. アプリケーションの main クラスを書く

自身の `main` メソッドを持つクラスを1つ用意し、`SplibCliApplication` に処理を委譲してください。

```java
@SpringBootApplication
public class CliApplication {

  public static void main(String[] args) {
    SplibCliApplication.main(CliApplication.class, args);
  }
}
```

## 2. `SplibCliRunner` を実装する

アプリのロジックを書く場所はここだけです。`SplibCliRunner`を実装し、`execute`メソッド1つに処理を書きます。

```java
@Component
public class HelloRunner implements SplibCliRunner {

  @Override
  public void execute(String[] args) throws InterruptedException {
    Thread.sleep(3000); // 「実行中です...」の表示を確認するためのダミー処理
    System.out.println("Hello, world!");
  }
}
```

## 3. コンソールを静かに保つ（推奨：任意）

CLIアプリのコンソールはそのアプリの「UI」であり、実行している本人が直接見ています。ロギング自体は他のSpring Bootアプリと同様、自分自身の`src/main/resources/logback-spring.xml`で設定できますが、生のlogback形式のフレームワークログ行がそのUIに混ざると浮いて見えがちです。そのため、ロギングを丸ごとOFFにして、アプリが意図して出したいものだけを表示することを推奨しています：

```xml
<configuration>
    <root level="OFF" />
</configuration>
```

## 4. 実行してみる

起動して動作を確認します。

```
mvn spring-boot:run
```

以下のような出力が出ればOKです。

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)
-----

[2026-08-15 15:37:42] 処理を開始します。
Hello, world!
[2026-08-15 15:37:42] 処理が正常に終了しました。
```
