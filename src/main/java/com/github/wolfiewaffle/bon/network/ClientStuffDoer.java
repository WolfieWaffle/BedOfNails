package com.github.wolfiewaffle.bon.network;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.IBodyTemp;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientStuffDoer {

    public static void handleBONPacket(float temp, float targetTemp, Supplier<NetworkEvent.Context> ctx) {
        Player player = Minecraft.getInstance().player;

        ctx.get().enqueueWork(() -> {
            if (player != null) {
                LazyOptional<IBodyTemp> tempLazyOptional = player.getCapability(BodyTemp.INSTANCE, null);
                tempLazyOptional.ifPresent(data -> {
                    data.setTemp(temp);
                    data.setTargetTemp(targetTemp);
                });
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
