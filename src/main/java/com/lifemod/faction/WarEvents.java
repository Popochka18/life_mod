package com.lifemod.faction;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import java.util.ArrayList;
import java.util.List;

/**
 * Commands to inspect and steer the faction systems:
 * /lifemod rep — show reputation,
 * /lifemod war declare|peace <a> <b> — manage wars (op only),
 * /lifemod war list — list active wars.
 *
 * Neutral factions also declare wars and make peace on their own
 * roughly every half hour; everyone online is notified.
 */
public final class WarEvents {
    private static final long AUTO_WAR_INTERVAL_TICKS = 20L * 60L * 30L;

    private WarEvents() {
    }

    public static void init() {
        registerCommands();
        registerAutoWars();
    }

    private static void registerAutoWars() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.dimension() != Level.OVERWORLD) {
                return;
            }

            long gameTime = level.getGameTime(); // VERIFY-MAPPING: Level#getGameTime

            if (gameTime == 0 || gameTime % AUTO_WAR_INTERVAL_TICKS != 0) {
                return;
            }

            RandomSource random = level.getRandom();

            if (random.nextInt(2) != 0) {
                return; // diplomacy stays calm this time
            }

            WarState war = WarState.get(level.getServer());
            List<Faction> neutrals = new ArrayList<>();

            for (Faction faction : Faction.values()) {
                if (faction.alignment() == Alignment.NEUTRAL) {
                    neutrals.add(faction);
                }
            }

            Faction initiator = neutrals.get(random.nextInt(neutrals.size()));

            // A third of the time an existing war ends instead of a new one starting.
            List<WarState.WarEntry> wars = war.wars();

            if (!wars.isEmpty() && random.nextInt(3) == 0) {
                WarState.WarEntry ended = wars.get(random.nextInt(wars.size()));
                war.makePeace(ended.a(), ended.b());
                broadcast(level, "message.life-mod.war.peace", ended.a(), ended.b());
                return;
            }

            List<Faction> targets = new ArrayList<>();

            for (Faction faction : Faction.values()) {
                if (faction != initiator && faction.alignment() != Alignment.EVIL
                        && !war.atWar(initiator, faction)) {
                    targets.add(faction);
                }
            }

            if (targets.isEmpty()) {
                return;
            }

            Faction target = targets.get(random.nextInt(targets.size()));

            if (war.declareWar(initiator, target)) {
                broadcast(level, "message.life-mod.war.declared", initiator, target);
            }
        });
    }

    private static void broadcast(ServerLevel level, String key, Faction a, Faction b) {
        Component message = Component.translatable(key,
                Component.translatable(a.translationKey()),
                Component.translatable(b.translationKey()));

        // VERIFY-MAPPING: ServerLevel#players
        for (ServerPlayer player : level.players()) {
            player.sendSystemMessage(message);
        }
    }

    private static void registerCommands() {
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
