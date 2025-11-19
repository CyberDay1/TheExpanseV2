package com.cyber3d.verticalexpansion.terrain;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;

public final class BiomeHeightModifiers {

    private BiomeHeightModifiers() {}

    public static double apply(
        Holder<Biome> biomeHolder,
        double baseHeight
    ) {
        Biome biome = biomeHolder.value();
        ResourceLocation key = biomeHolder.unwrapKey()
            .map(k -> k.location())
            .orElse(null);

        if (key == null) {
            return baseHeight;
        }

        String path = key.getPath();

        if (path.contains("mountain") || path.contains("windswept") || path.contains("stony_peaks")) {
            return baseHeight + 25.0;
        }

        if (path.contains("desert") || path.contains("badlands") || path.contains("mesa")) {
            return baseHeight * 0.9 + 5.0;
        }

        if (path.contains("swamp") || path.contains("mangrove")) {
            return baseHeight - 6.0;
        }

        if (path.contains("snowy") && path.contains("slopes")) {
            return baseHeight + 35.0;
        }

        return baseHeight;
    }
}
