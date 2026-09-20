`SplibAppConfDirLifecycleListener`（`jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener`）は
Tomcatの`LifecycleListener`です。アプリ側が自身の`META-INF/context.xml`にこれを宣言すると、外部ディレクトリを
アプリのクラスパス（デフォルトでは`/WEB-INF/classes`）にマウントします — 静的な`<PreResources>`エントリと
同じ効果ですが、ディレクトリが存在しない場合は自動的に作成する点が異なります。素の`<PreResources>`は`base`が
存在しないと`IllegalArgumentException`でTomcatのデプロイ自体が失敗しますが、これはその失敗を防ぎます。

特定のアプリに紐づく仕組みではない汎用クラスなので、`ecuacion-splib-core`に依存するTomcatデプロイ対象の
アプリであればどれでも宣言できます。

## `context.xml`での宣言

```xml
<Context>
	<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
			customPathPropertyName="jp.ecuacion.tool.your-app.app-conf-dir"
			defaultPath="${catalina.base}/app-conf/ecuacion-tool-your-app"
			createDefaultPathIfMissing="true"/>
</Context>
```

| 属性 | 必須 | 説明 |
| --- | --- | --- |
| `defaultPath` | 下記Note参照 | カスタムパスが有効でない場合に使われるディレクトリ。通常`${catalina.base}`で始め、このクラスが値を受け取る前にTomcat自身のDigesterによるプロパティ置換で展開されます。 |
| `customPathPropertyName` | 下記Note参照 | デプロイごとに`-D`（例: Tomcatの`setenv.sh` / `CATALINA_OPTS`）で設定できる、`defaultPath`を上書きするシステムプロパティの名前。 |
| `createDefaultPathIfMissing` | 任意（デフォルト`false`） | `defaultPath`が使われる場合に、それが存在しないときに作成するかどうか。`defaultPath`の設定が必須。詳細は下記の[2つの利用パターン](#2つの利用パターン)を参照。 |
| `webAppMount` | 任意（デフォルト`/WEB-INF/classes`） | 解決されたディレクトリをアプリ内のどこにマウントするか。 |

> **Note:** `defaultPath`と`customPathPropertyName`は少なくとも一方の設定が必須です — どちらも
> 未設定の`<Listener>`は何もマウントできず、常に設定ミスとみなされます（起動時に
> `IllegalStateException`で失敗します）。どちらか片方だけでも構いません:
> `defaultPath`のみ（上書き不可）、または`customPathPropertyName`のみ（オペレーターが明示的に
> 指定しない限り何もマウントされない — 下記のパターンBを参照）。

## 解決順序

1. `customPathPropertyName`が設定されており、かつそのシステムプロパティ自体も（空でなく）設定されている場合は
   その値が使われます — そしてディレクトリが存在しなければ**必ず**作成されます。オペレーターが明示的にパスを
   指定した場合、それが機能することを明らかに意図しているためです。
2. それ以外の場合、`defaultPath`が設定されていればそれが使われ、`createDefaultPathIfMissing`が`true`のとき
   だけディレクトリが作成されます。`false`でディレクトリが存在しない場合は何もマウントされません（INFOログ
   のみ）— 数瞬後にTomcat自身の検証で失敗するようなパスをマウントするより、アプリ自身に埋め込まれた設定を
   そのまま使う形になります。
3. それ以外（カスタムパスも有効でなく、`defaultPath`も未設定）の場合は、何もマウントされません（INFOログ
   のみ）。

## 2つの利用パターン

### パターンA — デフォルトパスを常時自動作成し、オペレーターによる上書きも可能にする

`ecuacion-tool-command-api`が採用しているパターンです（詳細は同アプリの
[configリファレンス](https://references.ecuacion.jp/ecuacion-references-tools/public/showMarkdown/page?id=command-api/config&lang=ja)の
「既存のTomcat等にデプロイする場合」を参照）:

```xml
<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
		customPathPropertyName="jp.ecuacion.tool.command-api.app-conf-dir"
		defaultPath="${catalina.base}/app-conf/ecuacion-tool-command-api"
		createDefaultPathIfMissing="true"/>
```

`createDefaultPathIfMissing="true"`により、オペレーターが何も設定しなくてもデフォルトパスが常に作成・
マウントされます — セットアップ不要でも外部設定ディレクトリが機能する状態になります。それでも
`jp.ecuacion.tool.command-api.app-conf-dir`システムプロパティにより、`context.xml`を書き換えずにデプロイ
ごとに別の場所へリダイレクトできます。

### パターンB — カスタムパスを明示した場合以外は何も起こらない

`ecuacion-tool-code-generator`が採用しているパターンです:

```xml
<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
		customPathPropertyName="jp.ecuacion.tool.code-generator.app-conf-dir"/>
```

`defaultPath`を完全に未設定のままにすることで、フォールバック先のディレクトリ自体が存在しなくなります —
オペレーターが`jp.ecuacion.tool.code-generator.app-conf-dir`を明示的に設定しない限り、ディレクトリの作成も
追加のマウントも一切発生しません。Tomcat側で何も設定せずにWARをそのままデプロイしても問題なく動作し、WAR
自体に埋め込まれた設定がそのまま使われます。

どちらのパターンを選ぶかはアプリ側の判断です。`createDefaultPathIfMissing`のデフォルトが`false`であり、
`defaultPath`自体も任意であるのは、このListenerを採用しただけで、望んでいないアプリのディスク上にディレクトリ
が勝手に作られ始めることのないようにするためです。

## 複数の`<Listener>`宣言

同一の`Context`に対して、このクラスの`<Listener>`を1つの`context.xml`内に複数宣言することもできます —
たとえば、上書き可能なアプリ専用ディレクトリ用に1つ、複数アプリで共有する固定ディレクトリ用に
（`customPathPropertyName`を未設定にした）シンプルなものをもう1つ、といった形です。Listenerは宣言順に
発火し、Tomcatの`WebResourceRoot.addPreResources`は追加（append）方式なので、複数のマウント先ディレクトリに
同名のファイルが存在する場合、**先に**宣言された`<Listener>`側が優先されます。これは静的な`<PreResources>`を
宣言順に2つ並べた場合と全く同じ挙動です。

## ライフサイクルのタイミング

`tomcat-embed-core` 11.0.21のソースに基づいて検証済みです（単なる推測ではありません — Tomcat公式リファレンス
にはこの粒度の説明はありません）。`META-INF/context.xml`で宣言された`<Listener>`は、`Context.init()`の
過程でパースされ`Context`に登録されます。これは`Context.start()`が呼ばれるより完全に前に完了します。
`Lifecycle.BEFORE_START_EVENT`は`LifecycleBase.start()`自身から、`StandardContext.startInternal()`を
呼び出すより前に発火します — そして`startInternal()`こそが`resourcesStart()`を呼び出す箇所であり、これが
実際に（存在しない`PreResources`ディレクトリを）検証し拒否する処理です。つまり、`init()`の時点で既に登録
されているListenerは、その検証が走るより前に必ず`BEFORE_START_EVENT`を受け取り、ディレクトリを作成する機会
を得られることが保証されています。
