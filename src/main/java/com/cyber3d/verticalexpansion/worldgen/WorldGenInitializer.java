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
import java.util.HashMap;
import java.util.Map;

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
            if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
                LOGGER.debug("WorldGenInitializer already initialized");
            }
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
            new PerlinNoiseSampler(createNoise(1005), terrainProfile.detailScale()),
            new PerlinNoiseSampler(createNoise(1006), terrainProfile.ravineFrequency()),
            new PerlinNoiseSampler(createNoise(1007), 0.0015)
        );

        densityFunctionIntegration = new DensityFunctionIntegration(heightFunction, terrainProfile);
        densityFunctionIntegration.initialize();

        // Tie biome bands to the *actual* deep-ocean depth from the terrain profile
        biomePalette = new BiomePalette(heightConfig.seaLevel(), terrainProfile.deepOceanDepth());
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug(
                "BiomePalette initialized with sea level: {} and deep ocean depth: {}",
                heightConfig.seaLevel(),
                terrainProfile.deepOceanDepth()
            );
        }

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
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("WorldGenInitializer reset");
        }
    }

    public static void registerChunkGenerator_1_21_1() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering chunk generator for MC 1.21.1–1.21.4");
        }
        ChunkGeneratorRegistry.registerChunkGenerator();
    }

    public static void registerBiomeModifiers_1_21_1() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering biome modifiers for MC 1.21.1–1.21.4");
        }
        
        OreProfileRegistry ores = OreProfileRegistry.getInstance();
        FeatureRegistry features = FeatureRegistry.getInstance();
        
        Map<String, OreModifierDescriptor> orePlacementMap = new HashMap<>();
        
        LOGGER.info("[VerticalExpansion] Building ore modifier descriptors:");
        for (String oreId : ores.getAll().keySet()) {
            OreProfile profile = ores.getAll().get(oreId);
            
            OreModifierDescriptor descriptor = new OreModifierDescriptor(
                oreId,
                profile.targetBlockTag(),
                profile.minY(),
                profile.maxY(),
                profile.veinSize(),
                profile.frequency(),
                profile.distributionCurve()
            );
            
            orePlacementMap.put(oreId, descriptor);
            
            LOGGER.info("  - Ore '{}': y=[{}, {}], size={}, freq={}, curve={}",
                oreId,
                profile.minY(),
                profile.maxY(),
                profile.veinSize(),
                profile.frequency(),
                profile.distributionCurve()
            );
        }
        
        LOGGER.info("[VerticalExpansion] Built {} ore placement descriptors", orePlacementMap.size());
        
        LOGGER.info("[VerticalExpansion] Building feature placement descriptors:");
        if (features.isInitialized()) {
            FeaturePlacementDescriptor megaTreeDescriptor = new FeaturePlacementDescriptor(
                "mega_tree",
                "mountain",
                64,
                512
            );
            LOGGER.info("  - Feature 'mega_tree': height=[{}, {}], biomes={mountain}",
                megaTreeDescriptor.minHeight, megaTreeDescriptor.maxHeight);
            
            FeaturePlacementDescriptor coralReefDescriptor = new FeaturePlacementDescriptor(
                "coral_reef",
                "aquatic",
                -64,
                64
            );
            LOGGER.info("  - Feature 'coral_reef': height=[{}, {}], biomes={aquatic}",
                coralReefDescriptor.minHeight, coralReefDescriptor.maxHeight);
            
            FeaturePlacementDescriptor deepCaveDescriptor = new FeaturePlacementDescriptor(
                "deep_cave",
                "underground",
                -256,
                -48
            );
            LOGGER.info("  - Feature 'deep_cave': height=[{}, {}], biomes={underground}",
                deepCaveDescriptor.minHeight, deepCaveDescriptor.maxHeight);
        }
        
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Biome modifier registration infrastructure prepared.");
            LOGGER.debug("TODO: Create BiomeModifier entries from these descriptors and register");
            LOGGER.debug("      via NeoForge bootstrap/RegisterEvent.");
        }
    }

    public static void registerBiomeLoadingEvents_1_21_1() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering biome loading events for MC 1.21.1–1.21.4");
        }
    }

    public static void registerChunkGenerator_1_21_5() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering chunk generator for MC 1.21.5+");
        }
        registerChunkGenerator_1_21_1();
    }

    public static void registerBiomeModifiers_1_21_5() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering biome modifiers for MC 1.21.5+");
        }
        registerBiomeModifiers_1_21_1();
    }

    public static void registerBiomeLoadingEvents_1_21_5() {
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Registering biome loading events for MC 1.21.5+");
        }
        registerBiomeLoadingEvents_1_21_1();
    }

    private static PerlinSimplexNoise createNoise(int seed) {
        RandomSource random = RandomSource.create(TERRAIN_BASE_SEED + seed);
        return new PerlinSimplexNoise(random, new IntArrayList(new int[]{0}));
    }

    private static final class OreModifierDescriptor {
        final String oreId;
        final net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> targetBlockTag;
        final int minY;
        final int maxY;
        final int veinSize;
        final float frequency;
        final OreProfile.DistributionCurve distributionCurve;

        OreModifierDescriptor(String oreId, net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> targetBlockTag,
                              int minY, int maxY, int veinSize, float frequency, OreProfile.DistributionCurve distributionCurve) {
            this.oreId = oreId;
            this.targetBlockTag = targetBlockTag;
            this.minY = minY;
            this.maxY = maxY;
            this.veinSize = veinSize;
            this.frequency = frequency;
            this.distributionCurve = distributionCurve;
        }
    }

    private static final class FeaturePlacementDescriptor {
        final String featureName;
        final String biomeTag;
        final int minHeight;
        final int maxHeight;

        FeaturePlacementDescriptor(String featureName, String biomeTag, int minHeight, int maxHeight) {
            this.featureName = featureName;
            this.biomeTag = biomeTag;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
        }
    }
}
