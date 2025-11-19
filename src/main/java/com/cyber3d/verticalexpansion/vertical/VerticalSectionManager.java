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
}
