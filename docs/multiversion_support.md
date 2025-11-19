# Multiversion Support — Minecraft 1.21.1–1.21.10

VerticalExpansion targets multiple patch versions of the **same** major/minor Minecraft version: 1.21.1–1.21.10, running on NeoForge.

We ship **one JAR** and isolate version-sensitive behavior behind a small `PlatformHooks` abstraction.

---

## 1. Goals

- Avoid scattering version checks throughout the codebase.
- Keep core systems (terrain, features, vertical sections) version-agnostic.
- Allow small patch differences (API changes, registry hooks) to be handled in one place.
- Make it easy to add support for new patch versions.

---

## 2. PlatformVersion

We centralize version detection in `platform/PlatformVersion`:

```java
package com.cyber3d.verticalexpansion.platform;

public final class PlatformVersion {

    private static final int MC_MAJOR = 1;
    private static final int MC_MINOR = 21;
    private static final int MC_PATCH = detectPatchVersion();

    private PlatformVersion() { }

    public static int major() {
        return MC_MAJOR;
    }

    public static int minor() {
        return MC_MINOR;
    }

    public static int patch() {
        return MC_PATCH;
    }

    public static boolean isAtLeastPatch(int patch) {
        return MC_PATCH >= patch;
    }

    // TODO: implementation detail: use whatever NeoForge / MC exposes to obtain the patch version.
    private static int detectPatchVersion() {
        // For example, parse a version string or query a runtime API.
        return 1;
    }
}
```

Implementation of `detectPatchVersion` is left to the actual code; it must reliably return the current patch.

---

## 3. PlatformHooks Interface

Version-sensitive operations are exposed through `PlatformHooks`:

```java
package com.cyber3d.verticalexpansion.platform;

public interface PlatformHooks {

    /**
     * Called once early in mod initialization.
     * Use this to register worldgen, events, etc., in a version-appropriate way.
     */
    void init();

    // Example hooks that may differ across patches:

    void registerChunkGenerator();

    void registerBiomeModifiers();

    void hookBiomeLoadingEvents();

    // Integration with VerticalSectionManager, if needed:

    void registerVerticalSectionIntegration();
}
```

This interface may be extended as the mod grows, but changes should be additive and coordinated.

---

## 4. Patch-Range Implementations

We expect to have one implementation per contiguous patch range, for example:

- `PlatformHooks_1_21_1` for 1.21.1–1.21.4
- `PlatformHooks_1_21_5` for 1.21.5–1.21.10

Skeletons:

```java
package com.cyber3d.verticalexpansion.platform;

public final class PlatformHooks_1_21_1 implements PlatformHooks {

    @Override
    public void init() {
        registerChunkGenerator();
        registerBiomeModifiers();
        hookBiomeLoadingEvents();
        registerVerticalSectionIntegration();
    }

    @Override
    public void registerChunkGenerator() {
        // TODO: implement using 1.21.1-compatible worldgen registration APIs
    }

    @Override
    public void registerBiomeModifiers() {
        // TODO
    }

    @Override
    public void hookBiomeLoadingEvents() {
        // TODO
    }

    @Override
    public void registerVerticalSectionIntegration() {
        // TODO
    }
}
```

```java
package com.cyber3d.verticalexpansion.platform;

public final class PlatformHooks_1_21_5 implements PlatformHooks {

    @Override
    public void init() {
        registerChunkGenerator();
        registerBiomeModifiers();
        hookBiomeLoadingEvents();
        registerVerticalSectionIntegration();
    }

    @Override
    public void registerChunkGenerator() {
        // TODO: implement using 1.21.5+ worldgen registration APIs
    }

    @Override
    public void registerBiomeModifiers() {
        // TODO
    }

    @Override
    public void hookBiomeLoadingEvents() {
        // TODO
    }

    @Override
    public void registerVerticalSectionIntegration() {
        // TODO
    }
}
```

Core code **must not** call patch-specific APIs directly; it must go through `Platform.hooks()`.

---

## 5. Platform Selector

`Platform` provides the single entry point:

```java
package com.cyber3d.verticalexpansion.platform;

public final class Platform {

    private static final PlatformHooks HOOKS = selectHooks();

    private Platform() { }

    public static PlatformHooks hooks() {
        return HOOKS;
    }

    private static PlatformHooks selectHooks() {
        int patch = PlatformVersion.patch();
        // Example split: future patches can refine this mapping.
        if (patch >= 5) {
            return new PlatformHooks_1_21_5();
        } else {
            return new PlatformHooks_1_21_1();
        }
    }
}
```

The main mod entrypoint calls:

```java
Platform.hooks().init();
```

at startup.

---

## 6. Rules

- Any code that needs version-specific behavior **must** go through `PlatformHooks`.
- Do not inline version checks or reflection in core packages.
- If a new patch introduces breaking changes:
  1. Add a new `PlatformHooks_1_21_X` implementation.
  2. Update `Platform.selectHooks()` to route to it.
  3. Keep terrain / features / vertical logic unchanged where possible.

