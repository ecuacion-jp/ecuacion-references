# ReflectionUtil

`ReflectionUtil`（`jp.ecuacion.lib.core.util.ReflectionUtil`）は `java.lang.reflect` を
使用した低レイヤーなリフレクション操作のユーティリティクラスです。

propertyPath 文字列を使ったオブジェクトグラフのナビゲーション（フィールド値の取得、
leafBean の取得など）は `PropertyPathUtil` が担います。

---

## クラスの存在確認とインスタンス生成

```java
// クラスが存在するか確認
boolean exists = ReflectionUtil.classExists("jp.ecuacion.lib.core.util.StringUtil"); // true
boolean exists2 = ReflectionUtil.classExists("com.example.NonExistent");             // false

// 引数なしコンストラクタでインスタンスを生成
Object instance = ReflectionUtil.newInstance("jp.ecuacion.example.MyClass");
```

---

## 単純フィールド名でのフィールド取得

アクセス修飾子に関わらず、スーパークラスを含めてフィールドを検索します。
ドット区切りパスやコレクションインデックスは受け付けません（それらは `PropertyPathUtil.getField` を使用）。

```java
// フィールドを取得（単純名のみ）
Field field = ReflectionUtil.getDeclaredField(MyBean.class, "fieldName");

// スーパークラスのフィールドも検索
Field inherited = ReflectionUtil.getDeclaredField(SubClass.class, "parentField");
```

---

## フィールド値の取得

`setAccessible(true)` を使って private フィールドにもアクセスします。

```java
Field field = ReflectionUtil.getDeclaredField(MyBean.class, "fieldName");
Object value = ReflectionUtil.getFieldValue(bean, field);
```

---

## クラス階層からアノテーションを検索

クラス自身からスーパークラスへと順にさかのぼってアノテーションを検索します。

```java
Optional<MyAnnotation> ann = ReflectionUtil.searchAnnotationPlacedAtClass(
    myInstance.getClass(), MyAnnotation.class);

ann.ifPresent(a -> System.out.println(a.value()));
```

最初に見つかったアノテーションを返します。`Object.class` まで遡っても
見つからない場合は空の `Optional` を返します。
