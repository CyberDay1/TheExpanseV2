package com.cyber3d.verticalexpansion.vertical;

public interface SectionContext {
    int chunkX();
    int chunkZ();
    int sectionIndex();
    Iterable<PlayerView> nearbyPlayers();
}
