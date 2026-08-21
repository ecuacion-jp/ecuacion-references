`SplibAppConfDirLifecycleListener` (`jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener`) is a
Tomcat `LifecycleListener`. A consuming app declares it in its own bundled
`META-INF/context.xml` to mount an external directory onto its classpath (at
`/WEB-INF/classes` by default) — the same effect as a static `<PreResources>` entry —
except the directory is created automatically if it doesn't already exist, instead of
Tomcat failing to deploy with an `IllegalArgumentException` (its own behavior for a
`PreResources` whose `base` is missing).

It's generic — not tied to any one app — so any app deployed to Tomcat that pulls in
`ecuacion-splib-core` can declare it.

## Declaring it in `context.xml`

```xml
<Context>
	<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
			customPathPropertyName="jp.ecuacion.tool.your-app.app-conf-dir"
			defaultPath="${catalina.base}/app-conf/ecuacion-tool-your-app"
			createDefaultPathIfMissing="true"/>
</Context>
```

| Attribute | Required | Description |
| --- | --- | --- |
| `defaultPath` | See note | The directory used when no custom path is in effect. Typically starts with `${catalina.base}`, expanded by Tomcat's own Digester property substitution before this class ever sees the value. |
| `customPathPropertyName` | See note | The name of a system property (settable per deployment via `-D`, e.g. in Tomcat's `setenv.sh` / `CATALINA_OPTS`) that, if set, overrides `defaultPath`. |
| `createDefaultPathIfMissing` | No (default `false`) | Whether to create `defaultPath` when it's in use and doesn't already exist. Requires `defaultPath` to be set. See [Two usage patterns](#two-usage-patterns) below. |
| `webAppMount` | No (default `/WEB-INF/classes`) | Where, within the web app, the resolved directory is mounted. |

> **Note:** At least one of `defaultPath` or `customPathPropertyName` must be set — a
> `<Listener>` with neither could never mount anything, which always indicates a
> misconfiguration (startup fails with an `IllegalStateException`). Either one alone
> is fine: `defaultPath` alone (no override possible), or `customPathPropertyName`
> alone (nothing mounted unless an operator opts in — see Pattern B below).

## Resolution order

1. If `customPathPropertyName` is set **and** that system property is itself set
   (non-blank), its value is used — and the directory is **always** created if
   missing. An operator who explicitly points at a path clearly wants it to work.
2. Otherwise, if `defaultPath` is set, it's used, and the directory is created only if
   `createDefaultPathIfMissing` is `true`. If it's `false` and the directory doesn't
   exist, nothing is mounted (logged at `INFO`) — the app's own embedded
   configuration is used as-is, rather than mounting a path that would fail Tomcat's
   own validation moments later.
3. Otherwise (no custom path in effect, and no `defaultPath` configured at all),
   nothing is mounted (logged at `INFO`).

## Two usage patterns

### Pattern A — default path always auto-created, with an operator override

Used by `ecuacion-tool-command-api` (see the "Deploying to an Existing Tomcat"
section of its
[config reference](https://references.ecuacion.jp/ecuacion-references-tools/public/showMarkdown/page?id=command-api/config&lang=en)):

```xml
<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
		customPathPropertyName="jp.ecuacion.tool.command-api.app-conf-dir"
		defaultPath="${catalina.base}/app-conf/ecuacion-tool-command-api"
		createDefaultPathIfMissing="true"/>
```

With `createDefaultPathIfMissing="true"`, the default path is always created and
mounted, even with nothing configured by the operator — a zero-setup deployment
still gets a working external config directory. The operator can still redirect it
elsewhere per deployment via the `jp.ecuacion.tool.command-api.app-conf-dir` system
property, without touching `context.xml`.

### Pattern B — nothing happens unless a custom path is explicitly given

Used by `ecuacion-tool-code-generator`:

```xml
<Listener className="jp.ecuacion.splib.core.tomcat.SplibAppConfDirLifecycleListener"
		customPathPropertyName="jp.ecuacion.tool.code-generator.app-conf-dir"/>
```

With `defaultPath` left unset entirely, there's no fallback location to fall back to
— no directory is created and nothing extra is mounted unless the operator
explicitly sets `jp.ecuacion.tool.code-generator.app-conf-dir`. Deploying the WAR
as-is, with no Tomcat-side configuration at all, works fine and simply uses the
WAR's own embedded configuration.

Which pattern to pick is an app-level choice — `createDefaultPathIfMissing` defaults
to `false`, and `defaultPath` itself is optional, precisely so that adopting this
listener never starts creating directories on disk for an app that didn't ask for
it.

## Declaring more than one `<Listener>`

A single `context.xml` can declare more than one `<Listener>` of this class for the
same `Context` — e.g. one for an app-specific directory with a configurable
override, and a second, simpler one (with `customPathPropertyName` left unset) for a
fixed, shared directory used by multiple co-located apps. Listeners fire in
declaration order, and Tomcat's `WebResourceRoot.addPreResources` appends — so a
file present in more than one mounted directory resolves from whichever
`<Listener>` was declared **first**, exactly as it would for two static
`<PreResources>` entries in declaration order.

## Lifecycle timing

Verified against `tomcat-embed-core` 11.0.21 sources (not merely inferred — Tomcat's
own reference docs don't cover this level of detail): a `<Listener>` declared in
`META-INF/context.xml` is parsed and registered onto the `Context` during
`Context.init()`, which completes entirely before `Context.start()` is ever called.
`Lifecycle.BEFORE_START_EVENT` fires from `LifecycleBase.start()` itself, before it
calls `StandardContext.startInternal()` — and `startInternal()` is what calls
`resourcesStart()`, which is what actually validates (and would otherwise reject) a
missing `PreResources` directory. So a listener already registered during `init()`
is guaranteed to see `BEFORE_START_EVENT`, and therefore to have a chance to create
the directory, before that validation ever runs.
