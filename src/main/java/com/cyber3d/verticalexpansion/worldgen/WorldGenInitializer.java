package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.NoiseBasedTerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.PerlinNoiseSampler;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldGenInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static DensityFunctionIntegration densityFunctionIntegration;
    private static BiomePalette biomePalette;
    private static boolean initialized = false;

    private WorldGenInitializer() {
    }

    public static void initialize() {
        if (initialized) {
            LOGGER.debug("WorldGenInitializer already initialized");
            return;
        }

        LOGGER.info("Initializing VerticalExpansion world generation system");

        WorldHeightConfig heightConfig = VerticalExpansionConfig.getWorldHeightConfig();
        WorldTerrainProfile terrainProfile = DefaultWorldTerrainProfile.standard(heightConfig);

        NoiseBasedTerrainHeightFunction heightFunction = new NoiseBasedTerrainHeightFunction(
            new PerlinNoiseSampler(null, terrainProfile.continentsScale()),
            new PerlinNoiseSampler(null, terrainProfile.erosionScale()),
            new PerlinNoiseSampler(null, terrainProfile.ridgeScale()),
            new PerlinNoiseSampler(null, terrainProfile.valleyScale()),
            new PerlinNoiseSampler(null, terrainProfile.detailScale())
        );

        densityFunctionIntegration = new DensityFunctionIntegration(heightFunction, terrainProfile);
        densityFunctionIntegration.initialize();

        biomePalette = new BiomePalette(heightConfig.seaLevel());
        LOGGER.debug("BiomePalette initialized with sea level: {}", heightConfig.seaLevel());

        ChunkGeneratorRegistry.registerChunkGenerator();

        initialized = true;
        LOGGER.info("VerticalExpansion world generation system initialized successfully");
    }

    public static DensityFunctionIntegration getDensityFunctionIntegration() {
        if (!initialized) {
            initialize();
        }
        return densityFunctionIntegration;
    }

    public static BiomePalette getBiomePalette() {
        if (!initialized) {
            initialize();
        }
        return biomePalette;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void reset() {
        densityFunctionIntegration = null;
        biomePalette = null;
        initialized = false;
        LOGGER.debug("WorldGenInitializer reset");
    }
}
