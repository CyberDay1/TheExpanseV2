package com.cyber3d.verticalexpansion.features;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class WaterfallFeature {

    public WaterfallFeature() {
    }

    public boolean place(WorldGenLevel level, RandomSource random, BlockPos origin) {

        BlockPos.MutableBlockPos pos = origin.mutable();

        while (pos.getY() < level.getMaxBuildHeight() - 1 && level.isEmptyBlock(pos.above())) {
            pos.move(0, 1, 0);
        }

        BlockPos below = pos.below();
        if (below.getY() <= level.getMinBuildHeight() + 5) {
            return false;
        }

        int drops = 0;
        for (BlockPos dir : new BlockPos[] {
                new BlockPos(1, 0, 0),
                new BlockPos(-1, 0, 0),
                new BlockPos(0, 0, 1),
                new BlockPos(0, 0, -1)
        }) {
            BlockPos side = pos.offset(dir);
            if (level.isEmptyBlock(side) && isAirBelow(level, side, 6)) {
                drops++;
            }
        }

        if (drops == 0) {
            return false;
        }

        BlockState water = Blocks.WATER.defaultBlockState();
        level.setBlock(pos, water, 2);

        BlockPos.MutableBlockPos fallPos = pos.mutable();
        for (int i = 0; i < 24 && fallPos.getY() > level.getMinBuildHeight() + 4; i++) {
            fallPos.move(0, -1, 0);
            if (!level.isEmptyBlock(fallPos)) {
                break;
            }
            level.setBlock(fallPos, water, 2);
        }

        return true;
    }

    private boolean isAirBelow(WorldGenLevel level, BlockPos start, int depth) {
        BlockPos.MutableBlockPos pos = start.mutable();
        for (int i = 0; i < depth; i++) {
            pos.move(0, -1, 0);
            if (!level.isEmptyBlock(pos)) {
                return false;
            }
        }
        return true;
    }
}
