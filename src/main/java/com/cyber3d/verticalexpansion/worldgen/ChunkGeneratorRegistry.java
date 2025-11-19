package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import net.minecraft.world.level.levelgen.NoiseSettings;
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
        LOGGER.info("[VerticalExpansion] Preparing world preset registration");
        
        WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
        
        LOGGER.info("World preset configuration:");
        LOGGER.info("  - Name: verticalexpansion:expanded_terrain");
        LOGGER.info("  - Min Y: {}, Max Y: {}, Sea Level: {}", 
            config.minY(), config.maxY(), config.seaLevel());
        LOGGER.info("  - Uses DensityFunctionIntegration for height computation");
        LOGGER.info("  - Would be available in world creation menu as custom preset");
        
        LOGGER.debug("TODO: Create a WorldPreset / LevelStem object that:");
        LOGGER.debug("  - Uses the NoiseSettings defined in registerNoiseSettings()");
        LOGGER.debug("  - Uses the standard overworld biome source or a custom one if desired");
        LOGGER.debug("  - Uses a chunk generator wired to our density function");
        LOGGER.debug("");
        LOGGER.debug("ResourceKey<WorldPreset> key = ResourceKey.create(Registries.WORLD_PRESET,");
        LOGGER.debug("  new ResourceLocation(\"verticalexpansion\", \"expanded_terrain\"));");
        LOGGER.debug("");
        LOGGER.debug("Register via bootstrap/RegisterEvent when entry point is clear.");
    }

    private static void registerNoiseSettings() {
        WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
        
        LOGGER.info("[VerticalExpansion] Building NoiseSettings for extended vertical range");
        LOGGER.info("Noise settings: minY={}, maxY={}, seaLevel={}, totalRange={}",
            config.minY(), config.maxY(), config.seaLevel(),
            config.maxY() - config.minY());
        
        int minY = config.minY();
        int height = config.maxY() - config.minY();
        
        NoiseSettings settings = new NoiseSettings(
            minY,
            height,
            64,
            64
        );
        
        LOGGER.debug("Constructed NoiseSettings with:");
        LOGGER.debug("  - minY: {}", minY);
        LOGGER.debug("  - height: {}", height);
        LOGGER.debug("  - sea level: {}", config.seaLevel());
        
        LOGGER.debug("NOTE: Actual NoiseSettings registration with Minecraft's registry");
        LOGGER.debug("      requires bootstrap or RegisterEvent. Resource key should be:");
        LOGGER.debug("      ResourceKey.create(Registries.NOISE_SETTINGS,");
        LOGGER.debug("        new ResourceLocation(\"verticalexpansion\", \"vertical_overworld\"))");
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
