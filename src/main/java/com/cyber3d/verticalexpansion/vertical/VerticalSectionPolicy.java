package com.cyber3d.verticalexpansion.vertical;

public interface VerticalSectionPolicy {
    
    boolean isActiveSection(SectionContext ctx, ServerVerticalConfig config);
}
