package com.cyber3d.verticalexpansion.debug;

import com.cyber3d.verticalexpansion.core.VerticalExpansionConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class DebugToggleCommand {

    private DebugToggleCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
            Commands.literal("ve_debug")
                .requires(src -> src.hasPermission(2))
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                    .executes(ctx -> {
                        boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                        VerticalExpansionConfig.setDebugLoggingEnabled(enabled);
                        ctx.getSource().sendSuccess(
                            () -> Component.literal("[VerticalExpansion] Debug logging set to " + enabled),
                            false
                        );
                        return 1;
                    }))
        );
    }
}
