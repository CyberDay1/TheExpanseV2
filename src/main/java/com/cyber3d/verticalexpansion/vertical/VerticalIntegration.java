package com.cyber3d.verticalexpansion.vertical;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wires VerticalSectionManager into the server lifecycle and tick events.
 * 
 * This is the integration point between the vertical section system and NeoForge events.
 * Version-specific event/lifecycle APIs are routed through separate methods so that
 * PlatformHooks_* implementations can call the appropriate version.
 */
public final class VerticalIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static VerticalSectionManager manager = null;

    private VerticalIntegration() {
    }

    /**
     * Initialize and register VerticalSectionManager for MC 1.21.1–1.21.4.
     */
    public static void register_1_21_1() {
        LOGGER.info("[VerticalExpansion] Registering VerticalSectionManager integration for 1.21.1–1.21.4");
        initializeManager();
    }

    /**
     * Initialize and register VerticalSectionManager for MC 1.21.5+.
     */
    public static void register_1_21_5() {
        LOGGER.info("[VerticalExpansion] Registering VerticalSectionManager integration for 1.21.5+");
        initializeManager();
    }

    private static void initializeManager() {
        if (manager != null) {
            LOGGER.debug("VerticalSectionManager already initialized");
            return;
        }

        ServerVerticalConfig config = DefaultServerVerticalConfig.standard(
            VerticalExpansionConfig.getWorldHeightConfig()
        );
        VerticalSectionPolicy policy = new SimpleVerticalSectionPolicy();
        manager = new VerticalSectionManager(policy, config);
        
        LOGGER.info("VerticalSectionManager initialized with vertical window: {} sections", 
            config.verticalSectionWindow());
    }

    /**
     * Get the active VerticalSectionManager instance.
     * Returns null if not yet initialized.
     */
    public static VerticalSectionManager getManager() {
        return manager;
    }

    /**
     * Called once per server tick to update section activity states.
     * This is hooked into NeoForge's ServerTickEvent via VerticalTickEvents.
     */
    public static void onServerTick() {
        if (manager == null) {
            return;
        }
        
        manager.tick();
    }
}
