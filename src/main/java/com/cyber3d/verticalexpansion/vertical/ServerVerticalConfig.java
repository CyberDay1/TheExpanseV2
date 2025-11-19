package com.cyber3d.verticalexpansion.vertical;

import com.cyber3d.verticalexpansion.core.WorldHeightConfig;

public interface ServerVerticalConfig {
    WorldHeightConfig worldHeightConfig();
    
    int verticalSectionWindow();
    
    int maxActiveSectionsPerColumn();
}
