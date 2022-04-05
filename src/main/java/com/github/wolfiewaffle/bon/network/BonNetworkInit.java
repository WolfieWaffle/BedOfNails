package com.github.wolfiewaffle.bon.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class BonNetworkInit {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("bon", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register () {
        CHANNEL.registerMessage(0, BonPacket.class, (msg, buf) -> {BonPacket.encode(msg, buf);}, (buf) -> {return BonPacket.decode(buf);}, (p, supplier) -> {BonPacket.handle(p, supplier);});
    }
}
