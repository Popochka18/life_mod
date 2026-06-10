package com.lifemod.faction;

/** Moral alignment of a faction. */
public enum Alignment {
    /** Cannot use violence at all, even in self-defence; relies on golems and mercenaries. */
    GOOD,
    /** May declare wars on other factions and enslave them. */
    NEUTRAL,
    /** At war with every other faction by default, but never attacks players unprovoked. */
    EVIL
}
