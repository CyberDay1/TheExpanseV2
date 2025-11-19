package com.cyber3d.verticalexpansion.terrain;

import net.minecraft.world.level.biome.Biome;

public interface BiomeHeightModifier {

    double modifyHeight(Biome biome, double baseY);
}
