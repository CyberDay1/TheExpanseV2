    package com.cyber3d.verticalexpansion.terrain;

/**
 * TerrainHeightFunction computes the target surface height for a given column.
 *
 * Implementations should follow docs/terrain_design.md.
 */
public interface TerrainHeightFunction {

    /**
     * Compute the target surface height at (x, z).
     *
     * @param x world-space block x
     * @param z world-space block z
     * @param profile global world terrain profile (min/max Y, noise scales, etc.)
     * @return height clamped to [profile.minY(), profile.maxY()]
     */
    int computeHeight(int x, int z, WorldTerrainProfile profile);
}
