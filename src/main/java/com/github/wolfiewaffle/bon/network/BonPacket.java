package com.github.wolfiewaffle.bon.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BonPacket {
    float temp;
    float targetTemp;

    public BonPacket(float temp, float targetTemp) {
        this.temp = temp;
        this.targetTemp = targetTemp;
    }

    public static void encode(BonPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.temp);
        buf.writeFloat(msg.targetTemp);
    }

    public static BonPacket decode(FriendlyByteBuf buf) {
        float temp = buf.readFloat();
        float targetTemp = buf.readFloat();

        return new BonPacket(temp, targetTemp);
    }

    public static void handle(BonPacket p, Supplier<NetworkEvent.Context> ctx) {
        ClientStuffDoer.handleBONPacket(p.temp, p.targetTemp, ctx);
    }
}