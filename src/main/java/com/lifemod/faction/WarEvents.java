package com.lifemod.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

/**
 * Commands to inspect and steer the faction systems:
 * /lifemod rep — show reputation,
 * /lifemod war declare|peace <a> <b> — manage wars (op only),
 * /lifemod war list — list active wars.
 */
public final class WarEvents {
    private WarEvents() {
    }

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, buildContext, selection) -> {
            dispatcher.register(Commands.literal("lifemod")
                    .then(Commands.literal("rep").executes(WarEvents::showReputation))
                    .then(Commands.literal("war")
                            .then(Commands.literal("list").executes(WarEvents::listWars))
                            .then(Commands.literal("declare")
                                    .requires(source -> source.hasPermission(2)) // VERIFY-MAPPING: CommandSourceStack#hasPermission
                                    .then(Commands.argument("a", StringArgumentType.word())
                                            .then(Commands.argument("b", StringArgumentType.word())
                                                    .executes(context -> changeWar(context, true)))))
                            .then(Commands.literal("peace")
                                    .requires(source -> source.hasPermission(2))
                                    .then(Commands.argument("a", StringArgumentType.word())
                                            .then(Commands.argument("b", StringArgumentType.word())
                                                    .executes(context -> changeWar(context, false)))))));
        });
    }

    private static int showReputation(CommandContext<CommandSourceStack> context) {
        if (!(context.getSource().getEntity() instanceof ServerPlayer player)) { // VERIFY-MAPPING: CommandSourceStack#getEntity
            return 0;
        }

        for (Faction faction : Faction.values()) {
            int reputation = ReputationApi.getReputation(player, faction);

            if (reputation != 0) {
                player.sendSystemMessage(Component.translatable("message.life-mod.rep.line",
                        Component.translatable(faction.translationKey()), reputation));
            }
        }

        return 1;
    }

    private static int listWars(CommandContext<CommandSourceStack> context) {
        WarState war = WarState.get(context.getSource().getServer()); // VERIFY-MAPPING: CommandSourceStack#getServer

        for (WarState.WarEntry entry : war.wars()) {
            context.getSource().sendSystemMessage(Component.translatable("message.life-mod.war.line",
                    Component.translatable(entry.a().translationKey()),
                    Component.translatable(entry.b().translationKey())));
        }

        return 1;
    }

    private static int changeWar(CommandContext<CommandSourceStack> context, boolean declare) {
        Faction a = Faction.byName(StringArgumentType.getString(context, "a"));
        Faction b = Faction.byName(StringArgumentType.getString(context, "b"));
        WarState war = WarState.get(context.getSource().getServer());
        boolean changed = declare ? war.declareWar(a, b) : war.makePeace(a, b);
        context.getSource().sendSystemMessage(Component.translatable(
                changed ? (declare ? "message.life-mod.war.declared" : "message.life-mod.war.peace")
                        : "message.life-mod.war.unchanged",
                Component.translatable(a.translationKey()),
                Component.translatable(b.translationKey())));
        return changed ? 1 : 0;
    }
}
