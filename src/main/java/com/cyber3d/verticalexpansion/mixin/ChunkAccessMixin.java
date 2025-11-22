package com.cyber3d.verticalexpansion.mixin;

// Adapted from Tectonic (https://github.com/apolloterraforming/Tectonic) under MIT license
// Copyright (c) 2024 Apollo Terraforming contributors

import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {
    @Inject(
        method = "getOrCreateOffsetList",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void verticalexpansion$stopHeightDecreasingLogSpam(ShortList[] positions, int index, CallbackInfoReturnable<ShortList> cir) {
        if (index >= positions.length) {
            cir.setReturnValue(new ShortArrayList());
        }
    }

    // Guard against negative section indices passed into ChunkAccess#getSection(int).
    //
    // In extended-height worlds, some vanilla logic can compute an index of -1 for the
    // section array, which would normally cause an ArrayIndexOutOfBoundsException and
    // crash the game during chunk generation. We clamp negative indices up to 0 here,
    // which preserves vanilla behavior for all valid indices while safely handling
    // out-of-range values.
    @ModifyVariable(method = "getSection", at = @At("HEAD"), argsOnly = true)
    private int verticalexpansion$clampNegativeSectionIndex(int index) {
        return index < 0 ? 0 : index;
    }
}
