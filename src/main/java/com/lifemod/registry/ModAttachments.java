package com.lifemod.registry;

import com.lifemod.LifeModIds;
import com.lifemod.faction.Faction;
import com.lifemod.quest.QuestInstance;
import com.lifemod.quest.QuestType;
import com.mojang.serialization.Codec;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.HashMap;
import java.util.Map;

/**
 * Persistent per-player data: faction reputation and active quests.
 * Both are synced to the owning client so the journal screen (key J) can show them.
 */
public final class ModAttachments {
    // VERIFY-MAPPING: FriendlyByteBuf#writeUtf/readUtf/writeVarInt/readVarInt
    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Faction, Integer>> REPUTATION_STREAM =
            StreamCodec.of((buf, map) -> {
                buf.writeVarInt(map.size());
                map.forEach((faction, value) -> {
                    buf.writeUtf(faction.getSerializedName());
                    buf.writeVarInt(value);
                });
            }, buf -> {
                int size = buf.readVarInt();
                Map<Faction, Integer> map = new HashMap<>();

                for (int i = 0; i < size; i++) {
                    map.put(Faction.byName(buf.readUtf()), buf.readVarInt());
                }

                return map;
            });

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Faction, QuestInstance>> QUESTS_STREAM =
            StreamCodec.of((buf, map) -> {
                buf.writeVarInt(map.size());
                map.forEach((faction, quest) -> {
                    buf.writeUtf(faction.getSerializedName());
                    buf.writeUtf(quest.type().name());
                    buf.writeUtf(quest.targetId());
                    buf.writeVarInt(quest.required());
                    buf.writeVarInt(quest.progress());
                    buf.writeVarInt(quest.rewardEmeralds());
                    buf.writeVarInt(quest.rewardReputation());
                });
            }, buf -> {
                int size = buf.readVarInt();
                Map<Faction, QuestInstance> map = new HashMap<>();

                for (int i = 0; i < size; i++) {
                    Faction faction = Faction.byName(buf.readUtf());
                    QuestType type = QuestType.valueOf(buf.readUtf());
                    String targetId = buf.readUtf();
                    int required = buf.readVarInt();
                    int progress = buf.readVarInt();
                    int rewardEmeralds = buf.readVarInt();
                    int rewardReputation = buf.readVarInt();
                    map.put(faction, new QuestInstance(faction, type, targetId, required,
                            progress, rewardEmeralds, rewardReputation));
                }

                return map;
            });

    /** Reputation with each faction, survives death, synced to the owner. */
    public static final AttachmentType<Map<Faction, Integer>> REPUTATION = AttachmentRegistry.create(
            LifeModIds.id("reputation"),
            builder -> builder
                    .initializer(HashMap::new)
                    .persistent(Codec.unboundedMap(Faction.CODEC, Codec.INT))
                    .copyOnDeath()
                    .syncWith(REPUTATION_STREAM, AttachmentSyncPredicate.targetOnly()));

    /** Active quests, at most one per faction, survives death, synced to the owner. */
    public static final AttachmentType<Map<Faction, QuestInstance>> ACTIVE_QUESTS = AttachmentRegistry.create(
            LifeModIds.id("active_quests"),
            builder -> builder
                    .initializer(HashMap::new)
                    .persistent(Codec.unboundedMap(Faction.CODEC, QuestInstance.CODEC))
                    .copyOnDeath()
                    .syncWith(QUESTS_STREAM, AttachmentSyncPredicate.targetOnly()));

    private ModAttachments() {
    }

    public static void init() {
        // Static initialisation registers the attachment types.
    }
}
