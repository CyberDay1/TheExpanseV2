package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;

public final class VerticalExpansionChunkGenerator {

    private final WorldHeightConfig heightConfig;
    private final WorldTerrainProfile terrainProfile;
    private final TerrainHeightFunction heightFunction;

    public VerticalExpansionChunkGenerator(WorldHeightConfig heightConfig) {
        this.heightConfig = heightConfig;
        this.terrainProfile = DefaultWorldTerrainProfile.standard(heightConfig);
        this.heightFunction = new TerrainHeightFunction() {
            @Override
            public int computeHeight(int x, int z, WorldTerrainProfile profile) {
                return (heightConfig.minY() + heightConfig.maxY()) / 2;
            }
        };
    }

    public WorldHeightConfig getHeightConfig() {
        return heightConfig;
    }

    public WorldTerrainProfile getTerrainProfile() {
        return terrainProfile;
    }

    public TerrainHeightFunction getHeightFunction() {
        return heightFunction;
    }

    public int getMinY() {
        return heightConfig.minY();
    }

    public int getMaxY() {
        return heightConfig.maxY();
    }

    public int getHeight(int x, int z) {
        return heightFunction.computeHeight(x, z, terrainProfile);
    }
}
