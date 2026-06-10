package com.lifemod.entity;

import com.lifemod.entity.projectile.MusketBallEntity;
import com.lifemod.faction.Faction;
import com.lifemod.registry.ModEntities;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * A pirate armed with a musket. Kill one to claim the weapon
 * (see the musketeer loot table).
 */
public class MusketeerEntity extends PirateEntity {
    private static final int SHOT_INTERVAL_TICKS = 60;
    private static final double SHOT_RANGE = 20.0;

    public MusketeerEntity(EntityType<? extends MusketeerEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected String pickProfession(Faction faction) {
        return "musketeer";
    }

    @Override
    public void aiStep() { // VERIFY-MAPPING: Mob#aiStep
        super.aiStep();

        if (this.level().isClientSide() || this.tickCount % SHOT_INTERVAL_TICKS != 0) {
            return;
        }

        LivingEntity target = this.getTarget();

        if (target == null || !target.isAlive()
                || this.distanceToSqr(target) > SHOT_RANGE * SHOT_RANGE
                || !this.hasLineOfSight(target)) { // VERIFY-MAPPING: LivingEntity#hasLineOfSight
            return;
        }

        MusketBallEntity ball = new MusketBallEntity(ModEntities.MUSKET_BALL, this.level());
        ball.setOwner(this); // VERIFY-MAPPING: Projectile#setOwner
        ball.setPos(this.getX(), this.getEyeY() - 0.1, this.getZ()); // VERIFY-MAPPING: Entity#getEyeY
        double dx = target.getX() - this.getX();
        double dy = target.getY(0.5) - ball.getY(); // VERIFY-MAPPING: Entity#getY(double)
        double dz = target.getZ() - this.getZ();
        ball.shoot(dx, dy, dz, 3.0F, 4.0F); // VERIFY-MAPPING: Projectile#shoot
        this.level().addFreshEntity(ball);
        // VERIFY-MAPPING: SoundEvents constant may be a Holder; unwrap with .value() if needed
        this.playSound(SoundEvents.FIREWORK_ROCKET_BLAST, 2.0F, 0.7F);
    }
}
