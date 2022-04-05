package com.github.wolfiewaffle.bon.tools.command;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.IBodyTemp;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.util.LazyOptional;

public class BONSetCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("bonset")
                .requires((commandSource) -> commandSource.hasPermission(2))
                .then(Commands.argument("value", FloatArgumentType.floatArg(0, 200))
                        .executes(BONSetCommand::set)
                );

        dispatcher.register(command);
    }

    static int set(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        float value = FloatArgumentType.getFloat(commandContext, "value");

        ServerPlayer player = commandContext.getSource().getPlayerOrException();

        LazyOptional<IBodyTemp> tempLazyOptional = player.getCapability(BodyTemp.INSTANCE, null);

        tempLazyOptional.ifPresent(data -> {
            data.setTemp(value);
        });

        return Command.SINGLE_SUCCESS;
    }
}
