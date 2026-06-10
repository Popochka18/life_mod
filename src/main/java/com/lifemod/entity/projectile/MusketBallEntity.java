package com.lifemod.entity.projectile;

import net.minecraft.world.entity.EntityType;
// VERIFY-MAPPING: net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** A heavy lead ball fired from a musket. Cannot be picked up again. */
public class MusketBallEntity extends AbstractArrow {
    public static final double DAMAGE = 14.0;

    public MusketBallEntity(EntityType<? extends MusketBallEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(DAMAGE); // VERIFY-MAPPING: AbstractArrow#setBaseDamage
        this.pickup = AbstractArrow.Pickup.DISALLOWED; // VERIFY-MAPPING: AbstractArrow.Pickup
    }

    @Override
    protected ItemStack getDefaultPickupItem() { // VERIFY-MAPPING: method name
        return ItemStack.EMPTY;
    }
}
