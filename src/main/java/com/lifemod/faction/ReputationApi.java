package com.lifemod.faction;

import com.lifemod.registry.ModAttachments;

import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

/** Helpers around the per-player reputation attachment. */
public final class ReputationApi {
    /** Reputation required to hire mercenaries from a faction. */
    public static final int REP_HIRE_MERCENARY = 20;
    /** Reputation required to buy a house (deed) from a faction leader. */
    public static final int REP_BUY_HOUSE = 50;

    private ReputationApi() {
    }

    public static int getReputation(ServerPlayer player, Faction faction) {
        return player.getAttachedOrCreate(ModAttachments.REPUTATION).getOrDefault(faction, 0);
    }

    public static int addReputation(ServerPlayer player, Faction faction, int delta) {
        Map<Faction, Integer> reputation = new HashMap<>(player.getAttachedOrCreate(ModAttachments.REPUTATION));
        int updated = reputation.getOrDefault(faction, 0) + delta;
        reputation.put(faction, updated);
        player.setAttached(ModAttachments.REPUTATION, reputation);
        return updated;
    }

    public static boolean canHireMercenary(ServerPlayer player, Faction faction) {
        return getReputation(player, faction) >= REP_HIRE_MERCENARY;
    }

    public static boolean canBuyHouse(ServerPlayer player, Faction faction) {
        return getReputation(player, faction) >= REP_BUY_HOUSE;
    }
}
