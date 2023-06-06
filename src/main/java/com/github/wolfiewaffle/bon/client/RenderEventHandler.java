package com.github.wolfiewaffle.bon.client;

import com.github.wolfiewaffle.bon.BONMod;
import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.IBodyTemp;
import com.github.wolfiewaffle.bon.config.Config;
import com.github.wolfiewaffle.bon.event.player.PlayerTickEventHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.text.DecimalFormat;

public class RenderEventHandler {

    public RenderEventHandler() {
    }



//    @SubscribeEvent
//    public void render(CustomizeGuiOverlayEvent.DebugText event) {
//        if (BONMod.showDisplay && minecraft.player != null) {
//            LazyOptional<IBodyTemp> tempLazyOptional = minecraft.player.getCapability(BodyTemp.INSTANCE, null);
//            tempLazyOptional.ifPresent(data -> {
//                DecimalFormat df = new DecimalFormat();
//                df.applyPattern("0.000");
//
//                String curTemp = df.format(data.getTemp());
//                String target = df.format(data.getTargetTemp());
//
//                event.getRight().add("Current " + curTemp);
//                event.getRight().add("Target  " + target);
//
//                // Calculate change
//                float tempDiff = data.getTargetTemp() - data.getTemp();
//                float changeAmount = tempDiff / 150;
//                changeAmount *= Config.baseSpeedModifier.get();
//                //changeAmount = BONMath.signMin(changeAmount, BodyTemp.MIN_CHANGE_AMOUNT);
//                changeAmount = (float) Math.pow(changeAmount, 2) * Math.signum(changeAmount);
//                changeAmount *= PlayerTickEventHandler.TICK_RATE_MULTIPLIER; // This ensures 1 changeAmount = 1 degree over one second
//
//                df.applyPattern("0.00000");
//                event.getRight().add("Rate  " + df.format(changeAmount));
//            });
//        }

//        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
//
//        }
//    }
}