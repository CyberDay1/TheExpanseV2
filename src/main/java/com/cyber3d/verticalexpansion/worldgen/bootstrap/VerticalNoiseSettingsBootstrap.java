package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalNoiseSettingsBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public static final ResourceKey<?> VERTICAL_NOISE =
        ResourceKey.create(Registries.NOISE_SETTINGS,
            ResourceLocation.parse("verticalexpansion:vertical_noise"));

    public static void initialize() {
        var cfg = VerticalExpansionConfig.getWorldHeightConfig();
        
        LOGGER.info("[VerticalExpansion] NoiseSettings bootstrap:");
        LOGGER.info("  - ResourceKey: {}", VERTICAL_NOISE);
        LOGGER.info("  - Min Y: {}, Max Y: {}, Height: {}", 
            cfg.minY(), cfg.maxY(), cfg.maxY() - cfg.minY());
        
        LOGGER.info("  - Loaded from: data/verticalexpansion/worldgen/noise_settings/vertical_noise.json");
        LOGGER.debug("  - Uses terrain_height density function for final terrain");
        LOGGER.debug("  - Uses continentalness density function for biome placement");
    }

    private VerticalNoiseSettingsBootstrap() {
    }
}
