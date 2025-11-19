package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalWorldPresetBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public static final ResourceKey<WorldPreset> VERTICAL_OVERWORLD =
        ResourceKey.create(Registries.WORLD_PRESET,
            ResourceLocation.parse("verticalexpansion:vertical_overworld"));

    public static void initialize() {
        LOGGER.info("[VerticalExpansion] WorldPreset bootstrap:");
        LOGGER.info("  - ResourceKey: {}", VERTICAL_OVERWORLD);
        
        LOGGER.debug("TODO: Build a WorldPreset that:");
        LOGGER.debug("  - Uses VerticalNoiseSettingsBootstrap.VERTICAL_NOISE");
        LOGGER.debug("  - Uses standard/vanilla biome source");
        LOGGER.debug("  - Uses chunk generator that will use our DensityFunction");
    }

    private VerticalWorldPresetBootstrap() {
    }
}
