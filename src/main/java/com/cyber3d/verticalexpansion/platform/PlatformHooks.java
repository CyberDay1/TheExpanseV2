package com.cyber3d.verticalexpansion.platform;

/**
 * PlatformHooks encapsulates all version-sensitive interactions with NeoForge / Minecraft.
 *
 * Core code should call Platform.hooks() and use these methods instead of directly referencing
 * version-specific APIs.
 */
public interface PlatformHooks {

    /**
     * Called once early in mod initialization.
     * Implementations should register worldgen, events, etc., in a version-appropriate way.
     */
    void init();

    void registerChunkGenerator();

    void registerBiomeModifiers();

    void hookBiomeLoadingEvents();

    void registerVerticalSectionIntegration();
    
    void registerRenderingCulling();
}
