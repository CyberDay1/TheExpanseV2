package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.features.FeatureRegistry;
import com.cyber3d.verticalexpansion.ore.OreProfile;
import com.cyber3d.verticalexpansion.ore.OreProfileRegistry;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.NoiseBasedTerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.PerlinNoiseSampler;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class WorldGenInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static DensityFunctionIntegration densityFunctionIntegration;
    private static BiomePalette biomePalette;
    private static boolean initialized = false;

    private static final long TERRAIN_BASE_SEED = 12345L;

    private WorldGenInitializer() {
    }

    public static void initialize() {
        if (initialized) {
            LOGGER.debug("WorldGenInitializer already initialized");
            return;
        }

        LOGGER.info("Initializing VerticalExpansion world generation system");

        WorldHeightConfig heightConfig = VerticalExpansionConfig.getWorldHeightConfig();
        WorldTerrainProfile terrainProfile = DefaultWorldTerrainProfile.standard(heightConfig);

        NoiseBasedTerrainHeightFunction heightFunction = new NoiseBasedTerrainHeightFunction(
            new PerlinNoiseSampler(createNoise(1001), terrainProfile.continentsScale()),
            new PerlinNoiseSampler(createNoise(1002), terrainProfile.erosionScale()),
            new PerlinNoiseSampler(createNoise(1003), terrainProfile.ridgeScale()),
            new PerlinNoiseSampler(createNoise(1004), terrainProfile.valleyScale()),
            new PerlinNoiseSampler(createNoise(1005), terrainProfile.detailScale())
        );

        densityFunctionIntegration = new DensityFunctionIntegration(heightFunction, terrainProfile);
        densityFunctionIntegration.initialize();

        biomePalette = new BiomePalette(heightConfig.seaLevel());
        LOGGER.debug("BiomePalette initialized with sea level: {}", heightConfig.seaLevel());

        ChunkGeneratorRegistry.registerChunkGenerator();

        initialized = true;
        LOGGER.info("VerticalExpansion world generation system initialized successfully");
    }

    public static DensityFunctionIntegration getDensityFunctionIntegration() {
        if (!initialized) {
            initialize();
        }
        return densityFunctionIntegration;
    }

    public static BiomePalette getBiomePalette() {
        if (!initialized) {
            initialize();
        }
        return biomePalette;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void reset() {
        densityFunctionIntegration = null;
        biomePalette = null;
        initialized = false;
        LOGGER.debug("WorldGenInitializer reset");
    }

    public static void registerChunkGenerator_1_21_1() {
        LOGGER.debug("Registering chunk generator for MC 1.21.1–1.21.4");
        ChunkGeneratorRegistry.registerChunkGenerator();
    }

    public static void registerBiomeModifiers_1_21_1() {
        LOGGER.debug("Registering biome modifiers for MC 1.21.1–1.21.4");
        
        FeatureRegistry features = FeatureRegistry.getInstance();
        OreProfileRegistry ores = OreProfileRegistry.getInstance();
        
        LOGGER.debug("Feature generators available: {} mega trees, {} coral reefs, {} deep caves",
            features.isInitialized() ? "yes" : "no",
            features.isInitialized() ? "yes" : "no",
            features.isInitialized() ? "yes" : "no"
        );
        
        int oreCount = ores.getAll().size();
        LOGGER.debug("Ore profiles registered: {} total", oreCount);
        
        for (String oreId : ores.getAll().keySet()) {
            OreProfile profile = ores.getAll().get(oreId);
            LOGGER.debug("  - {}: minY={}, maxY={}", oreId, 
                profile.minY(), profile.maxY());
        }
        
        LOGGER.debug("Biome modifier registration infrastructure initialized. " +
            "Actual ore/feature placement will be applied via biome modifiers and JSON data files.");
    }

    public static void registerBiomeLoadingEvents_1_21_1() {
        LOGGER.debug("Registering biome loading events for MC 1.21.1–1.21.4");
    }

    public static void registerChunkGenerator_1_21_5() {
        LOGGER.debug("Registering chunk generator for MC 1.21.5+");
        registerChunkGenerator_1_21_1();
    }

    public static void registerBiomeModifiers_1_21_5() {
        LOGGER.debug("Registering biome modifiers for MC 1.21.5+");
        registerBiomeModifiers_1_21_1();
    }

    public static void registerBiomeLoadingEvents_1_21_5() {
        LOGGER.debug("Registering biome loading events for MC 1.21.5+");
        registerBiomeLoadingEvents_1_21_1();
    }

    private static PerlinSimplexNoise createNoise(int seed) {
        RandomSource random = RandomSource.create(TERRAIN_BASE_SEED + seed);
        return new PerlinSimplexNoise(random, new IntArrayList(new int[]{0}));
    }
}
