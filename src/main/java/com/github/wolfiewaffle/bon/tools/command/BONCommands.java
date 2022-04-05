package com.github.wolfiewaffle.bon.tools.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BONCommands {
    public static boolean isDebugging = false;
    public static float targetTemp = 70f;

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
        //BONSetDebugCommand.register(commandDispatcher);
        //BONSetTargetCommand.register(commandDispatcher);
        BONSetCommand.register(commandDispatcher);
        BONGetCommand.register(commandDispatcher);
        BONDisplayCommand.register(commandDispatcher);
    }
}
