package com.lifemod.entity.monster;

import com.lifemod.LifeModIds;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Desert undead, ported from Diversity 1.7.2. */
public class MummyEntity extends AbstractHostileEntity {
    private static final Identifier TEXTURE = LifeModIds.id("textures/entity/monster/mummy.png");

    public MummyEntity(EntityType<? extends MummyEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.23)
                .add(Attributes.ATTACK_DAMAGE, 5.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
