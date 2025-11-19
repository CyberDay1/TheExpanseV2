package com.cyber3d.verticalexpansion.vertical;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "verticalexpansion", value = Dist.DEDICATED_SERVER)
public final class VerticalTickEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    @SubscribeEvent
    public static void onServerTickPost(ServerTickEvent.Post event) {
        VerticalIntegration.onServerTick(event.getServer());
    }
}
