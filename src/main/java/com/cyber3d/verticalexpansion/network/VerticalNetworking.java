package com.cyber3d.verticalexpansion.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VerticalNetworking {

    private static final Logger LOGGER = LoggerFactory.getLogger("VerticalExpansion");

    private VerticalNetworking() {
    }

    public static void registerPackets() {
        LOGGER.debug("Registering vertical expansion network packets");
    }
}
