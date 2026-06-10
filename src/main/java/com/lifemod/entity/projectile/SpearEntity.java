package com.lifemod.entity.projectile;

import com.lifemod.registry.ModItems;

import net.minecraft.world.entity.EntityType;
// VERIFY-MAPPING: net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/** A thrown spear; lands and can be picked up again. Ported from Diversity 1.7.2. */
public class SpearEntity extends AbstractArrow {
    private ItemStack spearItem = ItemStack.EMPTY;

    public SpearEntity(EntityType<? extends SpearEntity> entityType, Level level) {
        super(entityType, level);
        this.setBaseDamage(6.0); // VERIFY-MAPPING: AbstractArrow#setBaseDamage
    }

    public void setSpear(ItemStack stack, double damage) {
        this.spearItem = stack.copy();
        this.setBaseDamage(damage);
    }

    @Override
    protected ItemStack getDefaultPickupItem() { // VERIFY-MAPPING: method name
        return this.spearItem.isEmpty() ? new ItemStack(ModItems.WOODEN_SPEAR) : this.spearItem.copy();
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) { // VERIFY-MAPPING: method name
        super.addAdditionalSaveData(output);

        if (!this.spearItem.isEmpty()) {
            output.store("LifeSpearItem", ItemStack.CODEC, this.spearItem);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) { // VERIFY-MAPPING: method name
        super.readAdditionalSaveData(input);
        this.spearItem = input.read("LifeSpearItem", ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }
}
