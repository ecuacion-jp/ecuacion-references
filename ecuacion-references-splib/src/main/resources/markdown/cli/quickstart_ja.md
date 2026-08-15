[セットアップ](page?id=cli/setup&lang=ja) で依存関係を追加済みであることを前提に、
このページでは最小限のアプリを1つ書いて実行します。

## 1. アプリケーションの main クラスを書く

`SplibCliApplication` は、すべてのCLIアプリに必要な `main` メソッドのロジック
（`SpringApplication` を実行し、`SplibCliRunner` を実行し、適切な終了コードで終了する）を
提供していますが、`java` コマンドは親クラスから継承した `main` メソッドを実行できないため、
これ自体を起動対象のクラスにすることはできません。そのため、アプリケーション側で
自身の `main` メソッドを持つクラスを用意し、単純に処理を委譲してください。

```java
@SpringBootApplication
public class CliApplication {

  public static void main(String[] args) {
    SplibCliApplication.main(CliApplication.class, args);
  }
}
```

`ecuacion-splib-batch` と異なり、ここで `@ComponentScan` を書く必要はありません
— `ecuacion-splib-cli` 自身が持つ Bean（`SplibExceptionHandler`）は、
Spring Boot の自動設定機構により自ら登録されます。

アプリ自身の名前・バージョンを起動バナーに表示する方法や、バナー自体を非表示にする方法は
[バナー](page?id=cli/banner&lang=ja)を参照してください。

## 2. コンソールを静かに保つ（推奨）

CLIアプリのコンソールはそのアプリの「UI」であり、実行している本人が直接見ています。
ロギング自体は他のSpring Bootアプリと同様、自分自身の`src/main/resources/logback-spring.xml`で
設定できますが、生のlogback形式のフレームワークログ行がそのUIに混ざると浮いて見えがちです。
そのため、ロギングを丸ごとOFFにして、アプリが意図して出したいものだけを表示することを
推奨しています：

```xml
<configuration>
    <root level="OFF" />
</configuration>
```

この静かなデフォルトのままでも、ユーザーが必要な時だけスタックトレースを見る方法は
[例外処理](page?id=cli/exception-handling&lang=ja)を参照してください。

## 3. `SplibCliRunner` を実装する

```java
@Component
public class HelloRunner implements SplibCliRunner {

  @Override
  public void execute(String[] args) {
    System.out.println("Hello, world!");
  }
}
```

アプリが用意する `SplibCliRunner` Bean はちょうど1つです — `ecuacion-splib-batch` の
Job/Step分割は無人実行のためのものですが、CLIアプリは実行している本人が直接見ているため、
そのような分割は不要です。

## 4. 実行してみる

```
mvn spring-boot:run
```

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)
-----

[2026-08-15 15:37:42] 処理を開始します。
Hello, world!
[2026-08-15 15:37:42] 処理が正常に終了しました。
```

このバナーは`SplibCliApplication`自身が出すもので、上記のデフォルト表示自体は設定不要です。
アプリ自身の名前・バージョンを表示したり、バナー自体を非表示にする方法は
[バナー](page?id=cli/banner&lang=ja)を参照してください。
タイムスタンプ付きの「処理を開始します。」「処理が正常に終了しました。」（ローカライズ済み）は
`execute`呼び出しの前後に自動的に表示されます——これも呼び出す必要はありません。

`execute`の実行中は、コンソールの最終行にアニメーションする「実行中です...」
（ローカライズ済み。[概要](page?id=cli/overview&lang=ja)参照。文字の右側でくるくる回ります）が
表示され、処理が終わると自動的に消えます——呼び出す必要はありません。出力が対話的なターミナルで
ない場合（ファイルへのリダイレクト等）は完全にスキップされるので、リダイレクト先やCI環境の出力を
汚しません。

続きとして、[例外処理](page?id=cli/exception-handling&lang=ja) では `execute` が例外を
投げたときの挙動と、未捕捉の例外発生時に独自処理（開発者への通知など）を実行する方法を
扱っています。任意設定で、アプリを動かすだけなら不要です。
