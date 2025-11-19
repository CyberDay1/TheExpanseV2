package com.cyber3d.verticalexpansion.platform;

import com.cyber3d.verticalexpansion.features.FeatureRegistry;
import com.cyber3d.verticalexpansion.ore.OreProfileRegistry;
import com.cyber3d.verticalexpansion.ore.VanillaOresProvider;
import com.cyber3d.verticalexpansion.vertical.VerticalIntegration;
import com.cyber3d.verticalexpansion.worldgen.WorldGenInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PlatformHooks_1_21_1 implements PlatformHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    @Override
    public void init() {
        LOGGER.info("Initializing VerticalExpansion for Minecraft 1.21.1–1.21.4");
        registerOreProfiles();
        initializeWorldGen();
        initializeFeatures();
        registerChunkGenerator();
        registerBiomeModifiers();
        hookBiomeLoadingEvents();
        registerVerticalSectionIntegration();
        LOGGER.info("VerticalExpansion initialization complete");
    }

    private void registerOreProfiles() {
        LOGGER.debug("Registering vanilla ore profiles");
        VanillaOresProvider.registerVanillaOres(OreProfileRegistry.getInstance());
    }

    private void initializeWorldGen() {
        LOGGER.debug("Initializing world generation system for 1.21.1–1.21.4");
        WorldGenInitializer.initialize();
    }

    private void initializeFeatures() {
        LOGGER.debug("Initializing features for 1.21.1–1.21.4");
        FeatureRegistry.getInstance().initialize(WorldGenInitializer.getBiomePalette());
    }

    @Override
    public void registerChunkGenerator() {
        WorldGenInitializer.registerChunkGenerator_1_21_1();
    }

    @Override
    public void registerBiomeModifiers() {
        WorldGenInitializer.registerBiomeModifiers_1_21_1();
    }

    @Override
    public void hookBiomeLoadingEvents() {
        WorldGenInitializer.registerBiomeLoadingEvents_1_21_1();
    }

    @Override
    public void registerVerticalSectionIntegration() {
        VerticalIntegration.register_1_21_1();
    }
}
