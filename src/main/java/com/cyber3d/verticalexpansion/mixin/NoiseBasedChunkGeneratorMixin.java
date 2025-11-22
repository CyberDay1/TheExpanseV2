package com.cyber3d.verticalexpansion.mixin;

// Adapted from Tectonic (https://github.com/apolloterraforming/Tectonic) under MIT license
// Copyright (c) 2024 Apollo Terraforming contributors

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import com.google.common.base.Suppliers;
import com.cyber3d.verticalexpansion.worldgen.bootstrap.VerticalNoiseSettingsBootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(NoiseBasedChunkGenerator.class)
public class NoiseBasedChunkGeneratorMixin {

    @Shadow @Final @Mutable
    private Holder<NoiseGeneratorSettings> settings;
    
    @Shadow @Final @Mutable
    private Supplier<Aquifer.FluidPicker> globalFluidPicker;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void verticalexpansion$extendVerticalRange(BiomeSource source, Holder<NoiseGeneratorSettings> settings, CallbackInfo ci) {
        WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
        // Place the lava floor much higher than the absolute bottom so it sits
        // around Y = -200 in the default -256 -> 1024 world. We keep this
        // relative to minY so it still behaves sensibly if the vertical range
        // is changed in the config.
        //
        //   default minY = -256  ->  -256 + 56 = -200
        //
        int lavaLevel = config.minY() + 56;
        
        this.globalFluidPicker = Suppliers.memoize(() -> {
            Aquifer.FluidStatus lavaStatus = new Aquifer.FluidStatus(lavaLevel, Blocks.LAVA.defaultBlockState());
            int seaLevel = this.settings.value().seaLevel();
            Aquifer.FluidStatus seaStatus = new Aquifer.FluidStatus(seaLevel, this.settings.value().defaultFluid());
            Aquifer.FluidStatus disabledStatus = new Aquifer.FluidStatus(DimensionType.MIN_Y * 2, Blocks.AIR.defaultBlockState());
            return (j, k, l) -> {
                if (SharedConstants.DEBUG_DISABLE_FLUID_GENERATION) {
                    return disabledStatus;
                }
                if (k < Math.min(lavaLevel, seaLevel)) {
                    return lavaStatus;
                }
                return seaStatus;
            };
        });
    }
}
