package com.lifemod.quest;

import com.lifemod.faction.Faction;
import com.lifemod.faction.ReputationApi;
import com.lifemod.registry.ModAttachments;
import com.lifemod.util.InventoryUtil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;

/** Offer / progress / turn-in logic for leader quests. */
public final class QuestApi {
    private QuestApi() {
    }

    /** Called when a player right-clicks a faction leader. */
    public static void interact(ServerPlayer player, Faction faction) {
        Map<Faction, QuestInstance> quests = new HashMap<>(player.getAttachedOrCreate(ModAttachments.ACTIVE_QUESTS));
        QuestInstance quest = quests.get(faction);

        if (quest == null) {
            quest = QuestPools.pick(faction, player.getRandom());
            quests.put(faction, quest);
            player.setAttached(ModAttachments.ACTIVE_QUESTS, quests);
            player.sendSystemMessage(Component.translatable(
                    quest.type() == QuestType.KILL ? "message.life-mod.quest.offered.kill" : "message.life-mod.quest.offered.gather",
                    quest.required(), targetName(quest), Component.translatable(faction.translationKey())));
            return;
        }

        if (quest.type() == QuestType.GATHER) {
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.parse(quest.targetId())); // VERIFY-MAPPING: Identifier.parse

            if (InventoryUtil.count(player, item) >= quest.required()) {
                InventoryUtil.consume(player, item, quest.required());
                complete(player, faction, quest, quests);
            } else {
                player.sendSystemMessage(Component.translatable("message.life-mod.quest.progress.gather",
                        InventoryUtil.count(player, item), quest.required(), targetName(quest)));
            }

            return;
        }

        if (quest.isComplete()) {
            complete(player, faction, quest, quests);
        } else {
            player.sendSystemMessage(Component.translatable("message.life-mod.quest.progress.kill",
                    quest.progress(), quest.required(), targetName(quest)));
        }
    }

    private static void complete(ServerPlayer player, Faction faction, QuestInstance quest,
                                 Map<Faction, QuestInstance> quests) {
        quests.remove(faction);
        player.setAttached(ModAttachments.ACTIVE_QUESTS, quests);
        InventoryUtil.give(player, new ItemStack(Items.EMERALD, quest.rewardEmeralds()));
        int reputation = ReputationApi.addReputation(player, faction, quest.rewardReputation());
        player.sendSystemMessage(Component.translatable("message.life-mod.quest.completed",
                quest.rewardEmeralds(), Component.translatable(faction.translationKey()), reputation));
    }

    /** Reports a kill made by the player; updates all matching KILL quests. */
    public static void onKill(ServerPlayer player, Identifier killedEntityTypeId) {
        Map<Faction, QuestInstance> quests = player.getAttachedOrCreate(ModAttachments.ACTIVE_QUESTS);
        Map<Faction, QuestInstance> updated = null;

        for (Map.Entry<Faction, QuestInstance> entry : quests.entrySet()) {
            QuestInstance quest = entry.getValue();

            if (quest.type() != QuestType.KILL || quest.progress() >= quest.required()
                    || !quest.targetId().equals(killedEntityTypeId.toString())) {
                continue;
            }

            if (updated == null) {
                updated = new HashMap<>(quests);
            }

            QuestInstance advanced = quest.withProgress(quest.progress() + 1);
            updated.put(entry.getKey(), advanced);

            if (advanced.progress() >= advanced.required()) {
                player.sendSystemMessage(Component.translatable("message.life-mod.quest.kill_done",
                        Component.translatable(entry.getKey().translationKey())));
            }
        }

        if (updated != null) {
            player.setAttached(ModAttachments.ACTIVE_QUESTS, updated);
        }
    }

    private static Component targetName(QuestInstance quest) {
        Identifier id = Identifier.parse(quest.targetId()); // VERIFY-MAPPING: Identifier.parse

        if (quest.type() == QuestType.KILL) {
            return BuiltInRegistries.ENTITY_TYPE.getValue(id).getDescription(); // VERIFY-MAPPING: EntityType#getDescription
        }

        return BuiltInRegistries.ITEM.getValue(id).getName(BuiltInRegistries.ITEM.getValue(id).getDefaultInstance());
    }
}
