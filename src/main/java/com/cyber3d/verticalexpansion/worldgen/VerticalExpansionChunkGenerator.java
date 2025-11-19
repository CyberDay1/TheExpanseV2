package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.NoiseBasedTerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.PerlinNoiseSampler;
import com.cyber3d.verticalexpansion.terrain.TerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;

public final class VerticalExpansionChunkGenerator extends ChunkGenerator {

    private final WorldHeightConfig heightConfig;
    private final WorldTerrainProfile terrainProfile;
    private final TerrainHeightFunction heightFunction;
    private final RandomState randomState;

    public VerticalExpansionChunkGenerator(
            WorldHeightConfig heightConfig,
            RandomState randomState
    ) {
        super();
        this.heightConfig = heightConfig;
        this.terrainProfile = DefaultWorldTerrainProfile.standard(heightConfig);
        this.randomState = randomState;
        
        PerlinSimplexNoise continentsNoise = new PerlinSimplexNoise(randomState.getRandom(1001), new int[]{0});
        PerlinSimplexNoise erosionNoise = new PerlinSimplexNoise(randomState.getRandom(1002), new int[]{0});
        PerlinSimplexNoise ridgeNoise = new PerlinSimplexNoise(randomState.getRandom(1003), new int[]{0});
        PerlinSimplexNoise valleyNoise = new PerlinSimplexNoise(randomState.getRandom(1004), new int[]{0});
        PerlinSimplexNoise detailNoise = new PerlinSimplexNoise(randomState.getRandom(1005), new int[]{0});
        
        this.heightFunction = new NoiseBasedTerrainHeightFunction(
                new PerlinNoiseSampler(continentsNoise, terrainProfile.continentsScale()),
                new PerlinNoiseSampler(erosionNoise, terrainProfile.erosionScale()),
                new PerlinNoiseSampler(ridgeNoise, terrainProfile.ridgeScale()),
                new PerlinNoiseSampler(valleyNoise, terrainProfile.valleyScale()),
                new PerlinNoiseSampler(detailNoise, terrainProfile.detailScale())
        );
    }

    public WorldHeightConfig getHeightConfig() {
        return heightConfig;
    }

    public WorldTerrainProfile getTerrainProfile() {
        return terrainProfile;
    }

    public TerrainHeightFunction getHeightFunction() {
        return heightFunction;
    }

    @Override
    public int getMinY() {
        return heightConfig.minY();
    }

    @Override
    public int getMaxY() {
        return heightConfig.maxY();
    }

    @Override
    public int getHeight(int x, int z, Heightmap.Types type) {
        return heightFunction.computeHeight(x, z, terrainProfile);
    }

    @Override
    public void addDebugStructures(RandomState randomState) {
    }

    @Override
    public void createStructures(
            net.minecraft.world.level.StructureManager structureManager,
            net.minecraft.world.level.chunk.ChunkAccess chunk,
            net.minecraft.world.level.levelgen.structure.StructureManager structureManager1,
            long seed
    ) {
    }

    @Override
    public void createBiomes(
            net.minecraft.world.level.biome.BiomeManager biomeManager,
            net.minecraft.world.level.chunk.ChunkAccess chunk,
            RandomState randomState
    ) {
    }

    @Override
    public void fillFromNoise(
            net.minecraft.world.level.WorldGenLevel level,
            net.minecraft.world.level.StructureManager structureManager,
            net.minecraft.world.level.chunk.ChunkAccess chunk
    ) {
    }

    @Override
    public void spawnOriginalMobs(net.minecraft.world.level.WorldGenLevel level) {
    }
}
