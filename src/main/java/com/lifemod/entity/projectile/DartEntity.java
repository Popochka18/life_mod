package com.lifemod.entity.projectile;

import com.lifemod.registry.ModItems;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
// VERIFY-MAPPING: net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/** A blowgun dart that poisons its victim, ported from Diversity 1.7.2. */
public class DartEntity extends AbstractArrow {
    public DartEntity(EntityType<? extends DartEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(2.0); // VERIFY-MAPPING: AbstractArrow#setBaseDamage
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) { // VERIFY-MAPPING: method name
        super.onHitEntity(hitResult);

        if (hitResult.getEntity() instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 120, 0));
        }
    }

    @Override
    protected ItemStack getDefaultPickupItem() { // VERIFY-MAPPING: method name
        return new ItemStack(ModItems.DART);
    }
}
