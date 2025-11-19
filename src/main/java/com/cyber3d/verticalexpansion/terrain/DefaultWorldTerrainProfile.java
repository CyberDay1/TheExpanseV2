package com.cyber3d.verticalexpansion.terrain;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;

public final class DefaultWorldTerrainProfile implements WorldTerrainProfile {

    private final WorldHeightConfig heightConfig;
    
    private final double continentsScale;
    private final double erosionScale;
    private final double ridgeScale;
    private final double valleyScale;
    private final double detailScale;
    
    private final double baseHeightAmplitude;
    private final double mountainBoostAmplitude;
    private final double valleyDepth;
    private final double extremeMountainBoost;
    
    private final int highlandsStartY;
    private final int extremeStartY;
    private final int skyBandStartY;
    
    private final boolean enableSkyTerrain;
    private final boolean enableUndergroundRivers;
    private final boolean enableMegaMountains;

    public DefaultWorldTerrainProfile(
            WorldHeightConfig heightConfig,
            double continentsScale,
            double erosionScale,
            double ridgeScale,
            double valleyScale,
            double detailScale,
            double baseHeightAmplitude,
            double mountainBoostAmplitude,
            double valleyDepth,
            double extremeMountainBoost,
            int highlandsStartY,
            int extremeStartY,
            int skyBandStartY,
            boolean enableSkyTerrain,
            boolean enableUndergroundRivers,
            boolean enableMegaMountains
    ) {
        this.heightConfig = heightConfig;
        this.continentsScale = continentsScale;
        this.erosionScale = erosionScale;
        this.ridgeScale = ridgeScale;
        this.valleyScale = valleyScale;
        this.detailScale = detailScale;
        this.baseHeightAmplitude = baseHeightAmplitude;
        this.mountainBoostAmplitude = mountainBoostAmplitude;
        this.valleyDepth = valleyDepth;
        this.extremeMountainBoost = extremeMountainBoost;
        this.highlandsStartY = highlandsStartY;
        this.extremeStartY = extremeStartY;
        this.skyBandStartY = skyBandStartY;
        this.enableSkyTerrain = enableSkyTerrain;
        this.enableUndergroundRivers = enableUndergroundRivers;
        this.enableMegaMountains = enableMegaMountains;
    }

    public static DefaultWorldTerrainProfile standard(WorldHeightConfig heightConfig) {
        return new DefaultWorldTerrainProfile(
                heightConfig,
                0.002,    
                0.003,    
                0.0025,   
                0.004,    
                0.01,     
                100.0,    
                200.0,    
                50.0,     
                300.0,    
                220,      
                420,      
                900,      
                true,     
                true,     
                true      
        );
    }

    @Override
    public int minY() {
        return heightConfig.minY();
    }

    @Override
    public int maxY() {
        return heightConfig.maxY();
    }

    @Override
    public int seaLevel() {
        return heightConfig.seaLevel();
    }

    @Override
    public double continentsScale() {
        return continentsScale;
    }

    @Override
    public double erosionScale() {
        return erosionScale;
    }

    @Override
    public double ridgeScale() {
        return ridgeScale;
    }

    @Override
    public double valleyScale() {
        return valleyScale;
    }

    @Override
    public double detailScale() {
        return detailScale;
    }

    @Override
    public double baseHeightAmplitude() {
        return baseHeightAmplitude;
    }

    @Override
    public double mountainBoostAmplitude() {
        return mountainBoostAmplitude;
    }

    @Override
    public double valleyDepth() {
        return valleyDepth;
    }

    @Override
    public double extremeMountainBoost() {
        return extremeMountainBoost;
    }

    @Override
    public int highlandsStartY() {
        return highlandsStartY;
    }

    @Override
    public int extremeStartY() {
        return extremeStartY;
    }

    @Override
    public int skyBandStartY() {
        return skyBandStartY;
    }

    @Override
    public boolean enableSkyTerrain() {
        return enableSkyTerrain;
    }

    @Override
    public boolean enableUndergroundRivers() {
        return enableUndergroundRivers;
    }

    @Override
    public boolean enableMegaMountains() {
        return enableMegaMountains;
    }
}
