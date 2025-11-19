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
    private final NoiseSampler ravineNoise;
    private final NoiseSampler erosionNoise;

    public NoiseBasedTerrainHeightFunction(
            NoiseSampler continents,
            NoiseSampler erosion,
            NoiseSampler ridge,
            NoiseSampler valley,
            NoiseSampler detail,
            NoiseSampler ravineNoise,
            NoiseSampler erosionNoise
    ) {
        this.continents = continents;
        this.erosion = erosion;
        this.ridge = ridge;
        this.valley = valley;
        this.detail = detail;
        this.ravineNoise = ravineNoise;
        this.erosionNoise = erosionNoise;
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

        double baseLandHeight = computeBaseLandHeight(x, z, profile, c, e, r, v, d);

        double height;
        if (c < profile.deepOceanThreshold()) {
            double t = clamp((profile.deepOceanThreshold() - c) / 0.5, 0.0, 1.0);
            double depth = lerp(profile.shallowOceanDepth(), profile.deepOceanDepth(), t);
            height = profile.seaLevel() - depth;
        } else if (c < profile.oceanThreshold()) {
            height = profile.seaLevel() - profile.shallowOceanDepth();
        } else {
            height = baseLandHeight;
        }

        height = applyErosionShaping(x, z, height, profile);
        height = applyRivers(x, z, height, profile);
        height = applyRavines(x, z, height, profile, c);

        double clamped = clamp(height, profile.minY(), profile.maxY());
        return (int) Math.round(clamped);
    }

    private double computeBaseLandHeight(
        int x, int z,
        WorldTerrainProfile profile,
        double continents,
        double erosion,
        double ridge,
        double valley,
        double detail
    ) {
        double c01 = (continents + 1.0) / 2.0;
        double base = profile.seaLevel() + profile.baseHeightAmplitude() * (c01 - 0.5);
        
        double erosionFactor = 1.0 - ((erosion + 1.0) / 2.0);
        double mountainBoost = ridge * erosionFactor * profile.mountainBoostAmplitude();
        double valleyCut = -Math.abs(valley) * profile.valleyDepth();
        double detailEffect = detail * (profile.baseHeightAmplitude() * 0.3);
        
        double extremeBoost = 0.0;
        if (profile.enableMegaMountains()) {
            double extremeMask = clamp(erosionFactor * ((c01 - 0.5) * 2.0), 0.0, 1.0);
            extremeBoost = extremeMask * profile.extremeMountainBoost();
        }
        
        return base + mountainBoost + valleyCut + detailEffect + extremeBoost;
    }

    private double applyRivers(int x, int z, double height, WorldTerrainProfile profile) {
        double riverNoiseValue = valley.sample((int)(x * profile.valleyScale()), (int)(z * profile.valleyScale()));
        double d = Math.abs(riverNoiseValue);

        double width = profile.riverWidth();
        if (d > width) {
            return height;
        }

        double edgeT = d / width;
        double centerT = 1.0 - edgeT;
        centerT = centerT * centerT;

        double maxDepth = profile.riverMaxDepth();
        double carveDepth = maxDepth * centerT;

        return height - carveDepth;
    }

    private double applyRavines(
        int x,
        int z,
        double height,
        WorldTerrainProfile profile,
        double continents
    ) {
        double nx = x * profile.ravineFrequency();
        double nz = z * profile.ravineFrequency() * profile.ravineStretch();

        double ravineValue = ravineNoise.sample((int) nx, (int) nz);
        double d = Math.abs(ravineValue);

        double band = 0.15;
        if (d > band) {
            return height;
        }

        double t = d / band;
        double centerT = 1.0 - t;
        centerT = centerT * centerT * centerT;

        boolean isOceanColumn = continents < profile.oceanThreshold();

        double strength = isOceanColumn
            ? profile.oceanRavineStrength()
            : profile.landRavineStrength();

        if (strength <= 0.0) {
            return height;
        }

        double verticalRange = profile.maxY() - profile.minY();
        double maxCarveDepth = verticalRange * 0.5 * strength;

        double carveDepth = maxCarveDepth * centerT;

        double carvedHeight = height - carveDepth;

        return Math.min(height, carvedHeight);
    }

    private double applyErosionShaping(
        int x,
        int z,
        double height,
        WorldTerrainProfile profile
    ) {
        double strength = profile.erosionStrength();
        if (strength <= 0.0) {
            return height;
        }

        double erosionRaw = erosionNoise.sample((int)(x * 0.0015), (int)(z * 0.0015));
        double erosion01 = (erosionRaw * 0.5) + 0.5;

        double threshold = profile.erosionThreshold();

        double dist = Math.abs(erosion01 - threshold);
        double centerFactor = 1.0 - Math.min(1.0, dist / threshold);
        centerFactor = centerFactor * centerFactor;

        double erosionFactor = centerFactor * strength;

        double baseReference = profile.baseHeightAmplitude();
        double delta = height - baseReference;

        double flattenedDelta;
        if (delta >= 0) {
            flattenedDelta = delta * profile.erosionRidgeMultiplier();
        } else {
            flattenedDelta = delta * profile.erosionFlattenMultiplier();
        }

        double newDelta = lerp(delta, flattenedDelta, erosionFactor);

        return baseReference + newDelta;
    }
    
    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
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

    @Override
    public double computeContinentalness(int x, int z, WorldTerrainProfile profile) {
        double cx = x * profile.continentsScale();
        double cz = z * profile.continentsScale();
        return continents.sample((int) cx, (int) cz);
    }
}
