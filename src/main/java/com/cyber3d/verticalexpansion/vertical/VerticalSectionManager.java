package com.cyber3d.verticalexpansion.vertical;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class VerticalSectionManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private final VerticalSectionPolicy policy;
    private final ServerVerticalConfig config;
    private final Map<Long, boolean[]> sectionVisibilityCache = new ConcurrentHashMap<>();
    private static final long CACHE_INVALIDATION_MILLIS = 200;

    public VerticalSectionManager(VerticalSectionPolicy policy, ServerVerticalConfig config) {
        this.policy = policy;
        this.config = config;
    }

    public void updateForTick(Iterable<SectionContext> sections) {
        for (SectionContext ctx : sections) {
            boolean active = policy.isActiveSection(ctx, config);
        }
    }

    public VerticalSectionPolicy getPolicy() {
        return policy;
    }

    public ServerVerticalConfig getConfig() {
        return config;
    }

    public void tick(MinecraftServer server) {
        if (server == null) {
            return;
        }
        
        server.getAllLevels().forEach(level -> {
            if (level != null && !level.players().isEmpty()) {
                for (var player : level.players()) {
                    int chunkX = (int) player.getX() >> 4;
                    int chunkZ = (int) player.getZ() >> 4;
                    
                    try {
                        var chunk = level.getChunk(chunkX, chunkZ);
                        if (chunk instanceof LevelChunk lc) {
                            updateChunkSections(lc);
                        }
                    } catch (Exception e) {
                        if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Error updating sections for chunk at ({}, {})", chunkX, chunkZ, e);
                        }
                    }
                }
            }
        });
    }
    
    private void updateChunkSections(LevelChunk chunk) {
        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        
        var level = chunk.getLevel();
        java.util.List<PlayerView> players = new java.util.ArrayList<>();
        
        for (var player : level.players()) {
            players.add(new PlayerView() {
                @Override
                public int x() {
                    return (int) player.getX();
                }
                
                @Override
                public int y() {
                    return (int) player.getY();
                }
                
                @Override
                public int z() {
                    return (int) player.getZ();
                }
                
                @Override
                public int viewDistanceChunks() {
                    return 8;
                }
            });
        }
        
        int minY = config.worldHeightConfig().minY();
        int maxY = config.worldHeightConfig().maxY();
        int sectionHeight = 16;
        
        int numSections = (maxY - minY) / sectionHeight;
        boolean[] visibility = new boolean[numSections];
        
        for (int sectionIdx = 0; sectionIdx < numSections; sectionIdx++) {
            SectionContextImpl ctx = new SectionContextImpl(chunkX, chunkZ, sectionIdx, players);
            boolean active = policy.isActiveSection(ctx, config);
            visibility[sectionIdx] = active;
            
            if (active) {
                chunk.setUnsaved(true);
            }
        }
        
        long chunkKey = encodeChunkPos(chunkX, chunkZ);
        sectionVisibilityCache.put(chunkKey, visibility);
    }
    
    private static class SectionContextImpl implements SectionContext {
        private final int chunkX;
        private final int chunkZ;
        private final int sectionIndex;
        private final java.util.List<PlayerView> players;
        
        SectionContextImpl(int chunkX, int chunkZ, int sectionIndex, java.util.List<PlayerView> players) {
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.sectionIndex = sectionIndex;
            this.players = players;
        }
        
        @Override
        public int chunkX() {
            return chunkX;
        }
        
        @Override
        public int chunkZ() {
            return chunkZ;
        }
        
        @Override
        public int sectionIndex() {
            return sectionIndex;
        }
        
        @Override
        public Iterable<PlayerView> nearbyPlayers() {
            return players;
        }
    }

    public boolean isSectionActiveForBlock(int x, int y, int z) {
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        int sectionIndex = getSectionIndexForY(y);
        return isSectionActive(chunkX, chunkZ, sectionIndex);
    }
    
    public boolean isSectionActive(int chunkX, int chunkZ, int sectionIndex) {
        long chunkKey = encodeChunkPos(chunkX, chunkZ);
        boolean[] visibility = sectionVisibilityCache.get(chunkKey);
        if (visibility != null && sectionIndex >= 0 && sectionIndex < visibility.length) {
            return visibility[sectionIndex];
        }
        return true;
    }

    private int getSectionIndexForY(int y) {
        int minY = config.worldHeightConfig().minY();
        int sectionHeight = 16;
        return (y - minY) / sectionHeight;
    }
    
    private long encodeChunkPos(int x, int z) {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }
    
    public void clearVisibilityCache() {
        sectionVisibilityCache.clear();
    }
    
    public int getCachedSectionsCount() {
        return sectionVisibilityCache.size();
    }
    
}
