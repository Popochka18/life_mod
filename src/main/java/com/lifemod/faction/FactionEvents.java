package com.lifemod.faction;

import com.lifemod.entity.FactionLeaderEntity;
import com.lifemod.entity.FactionMob;
import com.lifemod.entity.FactionVillagerEntity;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

/**
 * Enslavement rule: when a bandit (or any faction able to enslave) would kill
 * a faction villager, the victim survives and is enslaved instead.
 */
public final class FactionEvents {
    private FactionEvents() {
    }

    public static void init() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
            if (!(entity instanceof FactionVillagerEntity victim) || victim.isEnslaved()
                    || victim instanceof FactionLeaderEntity) {
                return true;
            }

            // VERIFY-MAPPING: DamageSource#getEntity
            if (!(damageSource.getEntity() instanceof FactionMob attacker)) {
                return true;
            }

            Faction attackerFaction = attacker.getFaction();
            boolean canEnslave = attackerFaction == Faction.BANDITS
                    || attackerFaction.alignment() == Alignment.NEUTRAL;

            if (!canEnslave || attackerFaction == victim.getFaction()) {
                return true;
            }

            victim.setHealth(4.0F); // VERIFY-MAPPING: LivingEntity#setHealth
            victim.setEnslaved(true);
            victim.setFaction(attackerFaction);
            victim.setTarget(null);
            return false;
        });
    }
}
