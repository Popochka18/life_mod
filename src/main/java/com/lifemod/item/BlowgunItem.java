package com.lifemod.item;

import com.lifemod.entity.projectile.DartEntity;
import com.lifemod.registry.ModEntities;
import com.lifemod.registry.ModItems;
import com.lifemod.util.InventoryUtil;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

/** A blowgun firing poisoned darts, ported from Diversity 1.7.2. */
public class BlowgunItem extends Item {
    public BlowgunItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        boolean creative = player.getAbilities().instabuild; // VERIFY-MAPPING: Player#getAbilities

        if (!creative && InventoryUtil.count(player, ModItems.DART) == 0) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide()) {
            if (!creative) {
                InventoryUtil.consume(player, ModItems.DART, 1);
            }

            DartEntity dart = new DartEntity(ModEntities.DART, level);
            dart.setOwner(player); // VERIFY-MAPPING: Projectile#setOwner
            dart.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ()); // VERIFY-MAPPING: Entity#getEyeY
            // VERIFY-MAPPING: Projectile#shootFromRotation
            dart.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.8F, 1.0F);
            level.addFreshEntity(dart);
            player.getItemInHand(hand).hurtAndBreak(1, player, hand); // VERIFY-MAPPING: ItemStack#hurtAndBreak overload
        }

        return InteractionResult.SUCCESS;
    }
}
