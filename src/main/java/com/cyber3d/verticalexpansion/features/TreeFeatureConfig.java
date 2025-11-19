package com.cyber3d.verticalexpansion.features;

public final class TreeFeatureConfig {

    private final int minHeight;
    private final int maxHeight;
    private final float frequency;
    private final String biomeFilter;

    public TreeFeatureConfig(int minHeight, int maxHeight, float frequency, String biomeFilter) {
        if (maxHeight <= minHeight) {
            throw new IllegalArgumentException("maxHeight must be > minHeight");
        }
        if (frequency <= 0 || frequency > 1.0f) {
            throw new IllegalArgumentException("frequency must be in (0, 1]");
        }

        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.frequency = frequency;
        this.biomeFilter = biomeFilter;
    }

    public int minHeight() {
        return minHeight;
    }

    public int maxHeight() {
        return maxHeight;
    }

    public float frequency() {
        return frequency;
    }

    public String biomeFilter() {
        return biomeFilter;
    }
}
