package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;

public final class VerticalExpansionBiomeSource {

    private final TerrainHeightFunction heightFunction;
    private final WorldTerrainProfile profile;

    public VerticalExpansionBiomeSource(
            TerrainHeightFunction heightFunction,
            WorldTerrainProfile profile
    ) {
        this.heightFunction = heightFunction;
        this.profile = profile;
    }

    public int getHeight(int x, int z) {
        return heightFunction.computeHeight(x, z, profile);
    }

    public int getBand(int height) {
        if (height >= profile.skyBandStartY()) {
            return 4;
        } else if (height >= profile.extremeStartY()) {
            return 3;
        } else if (height >= profile.highlandsStartY()) {
            return 2;
        } else if (height >= profile.seaLevel()) {
            return 1;
        } else {
            return 0;
        }
    }

    public TerrainHeightFunction getHeightFunction() {
        return heightFunction;
    }

    public WorldTerrainProfile getProfile() {
        return profile;
    }
}
