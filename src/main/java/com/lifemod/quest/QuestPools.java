package com.lifemod.quest;

import com.lifemod.faction.Alignment;
import com.lifemod.faction.Faction;

import net.minecraft.util.RandomSource;

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
     * Picks a quest for the given faction: GOOD and NEUTRAL leaders give gathering quests,
     * EVIL leaders (the bandit ataman among them) give kill quests only.
     */
    public static QuestInstance pick(Faction faction, RandomSource random) {
        List<Template> pool = faction.alignment() == Alignment.EVIL ? KILL : GATHER;
        Template template = pool.get(random.nextInt(pool.size()));
        int required = template.min() + random.nextInt(template.max() - template.min() + 1);
        int emeralds = Math.max(2, required / 3);
        return new QuestInstance(faction, template.type(), template.targetId(), required,
                0, emeralds, template.reputation());
    }
}
