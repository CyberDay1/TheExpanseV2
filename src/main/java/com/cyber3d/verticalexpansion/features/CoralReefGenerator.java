package com.cyber3d.verticalexpansion.features;

import com.cyber3d.verticalexpansion.worldgen.BiomePalette;
import com.cyber3d.verticalexpansion.worldgen.StructurePlacementHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CoralReefGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private final int minDepth;
    private final int maxDepth;
    private final float frequency;
    private final BiomePalette biomePalette;
    private int reefsGenerated = 0;

    public CoralReefGenerator(BiomePalette biomePalette) {
        this.biomePalette = biomePalette;
        int seaLevel = biomePalette.getSeaLevel();
        this.minDepth = seaLevel - 128;
        this.maxDepth = seaLevel;
        this.frequency = 0.15f;
        LOGGER.debug("CoralReefGenerator initialized: depthRange=[{}, {}], frequency={}", 
                minDepth, maxDepth, frequency);
    }

    public boolean canPlaceReefAt(int x, int y, int z, String biomeName) {
        if (!isOceanBiome(biomeName)) {
            return false;
        }

        if (y < minDepth || y > maxDepth) {
            return false;
        }

        int seaLevel = biomePalette.getSeaLevel();
        if (!StructurePlacementHooks.isValidPlacementHeight(
                StructurePlacementHooks.StructureType.OCEAN_MONUMENT, y, seaLevel)) {
            return false;
        }

        return true;
    }

    public void placeReef(int x, int y, int z) {
        reefsGenerated++;
        StructurePlacementHooks.logStructurePlacement(
                StructurePlacementHooks.StructureType.OCEAN_MONUMENT, x, y, z);
        LOGGER.debug("Coral reef placed at ({}, {}, {})", x, y, z);
    }

    public float getFrequency() {
        return frequency;
    }

    public int getMinDepth() {
        return minDepth;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public int getReefsGenerated() {
        return reefsGenerated;
    }

    private boolean isOceanBiome(String biomeName) {
        return biomeName.contains("ocean") || biomeName.contains("deep_dark");
    }
}
