package com.cyber3d.verticalexpansion.ore;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public final class DefaultOreProfile implements OreProfile {

    private final TagKey<Block> targetTag;
    private final int minY;
    private final int maxY;
    private final int veinSize;
    private final float frequency;
    private final DistributionCurve curve;

    public DefaultOreProfile(
            TagKey<Block> targetTag,
            int minY,
            int maxY,
            int veinSize,
            float frequency,
            DistributionCurve curve
    ) {
        if (maxY <= minY) {
            throw new IllegalArgumentException("maxY must be > minY");
        }
        if (veinSize <= 0) {
            throw new IllegalArgumentException("veinSize must be > 0");
        }
        if (frequency <= 0 || frequency > 1.0f) {
            throw new IllegalArgumentException("frequency must be in (0, 1]");
        }

        this.targetTag = targetTag;
        this.minY = minY;
        this.maxY = maxY;
        this.veinSize = veinSize;
        this.frequency = frequency;
        this.curve = curve;
    }

    @Override
    public TagKey<Block> targetBlockTag() {
        return targetTag;
    }

    @Override
    public int minY() {
        return minY;
    }

    @Override
    public int maxY() {
        return maxY;
    }

    @Override
    public int veinSize() {
        return veinSize;
    }

    @Override
    public float frequency() {
        return frequency;
    }

    @Override
    public DistributionCurve distributionCurve() {
        return curve;
    }
}
