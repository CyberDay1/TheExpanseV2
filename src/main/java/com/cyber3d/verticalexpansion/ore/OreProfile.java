package com.cyber3d.verticalexpansion.ore;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public interface OreProfile {

    TagKey<Block> targetBlockTag();

    int minY();

    int maxY();

    int veinSize();

    float frequency();

    DistributionCurve distributionCurve();

    enum DistributionCurve {
        UNIFORM,
        TRIANGLE,
        GAUSSIAN
    }
}
