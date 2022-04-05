package com.github.wolfiewaffle.bon;

import com.github.wolfiewaffle.bon.capability.BONCapabilityAttacher;
import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.client.RenderEventHandler;
import com.github.wolfiewaffle.bon.event.player.PlayerTickEventHandler;
import com.github.wolfiewaffle.bon.network.BonNetworkInit;
import com.github.wolfiewaffle.bon.tools.command.BONCommands;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("bon")
public class BONMod {

    public static boolean showDisplay;

    public BONMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(BodyTemp::register);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new PlayerTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler(Minecraft.getInstance()));
        MinecraftForge.EVENT_BUS.register(BONCapabilityAttacher.class);
    }

    private void setup(final FMLCommonSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(BONCommands.class);

        BodyTemp.initArmorValues();
        BodyTemp.initBlockValues();
        BonNetworkInit.register();
    }
}
