package com.lifemod.faction;

import net.minecraft.server.MinecraftServer;

/** Pure rules describing which factions fight each other. */
public final class FactionRelations {
    private FactionRelations() {
    }

    /**
     * Whether members of faction {@code self} should attack members of faction {@code other}.
     *
     * <ul>
     *   <li>GOOD factions never attack anyone (they rely on golems and hired mercenaries).</li>
     *   <li>EVIL factions attack every other faction by default.</li>
     *   <li>NEUTRAL factions attack only factions they are formally at war with.</li>
     * </ul>
     */
    public static boolean hostile(Faction self, Faction other, WarState war) {
        if (self == other) {
            return false;
        }

        if (self.alignment() == Alignment.GOOD) {
            return false;
        }

        if (self.alignment() == Alignment.EVIL) {
            return true;
        }

        return war.atWar(self, other);
    }

    public static boolean hostile(Faction self, Faction other, MinecraftServer server) {
        return hostile(self, other, WarState.get(server));
    }

    /** Factions never attack players unprovoked, regardless of alignment. */
    public static boolean attacksPlayersByDefault(Faction faction) {
        return false;
    }
}
