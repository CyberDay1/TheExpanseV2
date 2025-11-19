package com.cyber3d.verticalexpansion.vertical;

public final class VerticalSectionManager {

    private final VerticalSectionPolicy policy;
    private final ServerVerticalConfig config;

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

    public void tick() {
    }

    public boolean isSectionActiveForBlock(int x, int y, int z) {
        int sectionIndex = getSectionIndexForY(y);
        return isSectionActive(sectionIndex);
    }

    private int getSectionIndexForY(int y) {
        int minY = config.worldHeightConfig().minY();
        int sectionHeight = 16;
        return (y - minY) / sectionHeight;
    }

    private boolean isSectionActive(int sectionIndex) {
        return true;
    }
}
