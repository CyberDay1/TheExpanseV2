package com.cyber3d.verticalexpansion.mixin;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.core.WorldHeightConfig;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.class)
public class ServerLevelMixin {
    
    @Inject(method = "<init>", at = @At("RETURN"))
    private void verticalexpansion$extendDimensionHeight(CallbackInfo ci) {
        ServerLevel level = (ServerLevel) (Object) this;
        ResourceKey<Level> dimensionKey = level.dimension();
        
        if (dimensionKey.location().getPath().equals("overworld")) {
            DimensionType dimType = level.dimensionType();
            WorldHeightConfig config = VerticalExpansionConfig.getWorldHeightConfig();
            int targetMinY = config.minY();
            int targetHeight = config.maxY() - config.minY();
            int targetLogicalHeight = Math.min(targetHeight, 1024);
            
            if (dimType.minY() != targetMinY || dimType.height() != targetHeight) {
                DimensionTypeAccessor accessor = (DimensionTypeAccessor) (Object) dimType;
                accessor.setMinY(targetMinY);
                accessor.setHeight(targetHeight);
                accessor.setLogicalHeight(targetLogicalHeight);
            }
        }
    }
}
