package com.cyber3d.verticalexpansion.platform;

/**
 * PlatformVersion provides basic information about the current Minecraft version.
 *
 * This is mainly used to choose the appropriate PlatformHooks implementation.
 */
public final class PlatformVersion {

    private static final int MC_MAJOR = 1;
    private static final int MC_MINOR = 21;
    private static final int MC_PATCH = detectPatchVersion();

    private PlatformVersion() { }

    public static int major() {
        return MC_MAJOR;
    }

    public static int minor() {
        return MC_MINOR;
    }

    public static int patch() {
        return MC_PATCH;
    }

    public static boolean isAtLeastPatch(int patch) {
        return MC_PATCH >= patch;
    }

    private static int detectPatchVersion() {
        try {
            Class<?> sharedConstants = Class.forName("net.minecraft.SharedConstants");
            java.lang.reflect.Field releaseTarget = sharedConstants.getField("RELEASE_TARGET");
            Object target = releaseTarget.get(null);
            
            if (target != null) {
                String targetStr = target.toString();
                if (targetStr.contains("1.21.")) {
                    String[] parts = targetStr.split("\\.");
                    if (parts.length >= 3) {
                        try {
                            return Integer.parseInt(parts[2]);
                        } catch (NumberFormatException e) {
                            return 1;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Fallback if reflection fails
        }
        
        return 1;
    }
}
