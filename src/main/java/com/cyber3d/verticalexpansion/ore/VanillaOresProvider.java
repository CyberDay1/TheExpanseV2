package com.cyber3d.verticalexpansion.ore;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;

public final class VanillaOresProvider {

    private VanillaOresProvider() {
    }

    public static void registerVanillaOres(OreProfileRegistry registry) {
        // NOTE: These ranges are intentionally scaled for the extended world
        // height (-256 -> ~1024). They broadly preserve vanilla layering
        // (deep ores below 0, mid ores around surface, coal higher up) while
        // expanding bands to make use of the taller underground and highlands.

        registry.register("coal", new DefaultOreProfile(
                BlockTags.COAL_ORES,
                // Coal: common in mid/high bands, extended into highlands
                // Vanilla-ish: 0..192 in a 384-tall world
                // Tall world: broaden to roughly 0..512
                0, 512,
                17,
                0.3f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("iron", new DefaultOreProfile(
                BlockTags.IRON_ORES,
                // Iron: core underground ore spanning most of the stone depth
                // Vanilla-ish: -64..72
                // Tall world: extend deeper and slightly higher: -192..256
                -192, 256,
                9,
                0.25f,
                OreProfile.DistributionCurve.GAUSSIAN
        ));

        registry.register("gold", new DefaultOreProfile(
                BlockTags.GOLD_ORES,
                // Gold: prefers deeper regions but not as deep as diamond
                // Vanilla-ish: -64..32
                // Tall world: broaden but keep relatively low: -192..128
                -192, 128,
                9,
                0.15f,
                OreProfile.DistributionCurve.GAUSSIAN
        ));

        registry.register("lapis", new DefaultOreProfile(
                BlockTags.LAPIS_ORES,
                // Lapis: mid-depth band around and below 0
                // Vanilla-ish: -32..32
                // Tall world: extend deeper and a bit higher: -160..160
                -160, 160,
                7,
                0.08f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("diamond", new DefaultOreProfile(
                BlockTags.DIAMOND_ORES,
                // Diamond: focused in the deepest stone
                // Vanilla-ish: -64..16
                // Tall world: extend to most of the deep underground: -256..32
                -256, 32,
                8,
                0.08f,
                OreProfile.DistributionCurve.UNIFORM
        ));

        registry.register("redstone", new DefaultOreProfile(
                BlockTags.REDSTONE_ORES,
                // Redstone: deep-mid tech ore, slightly above diamonds
                // Vanilla-ish: -64..15
                // Tall world: extend deeper and modestly higher: -224..80
                -224, 80,
                8,
                0.2f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("copper", new DefaultOreProfile(
                BlockTags.COPPER_ORES,
                // Copper: broad mid-range ore from slightly below surface
                // Vanilla-ish: -16..112
                // Tall world: broaden into underground and highlands: -64..384
                -64, 384,
                11,
                0.25f,
                OreProfile.DistributionCurve.TRIANGLE
        ));

        registry.register("emerald", new DefaultOreProfile(
                BlockTags.EMERALD_ORES,
                // Emerald: rare, prefers mountains/highlands
                // Vanilla-ish: 4..32 in mountain biomes
                // Tall world: shift into highlands and peaks: 128..640
                128, 640,
                1,
                0.04f,
                OreProfile.DistributionCurve.UNIFORM
        ));
    }
}
