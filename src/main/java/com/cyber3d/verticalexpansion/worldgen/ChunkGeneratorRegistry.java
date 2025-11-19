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
