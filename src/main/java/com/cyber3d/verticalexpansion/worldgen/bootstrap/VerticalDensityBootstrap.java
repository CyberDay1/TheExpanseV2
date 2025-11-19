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

    public static final ResourceKey<DensityFunction> CONTINENTALNESS =
        ResourceKey.create(Registries.DENSITY_FUNCTION,
            ResourceLocation.parse("verticalexpansion:continentalness"));

    public static void initialize() {
        LOGGER.info("[VerticalExpansion] DensityFunction bootstrap:");
        LOGGER.info("  - Terrain: {}", TERRAIN_HEIGHT);
        LOGGER.info("  - Continentalness: {}", CONTINENTALNESS);
        
        try {
            DensityFunctionIntegration integration = DensityFunctionIntegration.getInstance();
            DensityFunction terrainFunction = integration.getTerrainDensityFunction();
            DensityFunction continentalFunction = integration.getContinentalnessDensityFunction();
            
            LOGGER.info("  - Terrain density function ready");
            LOGGER.debug("    Min: {}, Max: {}", terrainFunction.minValue(), terrainFunction.maxValue());
            LOGGER.info("  - Continentalness density function ready");
            LOGGER.debug("    Min: {}, Max: {}", continentalFunction.minValue(), continentalFunction.maxValue());
        } catch (IllegalStateException e) {
            LOGGER.warn("  - DensityFunctionIntegration not yet initialized", e);
        }
    }

    private VerticalDensityBootstrap() {
    }
}
