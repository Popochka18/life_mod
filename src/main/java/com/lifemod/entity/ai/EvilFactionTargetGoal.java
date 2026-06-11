package com.lifemod.entity.ai;

import com.lifemod.entity.FactionMob;
import com.lifemod.faction.Alignment;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
// VERIFY-MAPPING: net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

/**
 * Targets the nearest member of an EVIL faction. Given to iron golems via mixin:
 * GOOD factions cannot fight themselves, so their golems guard the settlement
 * against bandit raids and other evil war parties.
 */
public class EvilFactionTargetGoal extends Goal {
    private static final double RANGE = 16.0;

    private final Mob mob;
    private LivingEntity candidate;

    public EvilFactionTargetGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (this.mob.getRandom().nextInt(10) != 0) {
            return false;
        }

        AABB box = this.mob.getBoundingBox().inflate(RANGE);
        // VERIFY-MAPPING: Level#getEntitiesOfClass
        List<Mob> nearby = this.mob.level().getEntitiesOfClass(Mob.class, box,
                other -> other != this.mob && other.isAlive()
                        && other instanceof FactionMob factionMob
                        && factionMob.getFaction().alignment() == Alignment.EVIL
                        && !factionMob.isEnslaved());

        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Mob other : nearby) {
            double distance = this.mob.distanceToSqr(other);

            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = other;
            }
        }

        this.candidate = nearest;
        return nearest != null;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.candidate);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.mob.getTarget();
        return target != null && target.isAlive() && this.mob.distanceToSqr(target) < RANGE * RANGE * 4;
    }
}
