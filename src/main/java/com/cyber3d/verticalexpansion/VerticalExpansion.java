package com.cyber3d.verticalexpansion;

import com.cyber3d.verticalexpansion.debug.HeightDebugCommand;
import com.cyber3d.verticalexpansion.debug.SectionDebugCommand;
import com.cyber3d.verticalexpansion.debug.DebugToggleCommand;
import com.cyber3d.verticalexpansion.platform.Platform;
import com.cyber3d.verticalexpansion.worldgen.bootstrap.VerticalBootstrap;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod("verticalexpansion")
public class VerticalExpansion {

    public VerticalExpansion(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.addListener(this::onRegisterCommands);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Platform.hooks().init();
            VerticalBootstrap.initialize();
        });
    }

    private void onRegisterCommands(RegisterCommandsEvent event) {
        HeightDebugCommand.register(event.getDispatcher());
        SectionDebugCommand.register(event.getDispatcher());
        DebugToggleCommand.register(event.getDispatcher());
    }
}
