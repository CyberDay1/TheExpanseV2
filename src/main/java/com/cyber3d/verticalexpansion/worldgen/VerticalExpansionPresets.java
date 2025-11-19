package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.DefaultWorldHeightConfig;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;

public final class VerticalExpansionPresets {

    private static final String MODID = "verticalexpansion";

    private VerticalExpansionPresets() {
    }

    public static class HeightConfigs {
        public static final int DEFAULT_MIN_Y = -256;
        public static final int DEFAULT_MAX_Y = 1024;
        public static final int DEFAULT_SEA_LEVEL = 64;

        public static final DefaultWorldHeightConfig STANDARD = 
            new DefaultWorldHeightConfig(DEFAULT_MIN_Y, DEFAULT_MAX_Y, DEFAULT_SEA_LEVEL);
    }

    public static class TerrainProfiles {
        public static final DefaultWorldTerrainProfile STANDARD = 
            DefaultWorldTerrainProfile.standard(HeightConfigs.STANDARD);

        public static final DefaultWorldTerrainProfile MODERATE = 
            new DefaultWorldTerrainProfile(
                HeightConfigs.STANDARD,
                0.003, 0.004, 0.003, 0.005, 0.012,
                80.0, 150.0, 40.0, 200.0,
                200, 400, 900,
                true, true, true,
                -0.1, -0.5, 20, 60,
                0.04, 16,
                0.75, 0.55, 0.0025, 3.0,
                0.6, 0.4, 0.65, 1.2
            );

        public static final DefaultWorldTerrainProfile EXTREME = 
            new DefaultWorldTerrainProfile(
                HeightConfigs.STANDARD,
                0.0015, 0.002, 0.002, 0.003, 0.008,
                120.0, 250.0, 60.0, 400.0,
                240, 440, 900,
                true, true, true,
                -0.08, -0.48, 25, 80,
                0.05, 20,
                0.85, 0.65, 0.0020, 3.5,
                0.8, 0.35, 0.55, 1.4
            );
    }
}
