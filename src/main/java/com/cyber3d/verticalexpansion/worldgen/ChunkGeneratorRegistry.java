package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChunkGeneratorRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static VerticalExpansionChunkGenerator ACTIVE_GENERATOR = null;

    private ChunkGeneratorRegistry() {
    }

    public static void registerChunkGenerator() {
        WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
        
        LOGGER.info("Registering VerticalExpansion chunk generator");
        LOGGER.debug("World height config: minY={}, maxY={}, seaLevel={}", 
            config.minY(), config.maxY(), config.seaLevel());
        
        registerWorldPreset();
        registerNoiseSettings();
    }

    private static void registerWorldPreset() {
        LOGGER.debug("Registering VerticalExpansion world preset");
        LOGGER.debug("  - Preset would be registered as: verticalexpansion:expanded_terrain");
        LOGGER.debug("  - Uses DensityFunctionIntegration for height computation");
        LOGGER.debug("  - Would be available in world creation menu as custom preset");
        LOGGER.debug("  - Requires WorldPreset or LevelStem registration via bootstrap/RegisterEvent");
    }

    private static void registerNoiseSettings() {
        WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
        LOGGER.debug("Registering VerticalExpansion noise settings");
        LOGGER.debug("  - Min Y: {} / Max Y: {}", config.minY(), config.maxY());
        LOGGER.debug("  - Sea Level: {}", config.seaLevel());
        LOGGER.debug("  - Height range: {} blocks", (config.maxY() - config.minY() + 1));
        LOGGER.debug("  - Would integrate with Minecraft's NoiseGeneratorSettings registry");
        LOGGER.debug("  - Requires registration via BootstrapContext or RegisterEvent");
    }

    public static VerticalExpansionChunkGenerator getActiveGenerator() {
        return ACTIVE_GENERATOR;
    }

    public static void setActiveGenerator(VerticalExpansionChunkGenerator generator) {
        ACTIVE_GENERATOR = generator;
        LOGGER.debug("Set active chunk generator: {}", generator != null ? "yes" : "null");
    }

    public static boolean isVerticalExpansionWorld() {
        return ACTIVE_GENERATOR != null;
    }
}
