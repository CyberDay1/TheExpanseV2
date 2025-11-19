package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalExpansionChunkGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private final WorldHeightConfig heightConfig;
    private final WorldTerrainProfile terrainProfile;
    private TerrainHeightFunction heightFunction;
    private DensityFunction terrainDensity;

    public VerticalExpansionChunkGenerator(WorldHeightConfig heightConfig) {
        this.heightConfig = heightConfig;
        this.terrainProfile = DefaultWorldTerrainProfile.standard(heightConfig);
        
        try {
            DensityFunctionIntegration integration = DensityFunctionIntegration.getInstance();
            this.heightFunction = integration.getHeightFunction();
            this.terrainDensity = integration.getTerrainDensityFunction();
            LOGGER.info("[VerticalExpansion] ChunkGenerator wired to DensityFunctionIntegration");
        } catch (IllegalStateException e) {
            LOGGER.warn("[VerticalExpansion] DensityFunctionIntegration not initialized, using fallback", e);
            this.heightFunction = new TerrainHeightFunction() {
                @Override
                public int computeHeight(int x, int z, WorldTerrainProfile profile) {
                    return (heightConfig.minY() + heightConfig.maxY()) / 2;
                }

                @Override
                public double computeContinentalness(int x, int z, WorldTerrainProfile profile) {
                    return 0.0;
                }
            };
            this.terrainDensity = null;
        }
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

    public DensityFunction getTerrainDensity() {
        return terrainDensity;
    }

    public double getDensityAt(int x, int y, int z) {
        if (terrainDensity != null) {
            return terrainDensity.compute(new DensityFunction.SinglePointContext(x, y, z));
        }
        return 0.0;
    }
}
