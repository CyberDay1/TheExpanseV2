package com.cyber3d.verticalexpansion.worldgen;

import java.util.HashMap;
import java.util.Map;

public final class BiomePalette {

    private final Map<Integer, String> bandToBiome = new HashMap<>();
    private final int seaLevel;

    public BiomePalette(int seaLevel) {
        this.seaLevel = seaLevel;
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

    public String getBiomeForHeight(int height) {
        if (height >= seaLevel + 256) {
            return bandToBiome.get(4);
        } else if (height >= seaLevel + 128) {
            return bandToBiome.get(3);
        } else if (height >= seaLevel) {
            return bandToBiome.get(2);
        } else if (height >= seaLevel - 64) {
            return bandToBiome.get(1);
        } else {
            return bandToBiome.get(0);
        }
    }

    public void setBiomeForBand(int band, String biomeName) {
        bandToBiome.put(band, biomeName);
    }

    public int getSeaLevel() {
        return seaLevel;
    }
}
