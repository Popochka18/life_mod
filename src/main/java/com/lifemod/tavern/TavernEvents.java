package com.lifemod.tavern;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;

/** Blocks other players from opening rented tavern rooms and bought houses. */
public final class TavernEvents {
    private TavernEvents() {
    }

    public static void init() {
        UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
            if (!(level instanceof ServerLevel serverLevel)) {
                return InteractionResult.PASS;
            }

            BlockPos pos = hitResult.getBlockPos();
            BlockState state = serverLevel.getBlockState(pos);

            if (!state.is(BlockTags.DOORS)) { // VERIFY-MAPPING: BlockTags.DOORS
                return InteractionResult.PASS;
            }

            RentState rent = RentState.get(serverLevel);
            long gameTime = serverLevel.getGameTime(); // VERIFY-MAPPING: Level#getGameTime

            // Doors occupy two blocks; check the clicked block and both vertical neighbours.
            for (BlockPos doorPos : new BlockPos[]{pos, pos.above(), pos.below()}) {
                if (serverLevel.getBlockState(doorPos).is(BlockTags.DOORS)
                        && !rent.mayUse(doorPos, player.getUUID(), gameTime)) {
                    player.sendSystemMessage(Component.translatable("message.life-mod.door.locked"));
                    return InteractionResult.FAIL;
                }
            }

            return InteractionResult.PASS;
        });
    }
}
