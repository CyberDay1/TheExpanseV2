package com.cyber3d.verticalexpansion.debug;

import com.cyber3d.verticalexpansion.vertical.VerticalIntegration;
import com.cyber3d.verticalexpansion.vertical.VerticalSectionManager;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class SectionDebugCommand {

    private SectionDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ve_section_info")
                .requires(src -> src.hasPermission(2))
                .executes(ctx -> {
                    CommandSourceStack src = ctx.getSource();
                    ServerPlayer player = src.getPlayerOrException();
                    return execute(src, player);
                })
        );
    }

    private static int execute(CommandSourceStack src, ServerPlayer player) {
        VerticalSectionManager manager = VerticalIntegration.getManager();
        if (manager == null) {
            src.sendFailure(Component.literal("[VerticalExpansion] VerticalSectionManager is not initialized."));
            return 0;
        }

        int x = player.getBlockX();
        int y = player.getBlockY();
        int z = player.getBlockZ();

        boolean active = manager.isSectionActiveForBlock(x, y, z);

        src.sendSuccess(
            () -> Component.literal(
                String.format("[VerticalExpansion] Section info at (%d, %d, %d): active=%s",
                    x, y, z, active)
            ),
            false
        );

        return 1;
    }
}
