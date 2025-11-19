package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public static void initialize() {
        LOGGER.info("[VerticalExpansion] Initializing worldgen bootstrap registrations");
        VerticalDensityBootstrap.initialize();
        VerticalNoiseSettingsBootstrap.initialize();
        VerticalWorldPresetBootstrap.initialize();
        LOGGER.info("[VerticalExpansion] Worldgen configuration complete");
    }

    private VerticalBootstrap() {
    }
}
