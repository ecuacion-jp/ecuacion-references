`ValidationMessages.properties`は常に`PropertiesFileUtil`を使います。`messages.properties`
のような切替設定は存在せず、変更する手段もありません。

## なぜ切替設定がないのか

`PropertiesFileUtil`自身の解決は、標準のJakarta Bean Validationが提供するメッセージ補間に
対してすでに上位互換です。そのため切り替えて得られるものがなく、`messages.properties`の
ように「素のSpring Boot挙動」がアプリによっては選ぶ価値のある選択肢になる、という状況とは
異なります。

標準のJakarta Bean Validationには、そもそもリクエスト単位でロケールを切り替える仕組み
（`Validator.validate()`に`Locale`引数がない）が存在しません。ecuacion自身の制約メッセージ
構築パイプライン（`ConstraintViolationBean`/`ExceptionUtil`）は、`cv.getMessageTemplate()`を
直接、ロケール対応の`PropertiesFileUtil.getValidationMessage()`で解決し、ecuacion側で
見つからない場合のみ標準インタポレータにフォールバックします。これはまた、Spring Bootの
`MessageSourceMessageInterpolator`（`{code}`形式の制約メッセージを`messages.properties`
経由で解決する機能）が一切呼ばれないことも意味します。ecuacion独自の`.default`/`.base`
上書き階層とロケール対応フォールバックが、`ValidationMessages.properties`のすべての参照に
一貫して適用され、`messages.properties`のキーが`min`/`max`のような制約属性名と偶然衝突する
リスクもありません。

## `${...}` EL式はサンドボックス化されています

Bean Validationのメッセージテンプレートは、制約自身の属性に束縛された`${...}`EL式を
サポートしています。例えば:

```properties
myapp.range.message=must be ${inclusive == true ? 'at most' : 'less than'} {value}
```

束縛済み変数の参照・添字アクセス・演算子・リテラルのみが許可されます。束縛された値への
プロパティアクセスやメソッド呼び出しは拒否されます。

```properties
# メッセージ解決時に ELException が投げられます — 許可されません:
myapp.bad.message=${arg.getClass().getClassLoader()}
```

これはHibernate Validator自身がデフォルトで適用する制限（最も制限の強いEL機能レベル）と
同じであり、`ValidationMessages.properties`のメッセージテンプレートも、素のJakarta Bean
Validationが元々持っている「任意のリフレクションを許さない」という保護を同様に受けます。
