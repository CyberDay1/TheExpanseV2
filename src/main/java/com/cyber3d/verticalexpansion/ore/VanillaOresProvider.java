package com.cyber3d.verticalexpansion.ore;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public final class VanillaOresProvider {

    private VanillaOresProvider() {
    }

    public static void registerVanillaOres(OreProfileRegistry registry) {
        registry.register("coal", new DefaultOreProfile(
                BlockTags.COAL_ORES,
                16, 136,
                17,
                0.3f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("iron", new DefaultOreProfile(
                BlockTags.IRON_ORES,
                -64, 72,
                9,
                0.25f,
                OreProfile.DistributionCurve.GAUSSIAN
        ));

        registry.register("gold", new DefaultOreProfile(
                BlockTags.GOLD_ORES,
                -64, 32,
                9,
                0.15f,
                OreProfile.DistributionCurve.GAUSSIAN
        ));

        registry.register("lapis", new DefaultOreProfile(
                BlockTags.LAPIS_ORES,
                -32, 32,
                7,
                0.08f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("diamond", new DefaultOreProfile(
                BlockTags.DIAMOND_ORES,
                -64, 16,
                8,
                0.08f,
                OreProfile.DistributionCurve.UNIFORM
        ));

        registry.register("redstone", new DefaultOreProfile(
                BlockTags.REDSTONE_ORES,
                -64, 15,
                8,
                0.2f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("copper", new DefaultOreProfile(
                BlockTags.COPPER_ORES,
                -16, 112,
                11,
                0.25f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("emerald", new DefaultOreProfile(
                BlockTags.EMERALD_ORES,
                4, 32,
                1,
                0.04f,
                OreProfile.DistributionCurve.UNIFORM
        ));
    }
}
