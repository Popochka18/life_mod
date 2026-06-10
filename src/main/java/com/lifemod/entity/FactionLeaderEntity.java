package com.lifemod.entity;

import com.lifemod.faction.Faction;
import com.lifemod.faction.ReputationApi;
import com.lifemod.quest.QuestApi;
import com.lifemod.registry.ModItems;
import com.lifemod.util.InventoryUtil;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;

/**
 * The leader of a faction settlement (chief, pharaoh, ataman, captain...).
 * Gives quests, and at high reputation sells house deeds.
 */
public class FactionLeaderEntity extends FactionVillagerEntity {
    /** Price of a house deed in emeralds. */
    public static final int DEED_PRICE = 32;

    public FactionLeaderEntity(EntityType<? extends FactionLeaderEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected String pickProfession(Faction faction) {
        return faction.leaderProfession();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.PASS;
        }

        ItemStack held = player.getItemInHand(hand);

        // Buying a house: pay DEED_PRICE emeralds while sneaking, requires high reputation.
        if (player.isShiftKeyDown() && held.is(Items.EMERALD)) { // VERIFY-MAPPING: Player#isShiftKeyDown
            Faction faction = this.getFaction();

            if (!ReputationApi.canBuyHouse(serverPlayer, faction)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.deed.reputation",
                        ReputationApi.REP_BUY_HOUSE, Component.translatable(faction.translationKey())));
                return InteractionResult.SUCCESS;
            }

            if (!InventoryUtil.consume(player, Items.EMERALD, DEED_PRICE)) {
                serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.deed.price", DEED_PRICE));
                return InteractionResult.SUCCESS;
            }

            InventoryUtil.give(player, new ItemStack(ModItems.HOUSE_DEED));
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.deed.bought"));
            return InteractionResult.SUCCESS;
        }

        QuestApi.interact(serverPlayer, this.getFaction());
        return InteractionResult.SUCCESS;
    }
}
