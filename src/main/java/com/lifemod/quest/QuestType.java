package com.lifemod.quest;

import com.mojang.serialization.Codec;

import java.util.Locale;

/** What the player must do to complete a quest. */
public enum QuestType {
    /** Bring N items to the leader. Given by GOOD and NEUTRAL factions. */
    GATHER,
    /** Kill N entities. Given by EVIL faction leaders (e.g. the bandit ataman). */
    KILL;

    public static final Codec<QuestType> CODEC = Codec.STRING.xmap(
            s -> QuestType.valueOf(s.toUpperCase(Locale.ROOT)),
            t -> t.name().toLowerCase(Locale.ROOT));
}
