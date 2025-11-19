package com.cyber3d.verticalexpansion.platform;

/**
 * Platform exposes a singleton PlatformHooks instance chosen for the current patch version.
 */
public final class Platform {

    private static final PlatformHooks HOOKS = selectHooks();

    private Platform() { }

    public static PlatformHooks hooks() {
        return HOOKS;
    }

    private static PlatformHooks selectHooks() {
        int patch = PlatformVersion.patch();
        // Example split: 1.21.5+ may require different registration than earlier patches.
        if (patch >= 5) {
            return new PlatformHooks_1_21_5();
        } else {
            return new PlatformHooks_1_21_1();
        }
    }
}
