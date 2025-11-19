package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import com.cyber3d.verticalexpansion.worldgen.DensityFunctionIntegration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalDensityBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public static final ResourceKey<DensityFunction> TERRAIN_HEIGHT =
        ResourceKey.create(Registries.DENSITY_FUNCTION,
            ResourceLocation.parse("verticalexpansion:terrain_height"));

    public static void initialize() {
        LOGGER.info("[VerticalExpansion] DensityFunction bootstrap:");
        LOGGER.info("  - ResourceKey: {}", TERRAIN_HEIGHT);
        
        try {
            DensityFunction function = DensityFunctionIntegration.getInstance().getTerrainDensityFunction();
            LOGGER.info("  - Terrain density function ready");
            LOGGER.debug("    Min: {}, Max: {}", function.minValue(), function.maxValue());
        } catch (IllegalStateException e) {
            LOGGER.warn("  - DensityFunctionIntegration not yet initialized", e);
        }
        
        LOGGER.debug("TODO: Register {} into Minecraft DensityFunction registry via NeoForge bootstrap", TERRAIN_HEIGHT);
    }

    private VerticalDensityBootstrap() {
    }
}
