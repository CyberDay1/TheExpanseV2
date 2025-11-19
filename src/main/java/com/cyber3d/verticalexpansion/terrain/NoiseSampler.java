    package com.cyber3d.verticalexpansion.terrain;

/**
 * NoiseSampler samples a 2D noise field used in terrain generation.
 */
public interface NoiseSampler {

    /**
     * Sample the noise field at the given world coordinates.
     *
     * Implementations should typically return values in [-1, 1], but other ranges are
     * allowed as long as they are consistent with the terrain formulas.
     */
    double sample(int x, int z);
}
