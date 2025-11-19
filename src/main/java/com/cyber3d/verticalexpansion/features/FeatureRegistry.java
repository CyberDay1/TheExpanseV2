package com.cyber3d.verticalexpansion.features;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.worldgen.BiomePalette;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FeatureRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static FeatureRegistry instance;

    private MegaTreeGenerator megaTreeGenerator;
    private CoralReefGenerator coralReefGenerator;
    private DeepCaveGenerator deepCaveGenerator;
    private WaterfallFeature waterfallFeature;
    private boolean initialized = false;

    private FeatureRegistry() {
    }

    public static synchronized FeatureRegistry getInstance() {
        if (instance == null) {
            instance = new FeatureRegistry();
        }
        return instance;
    }

    public synchronized void initialize(BiomePalette biomePalette) {
        if (initialized) {
            LOGGER.debug("FeatureRegistry already initialized");
            return;
        }

        LOGGER.info("Initializing VerticalExpansion feature registry");

        TreeFeatureConfig megaTreeConfig = new TreeFeatureConfig(
                64,
                512,
                0.08f,
                "mountain"
        );
        this.megaTreeGenerator = new MegaTreeGenerator(megaTreeConfig, biomePalette);

        this.coralReefGenerator = new CoralReefGenerator(biomePalette);

        this.deepCaveGenerator = new DeepCaveGenerator(biomePalette);

        this.waterfallFeature = new WaterfallFeature();

        initialized = true;
        LOGGER.info("VerticalExpansion feature registry initialized with 4 feature generators");
    }

    public MegaTreeGenerator getMegaTreeGenerator() {
        if (!initialized) {
            throw new IllegalStateException("FeatureRegistry not initialized");
        }
        return megaTreeGenerator;
    }

    public CoralReefGenerator getCoralReefGenerator() {
        if (!initialized) {
            throw new IllegalStateException("FeatureRegistry not initialized");
        }
        return coralReefGenerator;
    }

    public DeepCaveGenerator getDeepCaveGenerator() {
        if (!initialized) {
            throw new IllegalStateException("FeatureRegistry not initialized");
        }
        return deepCaveGenerator;
    }

    public WaterfallFeature getWaterfallFeature() {
        if (!initialized) {
            throw new IllegalStateException("FeatureRegistry not initialized");
        }
        return waterfallFeature;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void reset() {
        megaTreeGenerator = null;
        coralReefGenerator = null;
        deepCaveGenerator = null;
        waterfallFeature = null;
        initialized = false;
        LOGGER.debug("FeatureRegistry reset");
    }

    public void logStatistics() {
        if (initialized) {
            LOGGER.info("FeatureRegistry Statistics: trees={}, reefs={}, caves={}",
                    megaTreeGenerator.getTreesGenerated(),
                    coralReefGenerator.getReefsGenerated(),
                    deepCaveGenerator.getCavesGenerated());
        }
    }
}
