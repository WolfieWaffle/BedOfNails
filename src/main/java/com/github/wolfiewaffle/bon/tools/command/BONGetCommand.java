package com.github.wolfiewaffle.bon.tools.command;

import com.github.wolfiewaffle.bon.capability.temperature.BodyTemp;
import com.github.wolfiewaffle.bon.capability.temperature.TempModifier;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class BONGetCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command
                = Commands.literal("bonget")
                .requires((commandSource) -> commandSource.hasPermission(2))
                    .executes(BONGetCommand::get);

        dispatcher.register(command);
    }

    static int get(CommandContext<CommandSourceStack> commandContext) throws CommandSyntaxException {
        ServerPlayer player = commandContext.getSource().getPlayerOrException();

        if (BodyTemp.MOD_MAP.containsKey(player)) {
            List<TempModifier> modifiers = BodyTemp.MOD_MAP.get(player);

            player.displayClientMessage(Component.m_237113_(""), false);

            // Display Target Mods
            for (TempModifier mod : modifiers) {
                player.displayClientMessage(Component.m_237113_(mod.toString()), false);
            }

            player.displayClientMessage(Component.m_237113_(""), false);

            player.displayClientMessage(Component.m_237113_(""), false);
        }

        return Command.SINGLE_SUCCESS;
    }
}
