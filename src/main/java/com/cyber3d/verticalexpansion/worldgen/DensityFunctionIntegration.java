package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DensityFunctionIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    
    private final TerrainHeightDensityFunction densityFunction;
    private final TerrainHeightFunction terrainHeightFunction;
    private final WorldTerrainProfile terrainProfile;

    public DensityFunctionIntegration(
            TerrainHeightFunction terrainHeightFunction,
            WorldTerrainProfile terrainProfile
    ) {
        this.terrainHeightFunction = terrainHeightFunction;
        this.terrainProfile = terrainProfile;
        this.densityFunction = new TerrainHeightDensityFunction(
            terrainHeightFunction,
            terrainProfile
        );
    }

    public TerrainHeightDensityFunction getDensityFunction() {
        return densityFunction;
    }

    public TerrainHeightFunction getHeightFunction() {
        return terrainHeightFunction;
    }

    public WorldTerrainProfile getTerrainProfile() {
        return terrainProfile;
    }

    public void initialize() {
        LOGGER.info("Initialized DensityFunctionIntegration");
        LOGGER.debug("Terrain profile: {} at scale factors {}, {}, {}, {}, {}",
            terrainProfile.getClass().getSimpleName(),
            terrainProfile.continentsScale(),
            terrainProfile.erosionScale(),
            terrainProfile.ridgeScale(),
            terrainProfile.valleyScale(),
            terrainProfile.detailScale()
        );
    }
}
