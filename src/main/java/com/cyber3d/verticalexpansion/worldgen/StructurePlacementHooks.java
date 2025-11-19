package com.cyber3d.verticalexpansion.worldgen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class StructurePlacementHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    public enum StructureType {
        STRONGHOLD,
        VILLAGE,
        SHIPWRECK,
        OCEAN_MONUMENT,
        FORTRESS,
        JUNGLE_TEMPLE,
        DESERT_TEMPLE,
        SWAMP_HUT,
        PILLAGER_OUTPOST,
        ANCIENT_CITY,
        DEEP_DARK_CITY
    }

    private StructurePlacementHooks() {
    }

    public static boolean isValidPlacementHeight(StructureType type, int y, int seaLevel) {
        return switch (type) {
            case STRONGHOLD -> y >= seaLevel - 128 && y <= seaLevel + 64;
            case VILLAGE -> y >= seaLevel - 64 && y <= seaLevel + 128;
            case SHIPWRECK -> y >= seaLevel - 128 && y <= seaLevel;
            case OCEAN_MONUMENT -> y >= seaLevel - 256 && y <= seaLevel;
            case FORTRESS -> y >= seaLevel - 256 && y <= seaLevel - 64;
            case JUNGLE_TEMPLE -> y >= seaLevel && y <= seaLevel + 200;
            case DESERT_TEMPLE -> y >= seaLevel && y <= seaLevel + 200;
            case SWAMP_HUT -> y >= seaLevel - 16 && y <= seaLevel + 128;
            case PILLAGER_OUTPOST -> y >= seaLevel && y <= seaLevel + 256;
            case ANCIENT_CITY -> y >= seaLevel - 256 && y <= seaLevel - 128;
            case DEEP_DARK_CITY -> y >= seaLevel - 256 && y <= seaLevel - 64;
        };
    }

    public static void logStructurePlacement(StructureType type, int x, int y, int z) {
        LOGGER.debug("Structure placement: {} at ({}, {}, {})", type, x, y, z);
    }

    public static int adjustHeightForStructure(StructureType type, int originalY, int seaLevel) {
        if (!isValidPlacementHeight(type, originalY, seaLevel)) {
            return switch (type) {
                case STRONGHOLD -> Math.min(Math.max(originalY, seaLevel - 128), seaLevel + 64);
                case VILLAGE -> Math.min(Math.max(originalY, seaLevel - 64), seaLevel + 128);
                case SHIPWRECK -> Math.min(Math.max(originalY, seaLevel - 128), seaLevel);
                case OCEAN_MONUMENT -> Math.min(Math.max(originalY, seaLevel - 256), seaLevel);
                case FORTRESS -> Math.min(Math.max(originalY, seaLevel - 256), seaLevel - 64);
                case JUNGLE_TEMPLE, DESERT_TEMPLE -> Math.min(Math.max(originalY, seaLevel), seaLevel + 200);
                case SWAMP_HUT -> Math.min(Math.max(originalY, seaLevel - 16), seaLevel + 128);
                case PILLAGER_OUTPOST -> Math.min(Math.max(originalY, seaLevel), seaLevel + 256);
                case ANCIENT_CITY -> Math.min(Math.max(originalY, seaLevel - 256), seaLevel - 128);
                case DEEP_DARK_CITY -> Math.min(Math.max(originalY, seaLevel - 256), seaLevel - 64);
            };
        }
        return originalY;
    }
}
