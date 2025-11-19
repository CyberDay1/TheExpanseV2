package com.cyber3d.verticalexpansion.ore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class OreProfileRegistry {

    private static final OreProfileRegistry INSTANCE = new OreProfileRegistry();
    private final Map<String, OreProfile> profiles = new HashMap<>();

    private OreProfileRegistry() {
    }

    public static OreProfileRegistry getInstance() {
        return INSTANCE;
    }

    public void register(String id, OreProfile profile) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("ID cannot be null or empty");
        }
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        profiles.put(id, profile);
    }

    public Optional<OreProfile> get(String id) {
        return Optional.ofNullable(profiles.get(id));
    }

    public Map<String, OreProfile> getAll() {
        return new HashMap<>(profiles);
    }

    public boolean contains(String id) {
        return profiles.containsKey(id);
    }

    public void clear() {
        profiles.clear();
    }
}
