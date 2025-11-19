package com.cyber3d.verticalexpansion.worldgen.bootstrap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public static void initialize() {
        LOGGER.info("[VerticalExpansion] Bootstrap registration stub");
        LOGGER.debug("TODO: Wire bootstrap registrations into NeoForge event system");
        VerticalDensityBootstrap.initialize();
        VerticalNoiseSettingsBootstrap.initialize();
        VerticalWorldPresetBootstrap.initialize();
    }

    private VerticalBootstrap() {
    }
}
