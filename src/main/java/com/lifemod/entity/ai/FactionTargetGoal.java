package com.lifemod.entity.ai;

import com.lifemod.entity.FactionMob;
import com.lifemod.faction.Alignment;
import com.lifemod.faction.FactionRelations;
import com.lifemod.faction.WarState;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
// VERIFY-MAPPING: net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;

import java.util.EnumSet;
import java.util.List;

/**
 * Targets the nearest member of a hostile faction. Never targets players:
 * inter-faction wars are fought between NPCs only.
 */
public class FactionTargetGoal extends Goal {
    private static final double RANGE = 16.0;

    private final Mob mob;
    private LivingEntity candidate;

    public FactionTargetGoal(Mob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Goal.Flag.TARGET));
    }

    @Override
    public boolean canUse() {
        if (!(this.mob instanceof FactionMob factionMob)) {
            return false;
        }

        if (factionMob.getFaction().alignment() == Alignment.GOOD || factionMob.isEnslaved()) {
            return false;
        }

        if (this.mob.getRandom().nextInt(10) != 0) {
            return false;
        }

        if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
            return false;
        }

        WarState war = WarState.get(serverLevel.getServer());
        AABB box = this.mob.getBoundingBox().inflate(RANGE);

        // VERIFY-MAPPING: Level#getEntitiesOfClass
        List<Mob> nearby = serverLevel.getEntitiesOfClass(Mob.class, box,
                other -> other != this.mob && other.isAlive() && other instanceof FactionMob);

        LivingEntity nearest = null;
        double nearestDistance = Double.MAX_VALUE;

        for (Mob other : nearby) {
            FactionMob otherFaction = (FactionMob) other;

            if (!FactionRelations.hostile(factionMob.getFaction(), otherFaction.getFaction(), war)) {
                continue;
            }

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
