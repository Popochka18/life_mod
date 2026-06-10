package com.lifemod.item;

import com.lifemod.tavern.RentState;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

/**
 * A house deed bought from a faction leader at high reputation.
 * Use it on a door to claim the house permanently.
 */
public class DeedItem extends Item {
    public DeedItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer(); // VERIFY-MAPPING: UseOnContext accessors

        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }

        BlockPos pos = context.getClickedPos();

        if (!level.getBlockState(pos).is(BlockTags.DOORS)) { // VERIFY-MAPPING: BlockTags.DOORS
            return InteractionResult.PASS;
        }

        RentState rent = RentState.get(level);
        long gameTime = level.getGameTime(); // VERIFY-MAPPING: Level#getGameTime

        if (!rent.isFree(pos, gameTime) && !rent.mayUse(pos, player.getUUID(), gameTime)) {
            player.sendSystemMessage(Component.translatable("message.life-mod.deed.occupied"));
            return InteractionResult.FAIL;
        }

        rent.rent(pos, player.getUUID(), RentState.PERMANENT);
        rent.rent(pos.above(), player.getUUID(), RentState.PERMANENT);
        rent.rent(pos.below(), player.getUUID(), RentState.PERMANENT);
        context.getItemInHand().shrink(1);
        player.sendSystemMessage(Component.translatable("message.life-mod.deed.claimed",
                pos.getX(), pos.getY(), pos.getZ()));
        return InteractionResult.SUCCESS;
    }
}
