`ReflectionUtil` (`jp.ecuacion.lib.core.util.ReflectionUtil`) is a utility class for low-level
reflection operations using `java.lang.reflect`.

Navigation of object graphs using propertyPath strings (getting field values, getting leafBean, etc.)
is handled by `PropertyPathUtil`.

---

## Checking Class Existence and Creating Instances

```java
// Check if a class exists
boolean exists = ReflectionUtil.classExists("jp.ecuacion.lib.core.util.StringUtil"); // true
boolean exists2 = ReflectionUtil.classExists("com.example.NonExistent");             // false

// Create an instance using the no-arg constructor
Object instance = ReflectionUtil.newInstance("jp.ecuacion.example.MyClass");
```

---

## Getting Fields by Simple Field Name

Searches for fields including superclasses, regardless of access modifiers.
Does not accept dot-separated paths or collection indices (use `PropertyPathUtil.getField` for those).

```java
// Get a field (simple name only)
Field field = ReflectionUtil.getDeclaredField(MyBean.class, "fieldName");

// Also searches superclass fields
Field inherited = ReflectionUtil.getDeclaredField(SubClass.class, "parentField");
```

---

## Getting Field Values

Uses `setAccessible(true)` to access even private fields.

```java
Field field = ReflectionUtil.getDeclaredField(MyBean.class, "fieldName");
Object value = ReflectionUtil.getFieldValue(bean, field);
```

---

## Searching for Annotations in the Class Hierarchy

Searches for annotations by traversing from the class itself up through superclasses.

```java
Optional<MyAnnotation> ann = ReflectionUtil.searchAnnotationPlacedAtClass(
    myInstance.getClass(), MyAnnotation.class);

ann.ifPresent(a -> System.out.println(a.value()));
```

Returns the first annotation found. Returns an empty `Optional` if not found
even after traversing up to `Object.class`.
