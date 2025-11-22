package com.cyber3d.verticalexpansion.culling;

import com.cyber3d.verticalexpansion.vertical.VerticalIntegration;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "verticalexpansion")
public final class CullingEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static long culledEntitiesCount = 0;

    @SubscribeEvent
    public static void onEntityTickPre(EntityTickEvent.Pre event) {
        var entity = event.getEntity();
        if (entity == null || entity.level() == null || entity.level().isClientSide()) {
            return;
        }
        
        if (shouldCullEntity(entity)) {
            event.setCanceled(true);
            culledEntitiesCount++;
        }
    }

    private static boolean shouldCullEntity(Entity entity) {
        var manager = VerticalIntegration.getManager();
        if (manager == null) {
            return false;
        }
        
        var config = manager.getConfig();
        int minY = config.worldHeightConfig().minY();
        int sectionIndex = ((int) entity.getY() - minY) >> 4;
        
        return !manager.isSectionActive(
            ((int) entity.getX()) >> 4,
            ((int) entity.getZ()) >> 4,
            sectionIndex
        );
    }

    public static long getCulledEntitiesCount() {
        return culledEntitiesCount;
    }

    public static void resetCulledCount() {
        culledEntitiesCount = 0;
    }

    @EventBusSubscriber(modid = "verticalexpansion")
    public static class ServerBlockCulling {

        private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
        private static long culledBlockUpdatesCount = 0;

        @SubscribeEvent
        public static void onBlockNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
            if (event.getLevel().isClientSide()) {
                return;
            }
            
            if (shouldCullBlock(event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), event.getLevel())) {
                event.setCanceled(true);
                culledBlockUpdatesCount++;
            }
        }

        private static boolean shouldCullBlock(int x, int y, int z, LevelAccessor level) {
            var manager = VerticalIntegration.getManager();
            if (manager == null) {
                return false;
            }
            
            var config = manager.getConfig();
            int minY = config.worldHeightConfig().minY();
            int sectionIndex = (y - minY) >> 4;
            
            return !manager.isSectionActive(x >> 4, z >> 4, sectionIndex);
        }

        public static long getCulledBlockUpdatesCount() {
            return culledBlockUpdatesCount;
        }

        public static void resetBlockCulledCount() {
            culledBlockUpdatesCount = 0;
        }
    }
}
