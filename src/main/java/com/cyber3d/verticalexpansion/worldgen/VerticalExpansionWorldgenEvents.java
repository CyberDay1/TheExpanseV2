package com.cyber3d.verticalexpansion.worldgen;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "verticalexpansion", bus = EventBusSubscriber.Bus.FORGE)
public final class VerticalExpansionWorldgenEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        LOGGER.debug("Level loaded with height config: {} to {}", 
            VerticalExpansionConfig.getWorldHeightConfig().minY(),
            VerticalExpansionConfig.getWorldHeightConfig().maxY()
        );
    }
}

@EventBusSubscriber(modid = "verticalexpansion", bus = EventBusSubscriber.Bus.MOD)
final class VerticalExpansionModEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

}
