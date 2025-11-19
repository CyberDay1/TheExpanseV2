package com.cyber3d.verticalexpansion.features;

import com.cyber3d.verticalexpansion.worldgen.BiomePalette;
import com.cyber3d.verticalexpansion.worldgen.StructurePlacementHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MegaTreeGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private final TreeFeatureConfig config;
    private final BiomePalette biomePalette;
    private int treesGenerated = 0;

    public MegaTreeGenerator(TreeFeatureConfig config, BiomePalette biomePalette) {
        this.config = config;
        this.biomePalette = biomePalette;
        LOGGER.debug("MegaTreeGenerator initialized with config: minHeight={}, maxHeight={}, frequency={}, biomeFilter={}",
                config.minHeight(), config.maxHeight(), config.frequency(), config.biomeFilter());
    }

    public boolean canPlaceTreeAt(int x, int y, int z, String biomeName) {
        if (!isValidBiome(biomeName)) {
            return false;
        }

        if (y < config.minHeight() || y > config.maxHeight()) {
            return false;
        }

        int seaLevel = biomePalette.getSeaLevel();
        if (!StructurePlacementHooks.isValidPlacementHeight(
                StructurePlacementHooks.StructureType.PILLAGER_OUTPOST, y, seaLevel)) {
            return false;
        }

        return true;
    }

    public void placeTree(int x, int y, int z) {
        treesGenerated++;
        StructurePlacementHooks.logStructurePlacement(
                StructurePlacementHooks.StructureType.PILLAGER_OUTPOST, x, y, z);
        LOGGER.debug("Mega tree placed at ({}, {}, {})", x, y, z);
    }

    public float getFrequency() {
        return config.frequency();
    }

    public int getMinHeight() {
        return config.minHeight();
    }

    public int getMaxHeight() {
        return config.maxHeight();
    }

    public int getTreesGenerated() {
        return treesGenerated;
    }

    private boolean isValidBiome(String biomeName) {
        if (config.biomeFilter() == null || config.biomeFilter().isEmpty()) {
            return true;
        }

        return biomeName.contains(config.biomeFilter());
    }
}
