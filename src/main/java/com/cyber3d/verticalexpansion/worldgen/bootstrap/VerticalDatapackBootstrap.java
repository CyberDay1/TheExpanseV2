package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.worldgen.DensityFunctionIntegration;
import com.cyber3d.verticalexpansion.worldgen.WorldGenInitializer;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registers custom DensityFunctions into the minecraft:worldgen/density_function
 * registry so they can safely be referenced from datapack JSON.
 *
 * Fixes the “Unbound values in registry” crash when clicking Singleplayer.
 */
@EventBusSubscriber(modid = "verticalexpansion", bus = EventBusSubscriber.Bus.MOD)
public final class VerticalDatapackBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private VerticalDatapackBootstrap() {}

    @SubscribeEvent
    public static void onRegister(RegisterEvent event) {

        if (!event.getRegistryKey().equals(Registries.DENSITY_FUNCTION)) {
            return;
        }

        event.register(Registries.DENSITY_FUNCTION, helper -> {

            // Ensure worldgen system & integration are ready
            if (!WorldGenInitializer.isInitialized()) {
                if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
                    LOGGER.debug("[VerticalExpansion] Initializing worldgen for density function bootstrap");
                }
                WorldGenInitializer.initialize();
            }

            DensityFunctionIntegration integration = WorldGenInitializer.getDensityFunctionIntegration();

            DensityFunction terrainFn = integration.getTerrainDensityFunction();
            DensityFunction continentalFn = integration.getContinentalnessDensityFunction();

            LOGGER.info("[VerticalExpansion] Registering DensityFunctions:");
            LOGGER.info("  - {}", VerticalDensityBootstrap.TERRAIN_HEIGHT.location());
            LOGGER.info("  - {}", VerticalDensityBootstrap.CONTINENTALNESS.location());

            // Actual registry registrations — this is the missing piece.
            helper.register(
                    VerticalDensityBootstrap.TERRAIN_HEIGHT.location(),
                    terrainFn
            );

            helper.register(
                    VerticalDensityBootstrap.CONTINENTALNESS.location(),
                    continentalFn
            );

            if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
                LOGGER.debug("[VerticalExpansion] DensityFunction registry bootstrap complete");
            }
        });
    }
}
