package com.lifemod.entity;

import com.lifemod.faction.Faction;
import com.lifemod.tavern.RentState;
import com.lifemod.util.InventoryUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

/**
 * Tavern innkeeper: rents out the nearest free room (door) for emeralds.
 * Rent lasts three in-game days.
 */
public class InnkeeperEntity extends FactionVillagerEntity {
    public static final int RENT_PRICE = 5;
    public static final long RENT_DURATION = 3 * 24000L;
    private static final int SEARCH_RADIUS = 16;

    public InnkeeperEntity(EntityType<? extends InnkeeperEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected Faction defaultFaction(Holder<Biome> biome) {
        return Faction.VILLAGERS;
    }

    @Override
    protected String pickProfession(Faction faction) {
        return "innkeeper";
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!(player instanceof ServerPlayer serverPlayer) || !(this.level() instanceof ServerLevel serverLevel)) {
            return InteractionResult.PASS;
        }

        if (!player.getItemInHand(hand).is(Items.EMERALD)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.innkeeper.greeting", RENT_PRICE));
            return InteractionResult.SUCCESS;
        }

        RentState rent = RentState.get(serverLevel);
        long gameTime = serverLevel.getGameTime(); // VERIFY-MAPPING: Level#getGameTime
        BlockPos door = this.findFreeDoor(serverLevel, rent, gameTime);

        if (door == null) {
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.innkeeper.no_rooms"));
            return InteractionResult.SUCCESS;
        }

        if (!InventoryUtil.consume(player, Items.EMERALD, RENT_PRICE)) {
            serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.innkeeper.price", RENT_PRICE));
            return InteractionResult.SUCCESS;
        }

        long expiry = gameTime + RENT_DURATION;
        rent.rent(door, player.getUUID(), expiry);
        rent.rent(door.above(), player.getUUID(), expiry);
        serverPlayer.sendSystemMessage(Component.translatable("message.life-mod.innkeeper.rented",
                door.getX(), door.getY(), door.getZ()));
        return InteractionResult.SUCCESS;
    }

    private BlockPos findFreeDoor(ServerLevel level, RentState rent, long gameTime) {
        BlockPos origin = this.blockPosition();
        BlockPos nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-SEARCH_RADIUS, -4, -SEARCH_RADIUS),
                origin.offset(SEARCH_RADIUS, 4, SEARCH_RADIUS))) {
            if (!level.getBlockState(pos).is(BlockTags.DOORS)) { // VERIFY-MAPPING: BlockTags.DOORS
                continue;
            }

            // Only consider the lower half so each door is rented once.
            if (level.getBlockState(pos.below()).is(BlockTags.DOORS)) {
                continue;
            }

            if (!rent.isFree(pos, gameTime)) {
                continue;
            }

            double distance = pos.distSqr(origin); // VERIFY-MAPPING: BlockPos#distSqr

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = pos.immutable();
            }
        }

        return nearest;
    }
}
