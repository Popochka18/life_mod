package com.lifemod.entity.monster;

import com.lifemod.LifeModIds;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** A huge ape-like monster that spawns exclusively in mountains. */
public class YetiEntity extends AbstractHostileEntity {
    private static final Identifier TEXTURE = LifeModIds.id("textures/entity/monster/yeti.png");

    public YetiEntity(EntityType<? extends YetiEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 60.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 9.0) // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8); // VERIFY-MAPPING: Attributes.KNOCKBACK_RESISTANCE
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
