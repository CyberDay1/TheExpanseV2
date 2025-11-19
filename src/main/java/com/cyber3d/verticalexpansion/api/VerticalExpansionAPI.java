package com.cyber3d.verticalexpansion.api;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.ore.OreProfile;
import com.cyber3d.verticalexpansion.ore.OreProfileRegistry;
import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;

public final class VerticalExpansionAPI {

    private VerticalExpansionAPI() {
    }

    public static OreProfileRegistry getOreRegistry() {
        return OreProfileRegistry.getInstance();
    }

    public static void registerOre(String id, OreProfile profile) {
        getOreRegistry().register(id, profile);
    }

    public interface FeatureProvider {
        void registerFeatures();
    }

    public interface TerrainProvider {
        WorldTerrainProfile provideTerrainProfile(WorldHeightConfig heightConfig);
        TerrainHeightFunction provideHeightFunction(WorldTerrainProfile profile);
    }
}
