package com.lifemod.faction;

import com.lifemod.entity.BanditEntity;
import com.lifemod.registry.ModEntities;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

/**
 * Bandit raids: every ~20 minutes a war party spawns near a random player.
 * Bandits never target the player, but they assault every faction NPC nearby —
 * and their victims are enslaved instead of killed (see {@link FactionEvents}).
 */
public final class BanditRaids {
    private static final long INTERVAL_TICKS = 20L * 60L * 20L;
    private static final int MIN_DISTANCE = 32;
    private static final int MAX_DISTANCE = 56;

    private BanditRaids() {
    }

    public static void init() {
        ServerTickEvents.END_LEVEL_TICK.register(level -> {
            if (level.dimension() != Level.OVERWORLD) {
                return;
            }

            long gameTime = level.getGameTime(); // VERIFY-MAPPING: Level#getGameTime

            if (gameTime == 0 || gameTime % INTERVAL_TICKS != 0) {
                return;
            }

            // VERIFY-MAPPING: ServerLevel#players
            if (level.players().isEmpty()) {
                return;
            }

            RandomSource random = level.getRandom();
            ServerPlayer player = level.players().get(random.nextInt(level.players().size()));

            if (random.nextInt(3) == 0) {
                return; // sometimes the night stays quiet
            }

            spawnRaid(level, player, random);
        });
    }

    private static void spawnRaid(ServerLevel level, ServerPlayer player, RandomSource random) {
        double angle = random.nextDouble() * Math.PI * 2;
        int distance = MIN_DISTANCE + random.nextInt(MAX_DISTANCE - MIN_DISTANCE);
        int centerX = (int) (player.getX() + Math.cos(angle) * distance);
        int centerZ = (int) (player.getZ() + Math.sin(angle) * distance);
        int count = 4 + random.nextInt(3);
        boolean spawnedAny = false;

        for (int i = 0; i < count; i++) {
            int x = centerX + random.nextInt(7) - 3;
            int z = centerZ + random.nextInt(7) - 3;
            // VERIFY-MAPPING: Level#getHeight(Heightmap.Types, int, int)
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);

            // VERIFY-MAPPING: EntitySpawnReason.EVENT (SPAWN_ITEM_USE is confirmed to exist)
            BanditEntity bandit = ModEntities.BANDIT.create(level, EntitySpawnReason.EVENT);

            if (bandit == null) {
                continue;
            }

            bandit.setPos(x + 0.5, y, z + 0.5);
            bandit.setPersistenceRequired(); // VERIFY-MAPPING: Mob#setPersistenceRequired
            level.addFreshEntity(bandit);
            spawnedAny = true;
        }

        if (spawnedAny) {
            player.sendSystemMessage(Component.translatable("message.life-mod.raid.warning"));
        }
    }
}
