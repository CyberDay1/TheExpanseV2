package com.cyber3d.verticalexpansion.debug;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.cyber3d.verticalexpansion.terrain.WorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.DefaultWorldTerrainProfile;
import com.cyber3d.verticalexpansion.terrain.NoiseBasedTerrainHeightFunction;
import com.cyber3d.verticalexpansion.terrain.PerlinNoiseSampler;
import com.cyber3d.verticalexpansion.worldgen.DensityFunctionIntegration;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class HeightDebugCommand {

    private HeightDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ve_height")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> {
                    CommandSourceStack src = ctx.getSource();
                    int x = src.getPlayerOrException().getBlockX();
                    int z = src.getPlayerOrException().getBlockZ();
                    return execute(src, x, z);
                })
                .then(Commands.argument("x", IntegerArgumentType.integer())
                    .then(Commands.argument("z", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            CommandSourceStack src = ctx.getSource();
                            int x = IntegerArgumentType.getInteger(ctx, "x");
                            int z = IntegerArgumentType.getInteger(ctx, "z");
                            return execute(src, x, z);
                        })))
        );
    }

    private static int execute(CommandSourceStack src, int x, int z) {
        // Rebuild the terrain profile in the same way WorldGenInitializer does.
        var cfg = VerticalExpansionConfig.getWorldHeightConfig();
        WorldTerrainProfile profile = DefaultWorldTerrainProfile.standard(cfg);

        NoiseBasedTerrainHeightFunction heightFunction = new NoiseBasedTerrainHeightFunction(
            new PerlinNoiseSampler(null, profile.continentsScale()),
            new PerlinNoiseSampler(null, profile.erosionScale()),
            new PerlinNoiseSampler(null, profile.ridgeScale()),
            new PerlinNoiseSampler(null, profile.valleyScale()),
            new PerlinNoiseSampler(null, profile.detailScale())
        );

        double height = heightFunction.computeHeight(x, z, profile);
        src.sendSuccess(
            () -> Component.literal(
                String.format("[VerticalExpansion] Height at (%d, %d) = %.2f", x, z, height)
            ),
            false
        );
        return 1;
    }
}
