package com.cyber3d.verticalexpansion.mixin;

// Adapted from Tectonic (https://github.com/apolloterraforming/Tectonic) under MIT license
// Copyright (c) 2024 Apollo Terraforming contributors

import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Heightmap.class)
public class HeightmapMixin {
    @Shadow @Mutable @Final
    private static Logger LOGGER;

    static {
        LOGGER = NOPLogger.NOP_LOGGER;
    }
}
