package com.cyber3d.verticalexpansion.terrain;

/**
 * NoiseBasedTerrainHeightFunction combines multiple noise fields into a single height value.
 *
 * Design notes:
 * - We emulate a Tectonic-style terrain stack:
 *   - continents: decides land vs ocean and base elevation.
 *   - erosion: decides smooth vs jagged terrain.
 *   - ridge: boosts mountains into long ranges.
 *   - valley: carves river basins and lower areas.
 *   - detail: adds local variation.
 * - See docs/terrain_design.md for the formulas.
 */
public final class NoiseBasedTerrainHeightFunction implements TerrainHeightFunction {

    private final NoiseSampler continents;
    private final NoiseSampler erosion;
    private final NoiseSampler ridge;
    private final NoiseSampler valley;
    private final NoiseSampler detail;

    public NoiseBasedTerrainHeightFunction(
            NoiseSampler continents,
            NoiseSampler erosion,
            NoiseSampler ridge,
            NoiseSampler valley,
            NoiseSampler detail
    ) {
        this.continents = continents;
        this.erosion = erosion;
        this.ridge = ridge;
        this.valley = valley;
        this.detail = detail;
    }

    @Override
    public int computeHeight(int x, int z, WorldTerrainProfile profile) {
        double cx = x * profile.continentsScale();
        double cz = z * profile.continentsScale();
        
        double ex = x * profile.erosionScale();
        double ez = z * profile.erosionScale();
        
        double rx = x * profile.ridgeScale();
        double rz = z * profile.ridgeScale();
        
        double vx = x * profile.valleyScale();
        double vz = z * profile.valleyScale();
        
        double dx = x * profile.detailScale();
        double dz = z * profile.detailScale();
        
        double c = continents.sample((int) cx, (int) cz);
        double e = erosion.sample((int) ex, (int) ez);
        double r = ridge.sample((int) rx, (int) rz);
        double v = valley.sample((int) vx, (int) vz);
        double d = detail.sample((int) dx, (int) dz);
        
        double c01 = (c + 1.0) / 2.0;
        double base = profile.seaLevel() + profile.baseHeightAmplitude() * (c01 - 0.5);
        
        double erosionFactor = 1.0 - ((e + 1.0) / 2.0);
        double mountainBoost = r * erosionFactor * profile.mountainBoostAmplitude();
        double valleyCut = v * profile.valleyDepth();
        double localDetail = d * (profile.baseHeightAmplitude() * 0.3);
        
        double extremeBoost = 0.0;
        if (profile.enableMegaMountains()) {
            double extremeMask = clamp(erosionFactor * ((c01 - 0.5) * 2.0), 0.0, 1.0);
            extremeBoost = extremeMask * profile.extremeMountainBoost();
        }
        
        double rawHeight = base + mountainBoost + localDetail + extremeBoost - valleyCut;
        int height = (int) Math.round(rawHeight);
        
        return clamp(height, profile.minY(), profile.maxY());
    }
    
    private static double clamp(double value, double min, double max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
    
    private static int clamp(int value, int min, int max) {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }
}
