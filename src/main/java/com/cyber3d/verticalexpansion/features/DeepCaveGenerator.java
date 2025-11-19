package com.cyber3d.verticalexpansion.features;

import com.cyber3d.verticalexpansion.worldgen.BiomePalette;
import com.cyber3d.verticalexpansion.worldgen.StructurePlacementHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DeepCaveGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private final int minDepth;
    private final int maxDepth;
    private final float frequency;
    private final BiomePalette biomePalette;
    private int cavesGenerated = 0;

    public DeepCaveGenerator(BiomePalette biomePalette) {
        this.biomePalette = biomePalette;
        int seaLevel = biomePalette.getSeaLevel();
        this.minDepth = seaLevel - 256;
        this.maxDepth = seaLevel - 128;
        this.frequency = 0.25f;
        LOGGER.debug("DeepCaveGenerator initialized: depthRange=[{}, {}], frequency={}", 
                minDepth, maxDepth, frequency);
    }

    public boolean canPlaceCaveAt(int x, int y, int z) {
        if (y < minDepth || y > maxDepth) {
            return false;
        }

        int seaLevel = biomePalette.getSeaLevel();
        if (!StructurePlacementHooks.isValidPlacementHeight(
                StructurePlacementHooks.StructureType.ANCIENT_CITY, y, seaLevel)) {
            return false;
        }

        return true;
    }

    public void placeCave(int x, int y, int z) {
        cavesGenerated++;
        StructurePlacementHooks.logStructurePlacement(
                StructurePlacementHooks.StructureType.ANCIENT_CITY, x, y, z);
        LOGGER.debug("Deep cave system placed at ({}, {}, {})", x, y, z);
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

    public int getCavesGenerated() {
        return cavesGenerated;
    }
}
