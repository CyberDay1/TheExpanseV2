package com.cyber3d.verticalexpansion.terrain;

import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public final class PerlinNoiseSampler implements NoiseSampler {

    private final PerlinSimplexNoise noise;
    private final double scale;

    public PerlinNoiseSampler(PerlinSimplexNoise noise, double scale) {
        this.noise = noise;
        this.scale = scale;
    }

    @Override
    public double sample(int x, int z) {
        return noise.getValue((double) x * scale, (double) z * scale, false);
    }

    public PerlinNoiseSampler withScale(double newScale) {
        return new PerlinNoiseSampler(noise, newScale);
    }
}
