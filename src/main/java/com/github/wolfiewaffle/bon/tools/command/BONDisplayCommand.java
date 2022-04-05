package com.github.wolfiewaffle.bon.tools.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class BONDisplayCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("bondisplay")
                .requires((commandSource) -> commandSource.hasPermission(0))
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(BONSetDebugCommand::setDisplay)
                );

        dispatcher.register(command);
    }

    static void setDisplay(CommandContext<CommandSourceStack> commandContext) {
        boolean value = BoolArgumentType.getBool(commandContext, "value");
        BONCommands.isDebugging = value;
    }
}
