package com.cyber3d.verticalexpansion.mixin;

// Adapted from Tectonic (https://github.com/apolloterraforming/Tectonic) under MIT license
// Copyright (c) 2024 Apollo Terraforming contributors

import com.cyber3d.verticalexpansion.VerticalExpansion;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.chunk.storage.IOWorker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IOWorker.class)
public class IOWorkerMixin {
    @Inject(
        method = "isOldChunk",
        at = @At("HEAD"),
        cancellable = true
    )
    private void verticalexpansion$needsBlending(CompoundTag nbt, CallbackInfoReturnable<Boolean> cir) {
        int version = nbt.getInt(VerticalExpansion.BLENDING_KEY);
        if (version != VerticalExpansion.BLENDING_VERSION) {
            cir.setReturnValue(true);
        }
    }
}
