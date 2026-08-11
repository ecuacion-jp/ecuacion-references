`ValidationMessages.properties` always uses `PropertiesFileUtil` — there is no equivalent of
the `messages.properties` toggle, and no setting to change this.

## Why there's no toggle

`PropertiesFileUtil`'s own resolution is upper-compatible with what standard Jakarta Bean
Validation message interpolation offers, so there's nothing to gain by switching to it — unlike
`messages.properties`, where "plain Spring Boot behavior" is a real, opt-in-worthy choice for
some apps.

Standard Jakarta Bean Validation has no notion of resolving a message in a locale chosen
per-request (`Validator.validate()` takes no `Locale` argument). ecuacion's own
constraint-message-building pipeline (`ConstraintViolationBean`/`ExceptionUtil`) resolves
`cv.getMessageTemplate()` directly via `PropertiesFileUtil.getValidationMessage()` — which is
locale-aware — and only falls back to the standard interpolator when a key is not found by
ecuacion's own lookup. This also means Spring Boot's `MessageSourceMessageInterpolator` (which
resolves `{code}` constraint messages via `messages.properties`) is never consulted —
ecuacion's own `.default`/`.base` override hierarchy and locale-aware fallback apply
consistently to every `ValidationMessages.properties` lookup, with no risk of a
`messages.properties` key accidentally colliding with a constraint attribute name like
`min`/`max`.

## `${...}` EL expressions are sandboxed

Bean Validation message templates support `${...}` EL expressions bound to the constraint's
own attributes, e.g.:

```properties
myapp.range.message=must be ${inclusive == true ? 'at most' : 'less than'} {value}
```

Only bound variables, indexing, operators, and literals are allowed. Property access and
method invocation on a bound value are rejected:

```properties
# Throws ELException at message-resolution time — not allowed:
myapp.bad.message=${arg.getClass().getClassLoader()}
```

This matches the same restriction Hibernate Validator itself applies by default (its most
restrictive EL feature level), so `ValidationMessages.properties` message templates get the
same protection against arbitrary reflection that plain Jakarta Bean Validation already has.
