package com.lifemod.entity.monster;

import com.lifemod.LifeModIds;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** A venomous black spider. */
public class DarkSpiderEntity extends AbstractHostileEntity {
    private static final Identifier TEXTURE = LifeModIds.id("textures/entity/monster/dark_spider.png");

    public DarkSpiderEntity(EntityType<? extends DarkSpiderEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.35)
                .add(Attributes.ATTACK_DAMAGE, 3.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) { // VERIFY-MAPPING: Mob#doHurtTarget signature
        boolean hurt = super.doHurtTarget(level, target);

        if (hurt && target instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0), this);
        }

        return hurt;
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
