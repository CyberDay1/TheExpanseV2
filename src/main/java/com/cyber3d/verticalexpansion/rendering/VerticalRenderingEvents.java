package com.cyber3d.verticalexpansion.rendering;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.vertical.VerticalIntegration;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "verticalexpansion", value = Dist.CLIENT)
public final class VerticalRenderingEvents {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");
    private static int lastCameraSectionY = 0;
    private static int lastVisibleMin = 0;
    private static int lastVisibleMax = 0;

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        var manager = VerticalIntegration.getManager();
        if (manager == null) {
            return;
        }
        
        var camera = event.getCamera();
        if (camera == null) {
            return;
        }
        
        int cameraChunkX = ((int) camera.getPosition().x) >> 4;
        int cameraChunkZ = ((int) camera.getPosition().z) >> 4;
        int cameraY = (int) camera.getPosition().y;
        
        updateVerticalVisibility(manager, cameraChunkX, cameraChunkZ, cameraY);
    }

    private static void updateVerticalVisibility(
            com.cyber3d.verticalexpansion.vertical.VerticalSectionManager manager,
            int cameraChunkX, int cameraChunkZ, int cameraY) {
        
        var config = manager.getConfig();
        int minY = config.worldHeightConfig().minY();
        int maxY = config.worldHeightConfig().maxY();
        int verticalWindow = config.verticalSectionWindow();
        
        int cameraSectionY = (cameraY - minY) >> 4;
        int minVisibleSection = Math.max(0, cameraSectionY - verticalWindow);
        int maxVisibleSection = Math.min((maxY - minY) >> 4, cameraSectionY + verticalWindow);
        
        lastCameraSectionY = cameraSectionY;
        lastVisibleMin = minVisibleSection;
        lastVisibleMax = maxVisibleSection;
        
        if (VerticalExpansionConfig.isDebugLoggingEnabled()) {
            LOGGER.debug("Culling: Camera at section {}, visible range [{}, {}]", 
                cameraSectionY, minVisibleSection, maxVisibleSection);
        }
    }
    
    public static boolean isSectionVisibleFromCamera(int sectionY) {
        return sectionY >= lastVisibleMin && sectionY <= lastVisibleMax;
    }
}
