package com.lifemod.quest;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;

/** Wires kill tracking into the quest system. */
public final class QuestEvents {
    private QuestEvents() {
    }

    public static void init() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register((level, entity, killedEntity, damageSource) -> {
            if (entity instanceof ServerPlayer player) {
                Identifier killedId = BuiltInRegistries.ENTITY_TYPE.getKey(killedEntity.getType());
                QuestApi.onKill(player, killedId);
            }
        });
    }
}
