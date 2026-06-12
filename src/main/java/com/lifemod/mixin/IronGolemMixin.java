package com.lifemod.mixin;

import com.lifemod.entity.ai.EvilFactionTargetGoal;

import net.minecraft.world.entity.EntityType;
// VERIFY-MAPPING: golem package — expect net.minecraft.world.entity.animal.golem.* after the
// per-mob subpackage restructure; on older layouts it is net.minecraft.world.entity.animal.*
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import net.minecraft.world.entity.animal.golem.IronGolem;
import net.minecraft.world.level.Level;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Iron golems guard settlements of GOOD factions (who cannot fight themselves):
 * they additionally target members of EVIL factions, e.g. raiding bandits.
 */
@Mixin(IronGolem.class)
public abstract class IronGolemMixin extends AbstractGolem {
    protected IronGolemMixin(EntityType<? extends AbstractGolem> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("TAIL")) // VERIFY-MAPPING: Mob#registerGoals
    private void lifemod$targetEvilFactions(CallbackInfo ci) {
        this.targetSelector.addGoal(3, new EvilFactionTargetGoal(this));
    }
}
