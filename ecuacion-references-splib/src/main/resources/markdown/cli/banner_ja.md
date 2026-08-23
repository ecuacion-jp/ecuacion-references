[クイックスタート](page?id=cli/quickstart&lang=ja)の手順4で見た通り、`SplibCliApplication`は
Spring Boot標準の起動バナーの代わりに独自のバナーを表示します——これ自体は設定不要です：

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)
-----
```

## アプリ自身の名前を表示する

`SplibCliApplication.main`を2引数版の代わりに、3引数版で呼んでください。

```java
SplibCliApplication.main(CliApplication.class, args, "my-app");
```

同じ区切り線の上に、もう一つブロックが追加されます。

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)

       app  my-app
-----
```

## アプリ自身のバージョンを表示する

表示されるバージョンは`VersionUtil.getVersion("")`で取得されるため、
`src/main/resources/version.properties`を用意してください。

```properties
version=${project.version}
```

バージョンが表示されるようになると、名前の下にもう一行追加されます。

```
= ecuacion  command line interface
                            v5.0.0
              (spring boot v4.0.7)

       app  my-app
                            v1.0.0
-----
```

`${project.version}`はMavenのリソースフィルタリングでビルド時に実際のバージョン文字列へ置換されます。`${...}`を他のリソース——logback設定など——で誤って置換しないよう、`version.properties`だけをフィルタリング対象にする`<resources>`設定をpom.xmlに追加してください。

```xml
<build>
  <resources>
    <resource>
      <directory>src/main/resources</directory>
      <filtering>false</filtering>
    </resource>
    <resource>
      <directory>src/main/resources</directory>
      <includes>
        <include>version.properties</include>
      </includes>
      <filtering>true</filtering>
    </resource>
  </resources>
</build>
```

動作する実例として
[`ecuacion-tool-code-generator-core`の`pom.xml`](https://github.com/ecuacion-jp/ecuacion-tool-code-generator)
を参照してください。ちなみにこの実例では親POMが`ecuacion-splib-parent`（ホームページの
[セットアップ](page?id=home&lang=ja)参照）である都合上、`${project.version}`ではなく
`@project.version@`を使っています。

## バナーの色を変更・非表示にする

`jp.ecuacion.splib.cli.banner-mode`プロパティ（デフォルトは`color`）は以下の値を受け付けます。

- `color` — 上記の通り、ブロックごとに異なる色で表示します。
- `white` — 全ての文字を白一色で表示します。ターミナルの背景が暗く、デフォルトの配色が見づらい場合向けです。
- `black` — 全ての文字を黒一色で表示します。ターミナルの背景が明るく、デフォルトの配色が見づらい場合向けです。
- `off` — 何も表示しません。

アプリとしてのデフォルトを固定するには`application.properties`に設定してください。

```properties
jp.ecuacion.splib.cli.banner-mode=white
```

コマンドライン引数は（Spring Boot自体のプロパティソースの優先順位により）常に
`application.properties`より優先されるため、ユーザごとに使うターミナルの配色が異なる場合でも、
実行時に個別に上書きできます。

```
java -jar your-app.jar --jp.ecuacion.splib.cli.banner-mode=black
```
