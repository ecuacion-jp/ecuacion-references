[クイックスタート](page?id=cli/quickstart&lang=ja)の手順4で見た通り、`SplibCliApplication`は
Spring Boot標準の起動バナーの代わりに独自のバナーを表示します——これ自体は設定不要です：

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)
-----
```

## アプリ自身の名前とバージョンを表示する

`SplibCliApplication.main`を2引数版の代わりに、3引数版で呼んでください。

```java
SplibCliApplication.main(CliApplication.class, args, "my-app");
```

同じ区切り線の上に、もう一つブロックが追加されます。

```
= ecuacion  command line interface
                   v0.0.2-SNAPSHOT
              (spring boot v4.0.7)

       app  my-app
                   v1.0.0
-----
```

表示されるバージョンは`VersionUtil.getVersion("")`で取得されるため、アプリ自身の
`version.properties`が必要です。動作する実例として
[`ecuacion-tool-code-generator-core`の`pom.xml`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator)
の`version.properties`ファイルと、それに必要な`<resources>`フィルタリング設定
（`${...}`/`@...@`を他のリソース——logback設定など——で誤って置換しないよう、
このファイルだけをフィルタリング対象にする）を参照してください。プロジェクトの親POMが
`ecuacion-splib-parent`（ホームページの[セットアップ](page?id=home&lang=ja)参照）でない場合は、
`version.properties`で`@project.version@`ではなく素の`${project.version}`を使ってください
——`@`区切り文字の慣習は`ecuacion-splib-parent`自身のMavenプラグイン設定に由来するもので、
異なる親POMでは提供されません。

## バナーを非表示にする

`jp.ecuacion.splib.cli.banner-mode`プロパティを`off`に設定してください（デフォルトは`on`）。
コマンドライン引数での例：

```
java -jar your-app.jar --jp.ecuacion.splib.cli.banner-mode=off
```

または`application.properties`で：

```
jp.ecuacion.splib.cli.banner-mode=off
```
