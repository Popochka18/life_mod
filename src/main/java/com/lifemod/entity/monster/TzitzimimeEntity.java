package com.lifemod.entity.monster;

import com.lifemod.LifeModIds;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

/** Aztec star demon haunting the jungle, ported from Diversity 1.7.2. */
public class TzitzimimeEntity extends AbstractHostileEntity {
    private static final Identifier TEXTURE = LifeModIds.id("textures/entity/monster/tzitzimime.png");

    public TzitzimimeEntity(EntityType<? extends TzitzimimeEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 35.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_DAMAGE, 6.0); // VERIFY-MAPPING: Attributes.ATTACK_DAMAGE
    }

    @Override
    public Identifier getTexture() {
        return TEXTURE;
    }
}
