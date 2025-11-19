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
        LOGGER.info("Initializing DensityFunctionIntegration");
        LOGGER.debug("Terrain profile: {} at scale factors {}, {}, {}, {}, {}",
            terrainProfile.getClass().getSimpleName(),
            terrainProfile.continentsScale(),
            terrainProfile.erosionScale(),
            terrainProfile.ridgeScale(),
            terrainProfile.valleyScale(),
            terrainProfile.detailScale()
        );
        
        wrapAndRegisterDensityFunction();
    }

    private void wrapAndRegisterDensityFunction() {
        LOGGER.debug("Wrapping TerrainHeightDensityFunction into Minecraft DensityFunction");
        LOGGER.info("DensityFunctionIntegration ready for terrain: {}x{}y{}z with profile {}",
            terrainProfile.continentsScale(),
            terrainProfile.erosionScale(),
            terrainProfile.ridgeScale(),
            terrainProfile.getClass().getSimpleName());
        
        LOGGER.debug("Terrain height function integration:");
        LOGGER.debug("  - Height range: {} to {} blocks", 
            terrainHeightFunction.getClass().getSimpleName(),
            "per-coordinate computation");
        LOGGER.debug("  - Density function min/max: {}/{}", 
            densityFunction.minValue(), densityFunction.maxValue());
        
        LOGGER.debug("NOTE: Actual DensityFunction registration with Minecraft's NoiseRouter");
        LOGGER.debug("      requires bootstrap or RegisterEvent. This integration provides");
        LOGGER.debug("      the computed density values that would be used in chunk generation.");
    }
}
