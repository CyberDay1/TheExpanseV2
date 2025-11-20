package com.cyber3d.verticalexpansion.worldgen;

import java.util.HashMap;
import java.util.Map;

/**
 * BiomePalette maps altitude bands to biomes.
 *
 * With deep ocean support enabled, the entire water column from
 * (seaLevel - deepOceanDepth) up to sea level is treated as the ocean band.
 * Only the space below that deepest ocean floor is considered the "deep" band.
 */
public final class BiomePalette {

    private final Map<Integer, String> bandToBiome = new HashMap<>();
    private final int seaLevel;
    private final int deepOceanDepth;

    /**
     * Legacy constructor – defaults deep ocean depth to 128 blocks.
     */
    public BiomePalette(int seaLevel) {
        this(seaLevel, 128);
    }

    /**
     * Preferred constructor – pass in the terrain profile's deepOceanDepth so
     * biome bands line up with actual ocean floor depth.
     */
    public BiomePalette(int seaLevel, int deepOceanDepth) {
        this.seaLevel = seaLevel;
        this.deepOceanDepth = Math.max(0, deepOceanDepth);
        initializeBiomeMappings();
    }

    private void initializeBiomeMappings() {
        bandToBiome.put(0, "minecraft:deep_dark");
        bandToBiome.put(1, "minecraft:ocean");
        bandToBiome.put(2, "minecraft:plains");
        bandToBiome.put(3, "minecraft:mountains");
        bandToBiome.put(4, "minecraft:snowy_peaks");
    }

    public String getBiomeForBand(int band) {
        return bandToBiome.getOrDefault(band, "minecraft:plains");
    }

    /**
     * Map a world Y to a biome band using sea level and the configured
     * deep-ocean depth:
     *
     *   - Band 4 (snowy_peaks): y >= seaLevel + 256
     *   - Band 3 (mountains) : y >= seaLevel + 128
     *   - Band 2 (plains)    : seaLevel <= y < seaLevel + 128
     *   - Band 1 (ocean)     : deepOceanFloorY <= y < seaLevel
     *   - Band 0 (deep_dark) : y < deepOceanFloorY
     *
     * This guarantees that *all* blocks under the sea surface down to the
     * deepest ocean floor are tagged as ocean, instead of flipping to
     * deep_dark/land biomes below an arbitrary height.
     */
    public String getBiomeForHeight(int height) {
        int deepOceanFloorY = seaLevel - deepOceanDepth;

        if (height >= seaLevel + 256) {
            // Sky / extreme peaks
            return bandToBiome.get(4);
        } else if (height >= seaLevel + 128) {
            // Mountains
            return bandToBiome.get(3);
        } else if (height >= seaLevel) {
            // Surface band (plains / land biomes)
            return bandToBiome.get(2);
        } else if (height >= deepOceanFloorY) {
            // Entire water column above the deepest ocean floor
            return bandToBiome.get(1);
        } else {
            // Extreme depths below the deepest ocean floor
            return bandToBiome.get(0);
        }
    }

    public void setBiomeForBand(int band, String biomeName) {
        bandToBiome.put(band, biomeName);
    }

    public int getSeaLevel() {
        return seaLevel;
    }

    public int getDeepOceanDepth() {
        return deepOceanDepth;
    }
}
