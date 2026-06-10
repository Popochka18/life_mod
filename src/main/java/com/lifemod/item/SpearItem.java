package com.lifemod.item;

import com.lifemod.entity.projectile.SpearEntity;
import com.lifemod.registry.ModEntities;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * A throwable spear (wooden/stone/iron/golden/diamond), ported from Diversity 1.7.2.
 * Melee stats come from the item properties; right-click throws it.
 */
public class SpearItem extends Item {
    private final double throwDamage;

    public SpearItem(double throwDamage, Properties properties) {
        super(properties);
        this.throwDamage = throwDamage;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide()) {
            SpearEntity spear = new SpearEntity(ModEntities.SPEAR, level);
            ItemStack thrown = stack.copy();
            thrown.setCount(1);
            spear.setSpear(thrown, this.throwDamage);
            spear.setOwner(player); // VERIFY-MAPPING: Projectile#setOwner
            spear.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ()); // VERIFY-MAPPING: Entity#getEyeY
            // VERIFY-MAPPING: Projectile#shootFromRotation
            spear.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.5F, 1.0F);
            level.addFreshEntity(spear);

            if (!player.getAbilities().instabuild) { // VERIFY-MAPPING: Player#getAbilities
                stack.shrink(1);
            }
        }

        return InteractionResult.SUCCESS;
    }
}
