package com.cyber3d.verticalexpansion.core;

public final class VerticalExpansionConfig {

    private static WorldHeightConfig worldHeightConfig = DefaultWorldHeightConfig.standard();

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
}
