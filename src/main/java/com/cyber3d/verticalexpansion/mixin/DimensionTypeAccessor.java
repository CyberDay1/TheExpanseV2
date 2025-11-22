package com.cyber3d.verticalexpansion.mixin;

// Adapted from Tectonic (https://github.com/apolloterraforming/Tectonic) under MIT license
// Copyright (c) 2024 Apollo Terraforming contributors

import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(DimensionType.class)
public interface DimensionTypeAccessor {
    @Accessor("minY") @Mutable
    void setMinY(int minY);

    @Accessor("height") @Mutable
    void setHeight(int height);

    @Accessor("logicalHeight") @Mutable
    void setLogicalHeight(int logicalHeight);
}
