package com.github.wolfiewaffle.bon.tools.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BONSetTargetCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("bontarget")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("value", FloatArgumentType.floatArg(0, 200))
                        .executes(BONSetTargetCommand::setDebug)
                );

        dispatcher.register(command);
    }

    static int setDebug(CommandContext<CommandSourceStack> commandContext) {
        float value = FloatArgumentType.getFloat(commandContext, "value");
        BONCommands.targetTemp = value;
        return Command.SINGLE_SUCCESS;
    }
}
