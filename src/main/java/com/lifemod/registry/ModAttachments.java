package com.lifemod.registry;

import com.lifemod.LifeModIds;
import com.lifemod.faction.Faction;
import com.lifemod.quest.QuestInstance;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import java.util.HashMap;
import java.util.Map;

/** Persistent per-player data: faction reputation and active quests. */
public final class ModAttachments {
    /** Reputation with each faction, survives death. */
    public static final AttachmentType<Map<Faction, Integer>> REPUTATION = AttachmentRegistry.create(
            LifeModIds.id("reputation"),
            builder -> builder
                    .initializer(HashMap::new)
                    .persistent(Codec.unboundedMap(Faction.CODEC, Codec.INT))
                    .copyOnDeath());

    /** Active quests, at most one per faction, survives death. */
    public static final AttachmentType<Map<Faction, QuestInstance>> ACTIVE_QUESTS = AttachmentRegistry.create(
            LifeModIds.id("active_quests"),
            builder -> builder
                    .initializer(HashMap::new)
                    .persistent(Codec.unboundedMap(Faction.CODEC, QuestInstance.CODEC))
                    .copyOnDeath());

    private ModAttachments() {
    }

    public static void init() {
        // Static initialisation registers the attachment types.
    }
}
