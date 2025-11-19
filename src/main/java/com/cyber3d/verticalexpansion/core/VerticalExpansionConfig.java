package com.cyber3d.verticalexpansion.core;

public final class VerticalExpansionConfig {

    private static WorldHeightConfig worldHeightConfig = DefaultWorldHeightConfig.standard();
    private static boolean debugLoggingEnabled = false;

    private VerticalExpansionConfig() {
    }

    public static WorldHeightConfig getWorldHeightConfig() {
        return worldHeightConfig;
    }

    public static void setWorldHeightConfig(WorldHeightConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("WorldHeightConfig cannot be null");
        }
        worldHeightConfig = config;
    }

    public static boolean isDebugLoggingEnabled() {
        return debugLoggingEnabled;
    }

    public static void setDebugLoggingEnabled(boolean enabled) {
        debugLoggingEnabled = enabled;
    }
}
