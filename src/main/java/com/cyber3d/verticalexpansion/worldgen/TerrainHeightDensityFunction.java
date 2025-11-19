package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;

public final class TerrainHeightDensityFunction {

    private final TerrainHeightFunction heightFunction;
    private final WorldTerrainProfile profile;

    public TerrainHeightDensityFunction(TerrainHeightFunction heightFunction, WorldTerrainProfile profile) {
        this.heightFunction = heightFunction;
        this.profile = profile;
    }

    public double compute(int x, int y, int z) {
        int surfaceHeight = heightFunction.computeHeight(x, z, profile);
        return (double) (surfaceHeight - y);
    }

    public double minValue() {
        return Double.NEGATIVE_INFINITY;
    }

    public double maxValue() {
        return Double.POSITIVE_INFINITY;
    }

    public TerrainHeightFunction getHeightFunction() {
        return heightFunction;
    }

    public WorldTerrainProfile getProfile() {
        return profile;
    }
}
