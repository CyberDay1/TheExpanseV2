package com.cyber3d.verticalexpansion.vertical;

import java.util.ArrayList;
import java.util.List;

public final class SimpleVerticalSectionPolicy implements VerticalSectionPolicy {

    @Override
    public boolean isActiveSection(SectionContext ctx, ServerVerticalConfig config) {
        int minY = config.worldHeightConfig().minY();
        int verticalWindow = config.verticalSectionWindow();
        
        int playerSectionThreshold = (1 << 4);
        
        for (PlayerView player : ctx.nearbyPlayers()) {
            int playerChunkX = player.x() >> 4;
            int playerChunkZ = player.z() >> 4;
            int viewDistance = player.viewDistanceChunks();
            
            int chunkDistX = Math.abs(ctx.chunkX() - playerChunkX);
            int chunkDistZ = Math.abs(ctx.chunkZ() - playerChunkZ);
            int chunkDist = Math.max(chunkDistX, chunkDistZ);
            
            if (chunkDist > viewDistance) {
                continue;
            }
            
            int playerSectionIndex = (player.y() - minY) >> 4;
            int sectionDelta = Math.abs(ctx.sectionIndex() - playerSectionIndex);
            
            if (sectionDelta <= verticalWindow) {
                return true;
            }
        }
        
        return false;
    }
}
