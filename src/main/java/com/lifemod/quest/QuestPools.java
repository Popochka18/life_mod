package com.lifemod.quest;

import com.lifemod.faction.Alignment;
import com.lifemod.faction.Faction;
import com.lifemod.faction.WarState;

import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.List;

/** Static quest templates picked when a leader offers a quest. */
public final class QuestPools {
    private record Template(QuestType type, String targetId, int min, int max, int emeraldsPerUnit, int reputation) {
    }

    private static final List<Template> GATHER = List.of(
            new Template(QuestType.GATHER, "minecraft:wheat", 12, 32, 0, 10),
            new Template(QuestType.GATHER, "minecraft:leather", 6, 16, 0, 12),
            new Template(QuestType.GATHER, "minecraft:cod", 8, 20, 0, 10),
            new Template(QuestType.GATHER, "minecraft:oak_log", 16, 48, 0, 10),
            new Template(QuestType.GATHER, "minecraft:iron_ingot", 4, 12, 0, 15),
            new Template(QuestType.GATHER, "minecraft:string", 8, 24, 0, 10),
            new Template(QuestType.GATHER, "minecraft:coal", 12, 32, 0, 10),
            new Template(QuestType.GATHER, "minecraft:bone", 8, 20, 0, 12)
    );

    private static final List<Template> KILL = List.of(
            new Template(QuestType.KILL, "minecraft:zombie", 5, 12, 0, 12),
            new Template(QuestType.KILL, "minecraft:skeleton", 5, 12, 0, 12),
            new Template(QuestType.KILL, "minecraft:spider", 4, 10, 0, 10),
            new Template(QuestType.KILL, "minecraft:creeper", 3, 8, 0, 15),
            new Template(QuestType.KILL, "life-mod:dark_spider", 3, 8, 0, 15),
            new Template(QuestType.KILL, "life-mod:yeti", 1, 3, 0, 20),
            new Template(QuestType.KILL, "life-mod:mummy", 3, 8, 0, 15)
    );

    private QuestPools() {
    }

    /**
     * Picks a quest for the given faction: GOOD leaders give gathering quests,
     * EVIL leaders (the bandit ataman among them) give kill quests only —
     * sometimes against a whole enemy faction. NEUTRAL leaders gather in
     * peacetime but order strikes against factions they are at war with.
     * Faction-wide kill targets use the "faction:<name>" target id.
     */
    public static QuestInstance pick(Faction faction, RandomSource random, WarState war) {
        if (faction.alignment() == Alignment.EVIL) {
            if (random.nextInt(5) < 2) {
                return factionKillQuest(faction, randomEnemy(faction, random), random);
            }

            return fromPool(faction, KILL, random);
        }

        if (faction.alignment() == Alignment.NEUTRAL) {
            List<Faction> enemies = new ArrayList<>();

            for (Faction other : Faction.values()) {
                if (other != faction && war.atWar(faction, other)) {
                    enemies.add(other);
                }
            }

            if (!enemies.isEmpty() && random.nextInt(2) == 0) {
                return factionKillQuest(faction, enemies.get(random.nextInt(enemies.size())), random);
            }
        }

        return fromPool(faction, GATHER, random);
    }

    private static Faction randomEnemy(Faction faction, RandomSource random) {
        List<Faction> others = new ArrayList<>();

        for (Faction other : Faction.values()) {
            if (other != faction) {
                others.add(other);
            }
        }

        return others.get(random.nextInt(others.size()));
    }

    private static QuestInstance factionKillQuest(Faction issuer, Faction target, RandomSource random) {
        int required = 3 + random.nextInt(5);
        return new QuestInstance(issuer, QuestType.KILL, "faction:" + target.getSerializedName(),
                required, 0, Math.max(3, required), 15);
    }

    private static QuestInstance fromPool(Faction faction, List<Template> pool, RandomSource random) {
        Template template = pool.get(random.nextInt(pool.size()));
        int required = template.min() + random.nextInt(template.max() - template.min() + 1);
        int emeralds = Math.max(2, required / 3);
        return new QuestInstance(faction, template.type(), template.targetId(), required,
                0, emeralds, template.reputation());
    }
}
