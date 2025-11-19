package com.cyber3d.verticalexpansion.vertical;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;

public final class DefaultServerVerticalConfig implements ServerVerticalConfig {

    private final WorldHeightConfig heightConfig;
    private final int verticalSectionWindow;
    private final int maxActiveSectionsPerColumn;

    public DefaultServerVerticalConfig(
            WorldHeightConfig heightConfig,
            int verticalSectionWindow,
            int maxActiveSectionsPerColumn
    ) {
        this.heightConfig = heightConfig;
        this.verticalSectionWindow = verticalSectionWindow;
        this.maxActiveSectionsPerColumn = maxActiveSectionsPerColumn;
    }

    public static DefaultServerVerticalConfig standard(WorldHeightConfig heightConfig) {
        return new DefaultServerVerticalConfig(heightConfig, 3, 12);
    }

    @Override
    public WorldHeightConfig worldHeightConfig() {
        return heightConfig;
    }

    @Override
    public int verticalSectionWindow() {
        return verticalSectionWindow;
    }

    @Override
    public int maxActiveSectionsPerColumn() {
        return maxActiveSectionsPerColumn;
    }
}
