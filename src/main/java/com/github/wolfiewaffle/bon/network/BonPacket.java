package com.github.wolfiewaffle.bon.network;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.IBodyTemp;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BonPacket {
    private float temp;
    private float targetTemp;

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
        Player player = Minecraft.getInstance().player;

        ctx.get().enqueueWork(() -> {
            if (player != null) {
                LazyOptional<IBodyTemp> tempLazyOptional = player.getCapability(BodyTemp.INSTANCE, null);
                tempLazyOptional.ifPresent(data -> {
                    data.setTemp(p.temp);
                    data.setTargetTemp(p.targetTemp);
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}