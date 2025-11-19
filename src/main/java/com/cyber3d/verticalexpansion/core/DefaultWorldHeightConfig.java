package com.cyber3d.verticalexpansion.core;

public final class DefaultWorldHeightConfig implements WorldHeightConfig {

    private final int minY;
    private final int maxY;
    private final int seaLevel;

    public DefaultWorldHeightConfig(int minY, int maxY, int seaLevel) {
        if (maxY <= minY) {
            throw new IllegalArgumentException("maxY must be greater than minY");
        }
        if ((maxY - minY) % 16 != 0) {
            throw new IllegalArgumentException("(maxY - minY) must be a multiple of 16");
        }
        if (seaLevel < minY || seaLevel > maxY) {
            throw new IllegalArgumentException("seaLevel must be within [minY, maxY]");
        }

        this.minY = minY;
        this.maxY = maxY;
        this.seaLevel = seaLevel;
    }

    public static DefaultWorldHeightConfig standard() {
        return new DefaultWorldHeightConfig(-256, 1024, 64);
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
    public int seaLevel() {
        return seaLevel;
    }
}
