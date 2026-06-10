package com.lifemod.quest;

import com.lifemod.faction.Faction;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * A quest a player accepted from a faction leader.
 *
 * @param faction          faction that issued the quest
 * @param type             gather or kill
 * @param targetId         item id (GATHER) or entity type id (KILL), e.g. "minecraft:wheat"
 * @param required         amount of items to bring / entities to kill
 * @param progress         kills so far (only used for KILL quests; GATHER counts the inventory on turn-in)
 * @param rewardEmeralds   emeralds paid on completion
 * @param rewardReputation reputation gained with the faction on completion
 */
public record QuestInstance(Faction faction, QuestType type, String targetId, int required,
                            int progress, int rewardEmeralds, int rewardReputation) {
    public static final Codec<QuestInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Faction.CODEC.fieldOf("faction").forGetter(QuestInstance::faction),
            QuestType.CODEC.fieldOf("type").forGetter(QuestInstance::type),
            Codec.STRING.fieldOf("target").forGetter(QuestInstance::targetId),
            Codec.INT.fieldOf("required").forGetter(QuestInstance::required),
            Codec.INT.fieldOf("progress").forGetter(QuestInstance::progress),
            Codec.INT.fieldOf("reward_emeralds").forGetter(QuestInstance::rewardEmeralds),
            Codec.INT.fieldOf("reward_reputation").forGetter(QuestInstance::rewardReputation)
    ).apply(instance, QuestInstance::new));

    public QuestInstance withProgress(int newProgress) {
        return new QuestInstance(this.faction, this.type, this.targetId, this.required,
                newProgress, this.rewardEmeralds, this.rewardReputation);
    }

    public boolean isComplete() {
        return this.type == QuestType.KILL && this.progress >= this.required;
    }
}
