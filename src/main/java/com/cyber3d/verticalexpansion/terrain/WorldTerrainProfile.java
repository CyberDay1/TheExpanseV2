package com.cyber3d.verticalexpansion.terrain;

/**
 * WorldTerrainProfile holds configurable parameters that control global terrain behavior.
 *
 * See docs/terrain_design.md for the semantics of each value.
 */
public interface WorldTerrainProfile {

    int minY();
    int maxY();
    int seaLevel();

    // Horizontal noise scales
    double continentsScale();
    double erosionScale();
    double ridgeScale();
    double valleyScale();
    double detailScale();

    // Amplitudes
    double baseHeightAmplitude();
    double mountainBoostAmplitude();
    double valleyDepth();
    double extremeMountainBoost();

    // Vertical bands
    int highlandsStartY();
    int extremeStartY();
    int skyBandStartY();

    // Flags
    boolean enableSkyTerrain();
    boolean enableUndergroundRivers();
    boolean enableMegaMountains();

    // Oceans / deep oceans
    double oceanThreshold();
    double deepOceanThreshold();
    int shallowOceanDepth();
    int deepOceanDepth();

    // Rivers
    double riverWidth();
    int riverMaxDepth();

    // Ravines
    double landRavineStrength();
    double oceanRavineStrength();
    double ravineFrequency();
    double ravineStretch();
}
