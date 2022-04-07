package com.github.wolfiewaffle.bon.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy {

    public static void registerClientEvents() {
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler(Minecraft.getInstance()));
    }
}
