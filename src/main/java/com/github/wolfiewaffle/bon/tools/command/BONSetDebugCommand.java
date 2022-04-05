package com.github.wolfiewaffle.bon.tools.command;

import com.github.wolfiewaffle.bon.BONMod;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BONSetDebugCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("bondebug")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(BONSetDebugCommand::setDisplay)
                );

        dispatcher.register(command);
    }

    static int setDisplay(CommandContext<CommandSourceStack> commandContext) {
        boolean value = BoolArgumentType.getBool(commandContext, "value");
        BONMod.showDisplay = value;
        return Command.SINGLE_SUCCESS;
    }
}
