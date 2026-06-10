package com.lifemod.entity;

import com.lifemod.faction.Faction;

/** Implemented by every mob that belongs to a faction. */
public interface FactionMob {
    Faction getFaction();

    void setFaction(Faction faction);

    /** Survivors of bandit raids are enslaved instead of killed. */
    boolean isEnslaved();

    void setEnslaved(boolean enslaved);
}
