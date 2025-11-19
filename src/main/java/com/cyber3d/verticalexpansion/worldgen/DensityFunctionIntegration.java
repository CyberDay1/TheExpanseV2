package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DensityFunctionIntegration {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static DensityFunctionIntegration instance;
    
    private final TerrainHeightDensityFunction densityFunction;
    private final ContinentalnessDensityFunction continentalnessDensityFunction;
    private final TerrainHeightFunction terrainHeightFunction;
    private final WorldTerrainProfile terrainProfile;

    public DensityFunctionIntegration(
            TerrainHeightFunction terrainHeightFunction,
            WorldTerrainProfile terrainProfile
    ) {
        this.terrainHeightFunction = terrainHeightFunction;
        this.terrainProfile = terrainProfile;
        this.densityFunction = new TerrainHeightDensityFunction(
            terrainHeightFunction,
            terrainProfile
        );
        this.continentalnessDensityFunction = new ContinentalnessDensityFunction(
            terrainHeightFunction,
            terrainProfile
        );
        DensityFunctionIntegration.instance = this;
    }

    public static DensityFunctionIntegration getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DensityFunctionIntegration not initialized");
        }
        return instance;
    }

    public TerrainHeightDensityFunction getDensityFunction() {
        return densityFunction;
    }

    public DensityFunction getTerrainDensityFunction() {
        return new TerrainHeightDensityFunction(terrainHeightFunction, terrainProfile);
    }

    public ContinentalnessDensityFunction getContinentalnessDensityFunction() {
        return continentalnessDensityFunction;
    }

    public TerrainHeightFunction getHeightFunction() {
        return terrainHeightFunction;
    }

    public WorldTerrainProfile getTerrainProfile() {
        return terrainProfile;
    }

    public void initialize() {
        LOGGER.info("Initializing DensityFunctionIntegration");
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Terrain profile: {} at scale factors {}, {}, {}, {}, {}",
                terrainProfile.getClass().getSimpleName(),
                terrainProfile.continentsScale(),
                terrainProfile.erosionScale(),
                terrainProfile.ridgeScale(),
                terrainProfile.valleyScale(),
                terrainProfile.detailScale()
            );
        }
        
        wrapAndRegisterDensityFunction();
    }

    private void wrapAndRegisterDensityFunction() {
        LOGGER.info("[VerticalExpansion] Wrapping TerrainHeightFunction into DensityFunction");

        logSample(0, 0);
        logSample(100, 100);
        logSample(-200, 300);

        LOGGER.info("DensityFunctionIntegration ready for terrain: {}x{}y{}z with profile {}",
            terrainProfile.continentsScale(),
            terrainProfile.erosionScale(),
            terrainProfile.ridgeScale(),
            terrainProfile.getClass().getSimpleName());
        
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Terrain height function integration:");
            LOGGER.debug("  - Height range: {} to {} blocks", 
                terrainHeightFunction.getClass().getSimpleName(),
                "per-coordinate computation");
            LOGGER.debug("  - Density function min/max: {}/{}", 
                densityFunction.minValue(), densityFunction.maxValue());
            
            LOGGER.debug("NOTE: Actual DensityFunction registration with Minecraft's registry");
            LOGGER.debug("      requires bootstrap or RegisterEvent. This integration provides");
            LOGGER.debug("      the computed density values that would be used in chunk generation.");
        }
    }

    private void logSample(int x, int z) {
        int height = terrainHeightFunction.computeHeight(x, z, terrainProfile);
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Sample terrain at ({}, {}): height = {}", x, z, height);
        }
    }

    public static final class TerrainHeightDensityFunction implements DensityFunction.SimpleFunction {
        private final TerrainHeightFunction heightFunction;
        private final WorldTerrainProfile profile;

        public TerrainHeightDensityFunction(TerrainHeightFunction heightFunction, WorldTerrainProfile profile) {
            this.heightFunction = heightFunction;
            this.profile = profile;
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            int x = context.blockX();
            int z = context.blockZ();
            return heightFunction.computeHeight(x, z, profile);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor visitor) {
            return visitor.apply(this);
        }

        @Override
        public double minValue() {
            return profile.minY();
        }

        @Override
        public double maxValue() {
            return profile.maxY();
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            throw new UnsupportedOperationException("TerrainHeightDensityFunction cannot be serialized");
        }
    }

    public static final class ContinentalnessDensityFunction implements DensityFunction.SimpleFunction {
        private final TerrainHeightFunction heightFunction;
        private final WorldTerrainProfile profile;

        public ContinentalnessDensityFunction(TerrainHeightFunction heightFunction, WorldTerrainProfile profile) {
            this.heightFunction = heightFunction;
            this.profile = profile;
        }

        @Override
        public double compute(DensityFunction.FunctionContext context) {
            int x = context.blockX();
            int z = context.blockZ();
            return heightFunction.computeContinentalness(x, z, profile);
        }

        @Override
        public DensityFunction mapAll(DensityFunction.Visitor visitor) {
            return visitor.apply(this);
        }

        @Override
        public double minValue() {
            return -1.0;
        }

        @Override
        public double maxValue() {
            return 1.0;
        }

        @Override
        public KeyDispatchDataCodec<? extends DensityFunction> codec() {
            throw new UnsupportedOperationException("ContinentalnessDensityFunction cannot be serialized");
        }
    }
}
