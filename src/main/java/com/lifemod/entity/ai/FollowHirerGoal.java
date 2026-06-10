package com.lifemod.entity.ai;

import com.lifemod.entity.MercenaryEntity;

import net.minecraft.world.entity.player.Player;
// VERIFY-MAPPING: net.minecraft.world.entity.ai.goal.Goal
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

/** Makes a hired mercenary follow the player who hired it. */
public class FollowHirerGoal extends Goal {
    private static final double START_DISTANCE_SQR = 6.0 * 6.0;
    private static final double STOP_DISTANCE_SQR = 3.0 * 3.0;
    private static final double TELEPORT_DISTANCE_SQR = 32.0 * 32.0;

    private final MercenaryEntity mercenary;
    private final double speed;

    public FollowHirerGoal(MercenaryEntity mercenary, double speed) {
        this.mercenary = mercenary;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        Player hirer = this.mercenary.getHirer();
        return hirer != null && hirer.isAlive()
                && this.mercenary.distanceToSqr(hirer) > START_DISTANCE_SQR;
    }

    @Override
    public boolean canContinueToUse() {
        Player hirer = this.mercenary.getHirer();
        return hirer != null && hirer.isAlive()
                && this.mercenary.distanceToSqr(hirer) > STOP_DISTANCE_SQR;
    }

    @Override
    public void tick() {
        Player hirer = this.mercenary.getHirer();

        if (hirer == null) {
            return;
        }

        if (this.mercenary.distanceToSqr(hirer) > TELEPORT_DISTANCE_SQR) {
            this.mercenary.teleportTo(hirer.getX(), hirer.getY(), hirer.getZ()); // VERIFY-MAPPING: Entity#teleportTo
            return;
        }

        // VERIFY-MAPPING: Mob#getNavigation / PathNavigation#moveTo
        this.mercenary.getNavigation().moveTo(hirer, this.speed);
    }

    @Override
    public void stop() {
        this.mercenary.getNavigation().stop();
    }
}
